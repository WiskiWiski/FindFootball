package by.wiskiw.findfootball.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by WiskiW on 02.03.2017.
 */

public class App extends Application {

    public static final String G_TAG = "FFB"; //  Find Football
    public static final String INTENT_BUNBLE = "bundle"; //  Find Football

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
