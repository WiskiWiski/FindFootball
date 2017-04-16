package org.blackstork.findfootball.user.auth.providers;

import com.google.firebase.auth.FirebaseAuth;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.user.auth.ProviderCallback;

/**
 * Created by WiskiW on 12.03.2017.
 */

public class MyEmailAuthAuthProvider extends RootAuthProvider {

    private static final String TAG = App.G_TAG + ":EmailAuthProvider";
    private static final String PROVIDER = "email";
    private static final int PROVIDER_ID = 228;

    private ProviderCallback callback;

    public MyEmailAuthAuthProvider(ProviderCallback callback) {
        this.callback = callback;
    }

    public void signIn(String email, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getOnCompleteListener(callback));
    }

    public void signUp(String email, String password) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getOnCompleteListener(callback));

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
