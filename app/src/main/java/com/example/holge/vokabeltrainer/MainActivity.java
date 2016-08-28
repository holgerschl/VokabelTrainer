package com.example.holge.vokabeltrainer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {

    private ArrayList<Vokabel> buecher = new ArrayList<>();
    private Model model;
    private TextView textView;
    private TextView textView2;
    private ProgressBar progressBar;
    private EditText editText;
    private Menu menu;
    private boolean latinToGerman = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(mTextEditorWatcher);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        InputStream inputStream = getResources().openRawResource(
                getResources().getIdentifier("vokabeln",
                        "raw", getPackageName()));

        model = new Model(inputStream);
        model.showLatein(textView, textView2, latinToGerman);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        switch (id) {
            case R.id.translation_direction:
                translationDirection(id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void translationDirection(int id) {
        MenuItem translation_direction = menu.findItem(id);
        if (translation_direction.getTitle().equals(getString(R.string.translation_direction1))) {
            translation_direction.setTitle(getString(R.string.translation_direction2));
            editText.setHint(getString(R.string.textHint2));
            latinToGerman = false;
            editText.setText("");
            progressBar.setProgress(0);
            model.showLatein(textView, textView2, latinToGerman);
        } else {
            translation_direction.setTitle(getString(R.string.translation_direction1));
            editText.setHint(getString(R.string.textHint1));
            latinToGerman = true;
            editText.setText("");
            progressBar.setProgress(0);
            model.showLatein(textView, textView2, latinToGerman);
        }
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String sString = s.toString();
            if (sString.contains(System.getProperty("line.separator"))) {
                model.showLatein(textView, textView2, latinToGerman);
                editText.setText("");
                progressBar.setProgress(0);
            }
            else{
                model.setProgressBar(progressBar, editText, latinToGerman);
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    public void onClick(View view) {
        model.showLatein(textView, textView2, latinToGerman);
        editText.setText("");
        progressBar.setProgress(0);
    }

    public void onClick2(View view) {
        model.showDeutsch(textView, textView2, latinToGerman);
    }

}
