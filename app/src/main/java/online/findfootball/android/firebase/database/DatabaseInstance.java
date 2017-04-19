package online.findfootball.android.firebase.database;

import android.content.Context;

/**
 * Created by WiskiW on 18.04.2017.
 */

public interface DatabaseInstance {

    int RESULT_SUCCESS = 0;
    int RESULT_FAILED_NULL_UID = 1;
    int RESULT_FAILED_NO_PERMISSIONS = 2;

    boolean hasLoaded();

    void load(OnLoadListener onLoadListener);

    boolean isLoading();

    void abortLoading();

    int save(Context context);

    String getDatabasePath();


    interface OnLoadListener {
        int FAILED_NULL_SNAPSHOT = 1;
        int FAILED_HAS_REMOVED = 2;
        int FAILED_LOADING_ABORTED = 3;

        String MSG_LOADING_ABORTED = "Loading has been aborted";

        void onSuccess(DatabaseInstance instance);

        void onFailed(int code, String msg);
    }

}
