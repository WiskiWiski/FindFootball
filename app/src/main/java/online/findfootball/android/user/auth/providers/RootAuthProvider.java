package online.findfootball.android.user.auth.providers;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import online.findfootball.android.app.App;
import online.findfootball.android.user.auth.AuthUserObj;
import online.findfootball.android.user.auth.FailedResult;
import online.findfootball.android.user.auth.ProviderCallback;

/**
 * Created by WiskiW on 14.03.2017.
 */

abstract class RootAuthProvider {

    private static final String TAG = App.G_TAG + ":RootAuthProvider";

    protected abstract String getProvider();

    protected abstract int getProviderId();

    protected AuthUserObj onCompleteSuccess(@NonNull Task<AuthResult> task) {
        return new AuthUserObj(task.getResult().getUser());
    }

    protected FailedResult onCompleteFailed(@NonNull Task<AuthResult> task) {
        // TODO : Override this for all child's
        FailedResult result = new FailedResult(1);
        result.message(task.getException().getLocalizedMessage());
        result.providerId(getProviderId());
        result.provider(getProvider());
        return result;
    }

    OnCompleteListener<AuthResult> getOnCompleteListener(final ProviderCallback callback) {
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    callback.onResult(onCompleteSuccess(task));
                } else {
                    callback.onFailed(onCompleteFailed(task));
                }
            }
        };
    }

}
