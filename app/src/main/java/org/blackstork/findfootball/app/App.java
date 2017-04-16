package org.blackstork.findfootball.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import org.blackstork.findfootball.BuildConfig;
import org.blackstork.findfootball.R;
import org.blackstork.findfootball.user.auth.UserAuth;
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

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                // VKAccessToken is invalid
                // Если AccessToken стал невалиден (например, пользователь сменил пароль)
                UserAuth.signOut();
                // TODO : https://vk.com/dev/android_sdk
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LaunchCounter.init(this);
        PreferencesStorage.syncRemoteConfig(this);

        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
        UserAuth.checkForAccountAvailability(this);

    }


    @Override
    public void onFirstStart() {
        // First start stuff here
        saveInstallVersion(this);

    }

    private void saveInstallVersion(Context context) {
        String INSTALL_VERSION_TAG = context.getString(R.string.install_version);
        PreferencesStorage.saveInt(context, INSTALL_VERSION_TAG, BuildConfig.VERSION_CODE);
    }

    public static int getInstallVersion(Context context) {
        String INSTALL_VERSION_TAG = context.getString(R.string.install_version);
        return PreferencesStorage.getInt(context, INSTALL_VERSION_TAG, 1);
    }

}
