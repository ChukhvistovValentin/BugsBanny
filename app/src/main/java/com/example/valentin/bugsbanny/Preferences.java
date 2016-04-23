package com.example.valentin.bugsbanny;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Locale;

public class Preferences extends PreferenceActivity {
    //implements Preference.OnPreferenceChangeListener

    PendingIntent intent;
    ListPreference language;
    private Locale locale = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        // Get the custom preference
//        Preference customPref = (Preference) findPreference("customPref");
//        customPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            public boolean onPreferenceClick(Preference preference) {
//                Toast.makeText(getBaseContext(), "The custom preference has been clicked",
//                        Toast.LENGTH_LONG).show();
//                SharedPreferences customSharedPreference = getSharedPreferences(
//                        "myCustomSharedPrefs", Activity.MODE_PRIVATE);
//                SharedPreferences.Editor editor = customSharedPreference.edit();
////                editor.putString("myCustomPref", "The preference has been clicked");
//                editor.commit();
//                return true;
//            }
//        });
        Preference restart = (Preference) findPreference("restart");

        intent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getIntent()), 0);


        restart.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, intent);
                System.exit(1);
                return true;
            }
        });

//        language = (ListPreference) this.findPreference("PrefLanguage");
//        language.setOnPreferenceChangeListener(this);
    }


//    @Override
//    public boolean onPreferenceChange(Preference preference, Object newValue) {
//        Toast.makeText(this, R.string.msg_pref_lang, Toast.LENGTH_LONG).show();
//        Configuration config = getBaseContext().getResources().getConfiguration();
//        String lang = (String) newValue;
//        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
//            locale = new Locale(lang);
//            Locale.setDefault(locale);
//            config.locale = locale;
//            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//        }
////        preference.setSummary(getString(R.string.restart_app));
//        return true;
//    }
}

