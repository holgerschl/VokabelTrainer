package com.example.holge.vokabeltrainer;

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
            int i = 0, randomNumber;
            sequence.add(random.nextInt(buecherLength));
            while (i < buecherLength - 1) {
                randomNumber = random.nextInt(buecherLength);
                boolean found = false;
                for (int j= 0; j < sequence.size(); j++ )
                {
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

    public void showLatein(TextView textView) {

        textView.setText(buecher.get(sequence.get(index)).getLatein());
        index++;
        if (index > buecherLength - 1) {
            index = 0;
            createSequence();
        }
    }
}
