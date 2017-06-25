package online.findfootball.android.user.auth.providers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import online.findfootball.android.app.App;
import online.findfootball.android.user.auth.FailedResult;
import online.findfootball.android.user.auth.ProviderCallback;

/**
 * Created by WiskiW on 14.03.2017.
 */

public class MyFacebookAuthProvider extends RootAuthProvider {

    private static final String TAG = App.G_TAG + ":FBAuthProvider";
    private static final String PROVIDER = "facebook.com";
    private static final int PROVIDER_ID = 229;

    //https://developers.facebook.com/docs/facebook-login/permissions
    private static final String[] scope = new String[]{"email", "public_profile", "user_friends", "user_location"};

    private ProviderCallback callback;
    private Activity activity;

    private CallbackManager fbCallbackManager;

    public MyFacebookAuthProvider(Activity activity, ProviderCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void signIn() {
        fbCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                FirebaseAuth.getInstance()
                        .signInWithCredential(credential)
                        .addOnCompleteListener(activity,
                                getOnCompleteListener(callback));
            }

            @Override
            public void onCancel() {
                FailedResult result = new FailedResult(103);
                result.provider(getProvider());
                result.providerId(getProviderId());
                result.message("Facebook login has been canceled");
                callback.onFailed(result);
            }

            @Override
            public void onError(FacebookException error) {
                FailedResult result = new FailedResult(102);
                result.provider(getProvider());
                result.providerId(getProviderId());
                result.message(error.getLocalizedMessage());
                callback.onFailed(result);
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList(scope));
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (fbCallbackManager != null) {
            // Pass the activity result back to the Facebook SDK
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
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

    @Override
    protected FailedResult onCompleteFailed(@NonNull Task<AuthResult> task) {
        LoginManager.getInstance().logOut(); // for Facebook
        return super.onCompleteFailed(task);
    }
}
