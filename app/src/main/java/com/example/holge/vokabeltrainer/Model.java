package com.example.holge.vokabeltrainer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Created by holge on 27.08.2016.
 * Model class
 */
public class Model implements SharedPreferences.OnSharedPreferenceChangeListener {

    private InputStream inputStream;
    private Random random;
    private List<Vokabel> buecher = new ArrayList<>();
    private List<Integer> sequence = new ArrayList<>();
    private int index = 0;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private String vokabeln;

    public Model(InputStream inputStream, Activity activity) {
        this.inputStream = inputStream;
        this.activity = activity;
        random = new Random();
        vokabeln = loadJSON();
        lessonList();
        buecher = loadVokabeln(false);
        createSequence();
    }

    public void lessonList() {
        Set<String> lessons = new LinkedHashSet<>();
        List<Vokabel> localBuecher;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        localBuecher = loadVokabeln(true);
        for (Vokabel vokabel : localBuecher) {
            if (!lessons.contains(vokabel.getLektion().toString())) {
                lessons.add(vokabel.getLektion().toString());
            }
        }
//        return lessons.toArray(new CharSequence[lessons.size()]);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("lessons", lessons);
        editor.apply();
    }


    private void createSequence() {
        {
            sequence.clear();
            int i = 0, randomNumber;
            if (buecher.size()>0) {
                sequence.add(random.nextInt(buecher.size()));
                while (i < buecher.size() - 1) {
                    randomNumber = random.nextInt(buecher.size());
                    boolean found = false;
                    for (int j = 0; j < sequence.size(); j++) {
                        if (sequence.get(j) == randomNumber)
                            found = true;
                    }
                    if (!found) {
                        sequence.add(randomNumber);
                        i++;
                    }
                }
            }
        }
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

    private List<Vokabel> loadVokabeln(boolean all) {
        List<Vokabel> localBuecher = new ArrayList<>();
        String latein, deutsch;
        Integer buch, lektion;
        try {
            JSONArray json;
            JSONObject test = null;
            json = (JSONArray) new JSONTokener(vokabeln).nextValue();
            if (json != null) {
                int len = json.length();
                for (int i = 0; i < len; i++) {
                    latein = json.optJSONObject(i).getString("Latein");
                    deutsch = json.optJSONObject(i).getString("Deutsch");
                    buch = json.optJSONObject(i).getInt("Buch");
                    lektion = json.optJSONObject(i).getInt("Lektion");
                    if (checkLesson(lektion)||(all))
                        localBuecher.add(new Vokabel(deutsch, latein, lektion, buch));
                }
            }
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
        return localBuecher;
    }

    private boolean checkLesson(Integer lektion) {
        Set<String> lessons = sharedPreferences.getStringSet("preference", new LinkedHashSet<String>());
        String lesson = (String) TextUtils.concat(activity.getString(R.string.title_activity_einstellungen), ": ", lektion.toString());
        return lessons.contains(lesson);
    }

    public void showLatein(TextView textView, TextView textView2, boolean latinToGerman) {
        textView2.setText("");
        if (buecher.size()>0) {
            if (latinToGerman) {
                textView.setText(buecher.get(sequence.get(index)).getLatein());
            } else {
                textView.setText(buecher.get(sequence.get(index)).getDeutsch());
            }
            index++;
            if (index >= buecher.size()) {
                index = 0;
                createSequence();
            }
        }
    }

    public void showDeutsch(TextView textView, TextView textView2, boolean latinToGerman) {
        if (index > 0) {
            if (latinToGerman) {
                textView2.setText(buecher.get(sequence.get(index - 1)).getDeutsch());
            } else {
                textView2.setText(buecher.get(sequence.get(index - 1)).getLatein());
            }

        }
    }

    public void setProgressBar(ProgressBar progressBar, EditText editText, boolean latinToGerman) {
        if (index > 0 && buecher.size() > 0) {
            String deutsch;
            int count;
            if (latinToGerman) {
                deutsch = buecher.get(sequence.get(index - 1)).getDeutsch().toString().toLowerCase(Locale.GERMANY);
            } else {
                deutsch = buecher.get(sequence.get(index - 1)).getLatein().toString().toLowerCase(Locale.GERMANY);
            }
            String[] bedeutungen = deutsch.split("[^a-zA-ZäöüßÄÖÜ]");
            String versuch = editText.getText().toString().toLowerCase(Locale.GERMANY);
            String[] versuche = versuch.split("[^a-zA-ZäöüßÄÖÜ]");
            List<String> bedeutungenList = new LinkedList<>(Arrays.asList(bedeutungen));
            List<String> empty = new LinkedList<>();
            empty.add("");
            bedeutungenList.removeAll(empty);
            List<String> versucheList = new LinkedList<>(Arrays.asList(versuche));
            progressBar.setMax(bedeutungenList.size());
            count = countMatches(bedeutungenList, versucheList);
            progressBar.setProgress(count);
        }
/*
        CharSequence text = "";
        for (int i = 0; i < bedeutungen.length;i++) {
            text= TextUtils.concat( text," ", (CharSequence)bedeutungen[i]);
        }
        textView.setText(text);
*/

    }

    private int countMatches(List<String> bedeutungenList, List<String> versucheList) {
        int count = 0;
        List<String> toBeRemoved = new LinkedList<>();
        while (versucheList.size() > 0) {
            for (String versuch : versucheList) {
                boolean found = false;
                for (String bedeutung : bedeutungenList) {
                    if (versuch.equals(bedeutung)) {
                        count++;
                        found = true;
                        toBeRemoved.add(versuch);
                    }
                }
                if (!found) {
                    toBeRemoved.add(versuch);
                }
            }
            versucheList.removeAll(toBeRemoved);
        }

        return count;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        buecher = loadVokabeln(false);
        createSequence();
        activity.recreate();
    }
}
