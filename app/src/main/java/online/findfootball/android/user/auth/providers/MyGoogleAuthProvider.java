package online.findfootball.android.user.auth.providers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.user.auth.FailedResult;
import online.findfootball.android.user.auth.ProviderCallback;

/**
 * Created by WiskiW on 14.03.2017.
 */

public class MyGoogleAuthProvider extends RootAuthProvider {

    private static final String TAG = App.G_TAG + ":GoogleAuthProvider";
    private static final String PROVIDER = "google.com";
    private static final int PROVIDER_ID = 230;
    private static final int REQUEST_CODE = 3141;


    private ProviderCallback callback;
    private Activity activity;
    private GoogleApiClient mGoogleApiClient;

    public MyGoogleAuthProvider(Activity activity, ProviderCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void signIn() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.firebase_web_client_id))
                //.requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(((FragmentActivity) activity), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        FailedResult result = new FailedResult(101);
                        result.provider(getProvider());
                        result.providerId(getProviderId());
                        result.message("GoogleApiClient connection failed");
                        callback.onFailed(result);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        //signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(signInIntent, REQUEST_CODE);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            mGoogleApiClient.stopAutoManage((FragmentActivity) activity);
            mGoogleApiClient.disconnect();

            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (signInResult.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = signInResult.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance()
                        .signInWithCredential(credential)
                        .addOnCompleteListener(activity,
                                getOnCompleteListener(callback));
            } else {
                // Пользователь не дал согласия на вход через Google аккаунт
                FailedResult result = new FailedResult(signInResult.getStatus().getStatusCode());
                result.provider(getProvider());
                result.providerId(getProviderId());
                result.message(signInResult.getStatus().getStatusMessage());
                callback.onFailed(result);
            }
            return true;
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
