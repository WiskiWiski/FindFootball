package online.findfootball.android.user.auth.providers;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import online.findfootball.android.app.App;
import online.findfootball.android.user.auth.AuthUserObj;
import online.findfootball.android.user.auth.FailedResult;
import online.findfootball.android.user.auth.ProviderCallback;
import online.findfootball.android.user.auth.signup.EmailSignUpActivity;

/**
 * Created by WiskiW on 12.03.2017.
 */

public class MyEmailAuthAuthProvider extends RootAuthProvider {

    private static final String TAG = App.G_TAG + ":EmailAuthProvider";
    private static final String PROVIDER = "email";
    private static final int PROVIDER_ID = 228;

    private ProviderCallback callback;
    private Activity activity;

    public MyEmailAuthAuthProvider(Activity activity, ProviderCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }


    public void signIn(String email, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getUser().isEmailVerified()) {
                                callback.onResult(onCompleteSuccess(task));
                            } else {
                                FailedResult failedResult = new FailedResult(69);
                                failedResult.message("Failed email verification").provider(getProvider());
                                callback.onFailed(failedResult);

                            }
                        } else {
                            callback.onFailed(onCompleteFailed(task));
                        }
                    }
                });
    }

    public void signUp(String email, String password) {
        AuthUserObj authUser = new AuthUserObj();
        authUser.setEmail(email);
        authUser.setPassword(password);

        Intent signUpIntent = new Intent(activity, EmailSignUpActivity.class);
        signUpIntent.putExtra(EmailSignUpActivity.EXTRA_KEY, authUser);
        activity.startActivityForResult(signUpIntent, EmailSignUpActivity.REQUEST_CODE);

    }


    @Override
    protected String getProvider() {
        return PROVIDER;
    }

    @Override
    protected int getProviderId() {
        return PROVIDER_ID;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != EmailSignUpActivity.REQUEST_CODE || data == null) {
            return;
        }
        AuthUserObj signUpUser = data.getParcelableExtra(EmailSignUpActivity.EXTRA_KEY);
        if (signUpUser == null || signUpUser.isEmpty()) {
            FailedResult result = new FailedResult(26);
            result.message("Empty/null AuthUser object")
                    .provider(getProvider())
                    .providerId(getProviderId());
            callback.onFailed(result);
        } else {
            callback.onResult(signUpUser);
        }
    }
}
