package com.raider.rssapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.raider.rssapp.R;
import com.raider.rssapp.classes.Preferences;
import com.raider.rssapp.database.DbInteraction;
import com.raider.rssapp.utils.Constants;
import com.raider.rssapp.utils.Util;

public class Settings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private CheckBox cbSpanish;
    private CheckBox cbEnglish;
    private CheckBox cbRotation;
    private Button btSave;
    private DbInteraction db;
    private Preferences preferences;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cbEnglish = (CheckBox) findViewById(R.id.cbEnglish);
        cbSpanish = (CheckBox) findViewById(R.id.cbSpanish);
        cbRotation = (CheckBox) findViewById(R.id.cbRotation);
        btSave = (Button) findViewById(R.id.btSavePreferences);

        db = new DbInteraction(getApplicationContext(), Constants.dbName, null, 1);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.updatePreference(preferences, language);
                Util.updateLocale(getBaseContext(), getApplicationContext());
                recreate();
            }
        });

        cbEnglish.setOnCheckedChangeListener(this);
        cbSpanish.setOnCheckedChangeListener(this);
        cbRotation.setOnCheckedChangeListener(this);

        checkPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPreferences();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (compoundButton.getId()) {

            case R.id.cbEnglish:

                if (cbEnglish.isChecked()) {
                    cbSpanish.setChecked(false);
                    preferences.setLanguage("english");
                    db.updatePreference(preferences, language);
                }
                break;

            case R.id.cbRotation:

                if (cbRotation.isChecked()) {
                    preferences.setRotation(1);
                } else {
                    preferences.setRotation(0);
                }
                break;

            case R.id.cbSpanish:

                if (cbSpanish.isChecked()) {
                    cbEnglish.setChecked(false);
                    preferences.setLanguage("spanish");
                }
                break;

            default:

                System.out.println("no funciona");
                break;
        }
    }

    public void checkPreferences() {

        preferences = db.getPreference();
        language = preferences.getLanguage();

        if (preferences.getLanguage().equalsIgnoreCase("english")) {

            cbEnglish.setChecked(true);
        } else {

            if (preferences.getLanguage().equalsIgnoreCase("spanish")) {

                cbSpanish.setChecked(true);
            }
        }

        if (preferences.getRotation() == 0) {

            cbRotation.setChecked(false);
        } else {

            if (preferences.getRotation() == 1) {

                cbRotation.setChecked(true);
            }
        }
    }
}
