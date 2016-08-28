package com.example.holge.vokabeltrainer;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by holge on 27.08.2016.
 * Model class
 */
public class Model {

    private InputStream inputStream;
    private Random random;
    private int buecherLength;
    private ArrayList<Vokabel> buecher = new ArrayList<>();
    private ArrayList<Integer> sequence = new ArrayList<>();
    private int index = 0;

    public Model(InputStream inputStream) {
        this.inputStream = inputStream;
        random = new Random();
        loadVokabeln();
        createSequence();
    }

    private void createSequence() {
        {
            sequence.clear();
            int i = 0, randomNumber;
            sequence.add(random.nextInt(buecherLength));
            while (i < buecherLength - 1) {
                randomNumber = random.nextInt(buecherLength);
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

    private void loadVokabeln() {
        String latein, deutsch;
        int buch, lektion;
        String vokabeln = loadJSON();
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
                    buecher.add(new Vokabel(deutsch, latein, lektion, buch));
                }
            }
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
        buecherLength = buecher.size();
    }

    public void showLatein(TextView textView, TextView textView2, boolean latinToGerman) {
        textView2.setText("");
        if (latinToGerman) {
            textView.setText(buecher.get(sequence.get(index)).getLatein());
        } else {
            textView.setText(buecher.get(sequence.get(index)).getDeutsch());
        }
        index++;
        if (index >= buecherLength) {
            index = 0;
            createSequence();
        }
    }

    public void showDeutsch(TextView textView, TextView textView2, boolean latinToGerman) {
        if (index >0) {
            if (latinToGerman) {
                textView2.setText(buecher.get(sequence.get(index - 1)).getDeutsch());
            } else {
                textView2.setText(buecher.get(sequence.get(index - 1)).getLatein());
            }

        }
    }

    public void setProgressBar(ProgressBar progressBar, EditText editText, boolean latinToGerman) {
        if (index > 0) {
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
}
