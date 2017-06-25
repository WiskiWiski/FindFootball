package online.findfootball.android.user.auth;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by WiskiW on 14.03.2017.
 */

public interface ProviderCallback {

    void onResult(FirebaseUser user);

    void onFailed(FailedResult result);

}
