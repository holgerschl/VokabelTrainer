package com.example.holge.vokabeltrainer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.Preference;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by holge on 30.08.2016.
 * Preference model
 */
public class PreferenceModel implements Preference.OnPreferenceClickListener {

    private SharedPreferences sharedPreferences;
    private InputStream inputStream;
    private Activity activity;

    public PreferenceModel(Activity activity, SharedPreferences sharedPreferences, InputStream inputStream)
    {
        this.activity = activity;
        this.sharedPreferences = sharedPreferences;
        this.inputStream = inputStream;
    }

    private String loadJSON() {

        String json = "";
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        try {
            json = r.readLine();
            r.close();
            inputStream.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return json;
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        // do stuff
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("vokabelnUnlearned",loadJSON());
        editor.apply();
        Runtime.getRuntime().exit(0);
        return true;
    }

}
