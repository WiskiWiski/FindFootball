package org.blackstork.findfootball.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.blackstork.findfootball.storage.PreferencesStorage;

/**
 * Created by WiskiW on 02.03.2017.
 */

public class App extends Application implements LaunchCounter.OnFirstStartListener {

    public static final String G_TAG = "FFB"; //  Find Football
    public static final String INTENT_BUNDLE = "bundle";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LaunchCounter.init(this);
        PreferencesStorage.syncRemoteConfig(this);



    }


    @Override
    public void onFirstStart() {
        // First start stuff here


    }

}
