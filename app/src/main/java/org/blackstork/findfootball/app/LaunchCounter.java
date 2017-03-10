package org.blackstork.findfootball.app;

import android.content.Context;
import android.util.Log;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.storage.PreferencesStorage;

/**
 * Created by WiskiW on 10.03.2017.
 */

public class LaunchCounter {

    private static final String TAG = App.G_TAG + ":LaunchCounter";

    private static int launchCounter;


    static int init(Context context) {
        String LAUNCH_COUNTER_TAG = context.getString(R.string.launch_counter);
        launchCounter = PreferencesStorage.getInt(context, LAUNCH_COUNTER_TAG, 0);
        launchCounter++;
        if (launchCounter == 1) {
            if (context instanceof OnFirstStartListener) {
                ((OnFirstStartListener) context).onFirstStart();
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFirstStartListener");
            }
        }
        PreferencesStorage.saveInt(context, LAUNCH_COUNTER_TAG, launchCounter);
        Log.i(TAG, "Initialising launch number " + launchCounter);
        return launchCounter;
    }

    public static int getLaunchNumb() {
        return launchCounter;
    }


    interface OnFirstStartListener {
        void onFirstStart();
    }

}
