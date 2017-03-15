package org.blackstork.findfootball.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vk.sdk.VKSdk;

import org.blackstork.findfootball.app.App;

/**
 * Created by WiskiW on 11.03.2017.
 */

public class UserAuth {

    private static final String TAG = App.G_TAG + ":UserAuth";

    static final String NEXT_ACTIVITY_INTENT = "next_activity";

    private Activity activity;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;


    private FirebaseAuth.AuthStateListener mAuthListener;

    public static UserAuth getInstance(Activity activity) {
        return new UserAuth(activity);
    }


    private UserAuth(Activity activity) {
        this.activity = activity;
        // TODO : http://stackoverflow.com/questions/38345085/firebase-authentication-state-change-does-not-fire-when-user-is-disabled-or-dele
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged: ===================================");
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                    Log.d(TAG, "onAuthStateChanged: user name: " + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged: user email: " + user.getEmail());
                    Log.d(TAG, "onAuthStateChanged: user provider: " + user.getProviderId());
                    Log.d(TAG, "onAuthStateChanged: user photo: " + user.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged: providers: " + user.getProviders());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    public FirebaseUser getUser() {
        user = mFirebaseAuth.getCurrentUser();
        return user;
    }

    public void requestAuth(Intent activityIntent) {
        Intent intent = new Intent(activity, AuthUiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putParcelable(NEXT_ACTIVITY_INTENT, activityIntent);
        intent.putExtra(App.INTENT_BUNDLE, bundle);

        activity.startActivity(intent);
        activity.finish();
    }

    public boolean requestUser(Intent activityIntent) {
        if (getUser() != null) return true;
        Intent intent = new Intent(activity, AuthUiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putParcelable(NEXT_ACTIVITY_INTENT, activityIntent);
        intent.putExtra(App.INTENT_BUNDLE, bundle);

        activity.startActivity(intent);
        activity.finish();
        return false;
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut(); // for Facebook
        VKSdk.logout(); // VK
    }

    public void onStart() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

}
