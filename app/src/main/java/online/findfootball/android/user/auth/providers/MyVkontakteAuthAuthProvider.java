package online.findfootball.android.user.auth.providers;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import online.findfootball.android.app.App;
import online.findfootball.android.user.auth.FailedResult;
import online.findfootball.android.user.auth.ProviderCallback;

/**
 * Created by WiskiW on 14.03.2017.
 */

public class MyVkontakteAuthAuthProvider extends RootAuthProvider {

    private static final String TAG = App.G_TAG + ":VKAuthProvider";
    private static final String PROVIDER = "vk.com";
    private static final int PROVIDER_ID = 231;

    private static final int REQUEST_CODE = 3143;

    //https://vk.com/dev/permissions
    private String[] scope = new String[]{VKScope.EMAIL, VKScope.PHOTOS, VKScope.OFFLINE, VKScope.FRIENDS};

    private Activity activity;
    private ProviderCallback callback;


    public MyVkontakteAuthAuthProvider(Activity activity, ProviderCallback callback) {
        this.callback = callback;
        this.activity = activity;
    }

    public void signIn() {
        VKSdk.login(activity, scope);
    }

    private void possessVKAccessToken(VKAccessToken vkAccessToken) {
        requestFirebaseToken(vkAccessToken);
        FirebaseAuth.getInstance()
                .signInWithCustomToken("generated token")
                .addOnCompleteListener(activity,
                        getOnCompleteListener(ProviderCallback.CODE_SIGN_IN, callback));

    }

    private void requestFirebaseToken(VKAccessToken vkAccessToken) {
        // TODO : Request for Oleg's NODE.js function to generate FirebaseToken
    }


    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
                @Override
                public void onResult(VKAccessToken res) {
                    // Пользователь успешно авторизовался
                    possessVKAccessToken(res);
                }

                @Override
                public void onError(VKError error) {
                    FailedResult result = new FailedResult(error.errorCode);
                    result.provider(getProvider());
                    result.providerId(getProviderId());
                    result.message(error.errorMessage);
                    callback.onFailed(result);
                }
            });

        }
        return false;
    }

    @Override
    protected String getProvider() {
        return PROVIDER;
    }

    @Override
    protected int getProviderId() {
        return PROVIDER_ID;
    }

}
