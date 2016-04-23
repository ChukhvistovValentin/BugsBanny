package com.example.valentin.bugsbanny;

import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

public class AppBugsBanny extends Application {
    private Locale locale = null;
    final String LOG_TAG = "myLogs";
    private SharedPreferences preferences;
    //    private Locale locale;
    private String lang;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (locale != null) {
//            newConfig.locale = locale;
//            Locale.setDefault(locale);
//            getBaseContext().getResources().updateConfiguration(newConfig,
//                    getBaseContext().getResources().getDisplayMetrics());
//        }
        super.onConfigurationChanged(newConfig);
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        lang = preferences.getString("lang", "uk");
        if (lang.equals("default")) {
            lang = getResources().getConfiguration().locale.getCountry();
        }
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        Log.d("myLogs", "Locale=" + locale);
        getBaseContext().getResources().updateConfiguration(config, null);

//        super.onCreate();
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean use_system = settings.getBoolean("checkboxLang", false);
//        String use_lang = settings.getString("PrefLanguage", "uk");
//        String lang = "uk";
//        if (!use_system) use_lang = lang;
//        Configuration config = getBaseContext().getResources().getConfiguration();
//
//        if (!"".equals(use_lang) && !config.locale.getLanguage().equals(use_lang)) {
//            locale = new Locale(use_lang);
//            Locale.setDefault(locale);
//            config.locale = locale;
//            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//        }
//
//        Log.d(LOG_TAG, use_lang + " ///***---");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (Constant.dbHelper != null)
            Constant.dbHelper.close();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(LOG_TAG, "--- onTerminate AppBugsBanny ---");
        if (Constant.dbHelper != null)
            Constant.dbHelper.close();
    }
}
