package com.example.holge.vokabeltrainer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by holge on 29.08.2016.
 * Einstellungen Activity
 */
public class EinstellungenActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Toast.makeText(this, R.string.settingStarted, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, R.string.backButton, Toast.LENGTH_SHORT).show();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new EinstellungenFragment()).commit();
    }

    public static class EinstellungenFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            MultiSelectListPreference listPreference = (MultiSelectListPreference) findPreference("preference");
            SharedPreferences sharedPreferences = listPreference.getSharedPreferences();
            Set<String> lessonsStr = sharedPreferences.getStringSet("lessons",new LinkedHashSet<String>());
            List<Integer> lessonsNo = new LinkedList<>();
            for(String lesson:lessonsStr)
            {
                lessonsNo.add(Integer.parseInt(lesson));
            }
            Collections.sort(lessonsNo);
            List<CharSequence> entriesList = new LinkedList<>();
            for (Integer lesson:lessonsNo)
            {
                entriesList.add(TextUtils.concat(getString(R.string.title_activity_einstellungen),": ",lesson.toString()));
            }
            CharSequence[] entries = entriesList.toArray(new CharSequence[entriesList.size()]);

            listPreference.setEntries(entries);
            listPreference.setEntryValues(entries);

            CharSequence[] entryValues = listPreference.getEntryValues();
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value){
        return false;
    }
}
