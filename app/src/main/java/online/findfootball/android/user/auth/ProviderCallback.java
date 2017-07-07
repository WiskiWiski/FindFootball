package online.findfootball.android.user.auth;

/**
 * Created by WiskiW on 14.03.2017.
 */

public interface ProviderCallback {

    void onResult(AuthUserObj user);

    void onFailed(FailedResult result);

}
