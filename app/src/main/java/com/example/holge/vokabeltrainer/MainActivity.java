package com.example.holge.vokabeltrainer;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
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

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

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

        setModel(new Model(inputStream, this));
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(getModel());
        getModel().showLatein(textView, textView2, isLatinToGerman());
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
            case R.id.action_settings:
                startActivity(new Intent(this, EinstellungenActivity.class));
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
            setLatinToGerman(false);
            editText.setText("");
            progressBar.setProgress(0);
            getModel().showLatein(textView, textView2, isLatinToGerman());
        } else {
            translation_direction.setTitle(getString(R.string.translation_direction1));
            editText.setHint(getString(R.string.textHint1));
            setLatinToGerman(true);
            editText.setText("");
            progressBar.setProgress(0);
            getModel().showLatein(textView, textView2, isLatinToGerman());
        }
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String sString = s.toString();
            if (sString.contains(System.getProperty("line.separator"))) {
                getModel().removeLearned();
                getModel().showLatein(textView, textView2, isLatinToGerman());
                editText.setText("");
                progressBar.setProgress(0);
            } else {
                getModel().setProgressBar(progressBar, editText, isLatinToGerman());
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    public void onClick(View view) {
        getModel().removeLearned();
        getModel().showLatein(textView, textView2, isLatinToGerman());
        editText.setText("");
        progressBar.setProgress(0);

    }

    public void onClick2(View view) {
        getModel().showDeutsch(textView, textView2, isLatinToGerman());
    }

    public void onClick3(View view) {
        getModel().saveJSON();
    }

    public boolean isLatinToGerman() {
        return latinToGerman;
    }

    public void setLatinToGerman(boolean latinToGerman) {
        this.latinToGerman = latinToGerman;
    }
}
