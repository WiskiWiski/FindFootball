package online.findfootball.android.user.auth.providers;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import online.findfootball.android.app.App;
import online.findfootball.android.user.auth.AuthUiActivity;
import online.findfootball.android.user.auth.FailedResult;
import online.findfootball.android.user.auth.ProviderCallback;

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
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "Successful sent verification" + task.isSuccessful());

                                        }
                                    });
                            FailedResult failedResult = new FailedResult(25)
                                    .message("Check your email please!")
                                    .provider(getProvider());
                            callback.onFailed(failedResult);
                        } else callback.onFailed(onCompleteFailed(task));
                    }
                });
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
