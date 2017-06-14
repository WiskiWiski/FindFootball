package online.findfootball.android.user.auth;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by WiskiW on 14.03.2017.
 */

public interface ProviderCallback {

    int CODE_SIGN_IN = 1;
    int CODE_SIGN_UP = 2;

    void onResult(int code, FirebaseUser user);

    void onFailed(FailedResult result);

}
