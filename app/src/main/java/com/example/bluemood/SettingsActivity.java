package com.example.bluemood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("theme", false)) {
            setTheme(R.style.Theme_BlueMood_Dark);
        } else {
            setTheme(R.style.Theme_BlueMood);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        settings.registerOnSharedPreferenceChangeListener(this);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        switch (key) {
            case "theme":
                boolean useAltTheme = sp.getBoolean("theme", false);
                if (useAltTheme) {
                    Toast.makeText(getBaseContext(), "Using alternative theme! Please restart see changes.", Toast.LENGTH_LONG).show();
//                    finish();
//                    startActivity(new Intent(this, SettingsActivity.class));
//                    getTheme().applyStyle(R.style.Theme_BlueMood_Dark, true);
//                    setContentView(R.layout.settings_activity);
                } else {
                    Toast.makeText(getBaseContext(), "Using default theme! Please restart see changes.", Toast.LENGTH_LONG).show();
//                    finish();
//                    startActivity(new Intent(this, SettingsActivity.class));
//                    getTheme().applyStyle(R.style.Theme_BlueMood, true);
//                    setContentView(R.layout.settings_activity);
                }
                break;
        }
    }
}