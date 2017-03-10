package org.blackstork.findfootball.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.blackstork.findfootball.BuildConfig;
import org.blackstork.findfootball.app.App;

/**
 * Created by WiskiW on 10.03.2017.
 */

public class PreferencesStorage {

    private static final String TAG = App.G_TAG + ":PrefsStorage";

    private static SharedPreferences sharedpreferences = null;


    public static boolean getBoolean(Context c, String TAG, boolean dDefault) {
        getSharedPreferences(c);
        return sharedpreferences.getBoolean(TAG, dDefault);
    }

    public static void saveBoolean(Context c, String TAG, boolean toSet) {
        getSharedPreferences(c);
        sharedpreferences.edit()
                .putBoolean(TAG, toSet)
                .apply();
    }

    public static String getString(Context c, String TAG, String dDefault) {
        getSharedPreferences(c);
        return sharedpreferences.getString(TAG, dDefault);
    }

    public static void saveString(Context c, String TAG, String toSet) {
        getSharedPreferences(c);
        sharedpreferences.edit()
                .putString(TAG, toSet)
                .apply();
    }

    public static int getInt(Context c, String TAG, int dDefault) {
        getSharedPreferences(c);
        return sharedpreferences.getInt(TAG, dDefault);
    }

    public static void saveInt(Context c, String TAG, int toSet) {
        getSharedPreferences(c);
        sharedpreferences.edit()
                .putInt(TAG, toSet)
                .apply();
    }

    public static void reset(Context c) {
        getSharedPreferences(c);
        sharedpreferences.edit()
                .clear()
                .apply();
    }

    private static SharedPreferences getSharedPreferences(Context c) {
        if (sharedpreferences == null) {
            sharedpreferences = PreferenceManager.getDefaultSharedPreferences(c);
        }
        return sharedpreferences;
    }

    public static void syncRemoteConfig(final Context c) {
        /*
            Получаем настройки от Firebase Remote Config
            и сохраняем в локальные Preferences
         */
        try {
            final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            firebaseRemoteConfig.setConfigSettings(configSettings);

            long cacheExpiration = 3600; // 1 hour
            // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
            // the server.
            if (firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
                cacheExpiration = 0;
            }

            firebaseRemoteConfig.fetch(cacheExpiration)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Firebase Remote Config synchronization complete (Fetch Succeeded).");
                                // Once the config is successfully fetched it must be activated before newly fetched
                                // values are returned.
                                firebaseRemoteConfig.activateFetched();
                                getSharedPreferences(c);

                                /*
                                TODO : Fetch Firebase Preferences Here
                                sharedpreferences.edit()
                                        .putBoolean(LOCAL_PREF_TAG, firebaseRemoteConfig.getBoolean(REMOTE_PREF_TAG))
                                        .apply();
                                 */

                            } else {
                                Log.d(TAG, "Firebase Remote Config synchronization failed (Fetch failed).");
                            }
                        }
                    });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
