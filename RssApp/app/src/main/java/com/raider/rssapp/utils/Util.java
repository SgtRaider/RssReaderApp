package com.raider.rssapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.widget.Toast;

import com.raider.rssapp.R;
import com.raider.rssapp.classes.Preferences;
import com.raider.rssapp.database.DbInteraction;

import java.util.Locale;

/**
 * Created by Raider on 10/05/16.
 */
public class Util {

    public static void toast(Context appContext, String texto) {
        Toast toastNotice = Toast.makeText(appContext, texto, Toast.LENGTH_SHORT);
        toastNotice.show();
    }

    public static void updateLocale(Context baseContext, Context appContext) {

        DbInteraction db = new DbInteraction(appContext, Constants.dbName, null, 1);
        Preferences preferences = db.getPreference();

        Locale locale = null;


        if (preferences.getLanguage().equalsIgnoreCase("english")) {

            locale = new Locale("en");
        } else {

            if (preferences.getLanguage().equalsIgnoreCase("spanish")) {

                locale = new Locale("es");
            }
        }

        if (locale != null) {

            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            baseContext.getResources().updateConfiguration(config,
                    baseContext.getResources().getDisplayMetrics());
        }
    }
}
