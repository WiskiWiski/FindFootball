package org.blackstork.findfootball.firebase.database;

/**
 * Created by WiskiW on 12.04.2017.
 */

public interface FBCompleteListener {

    void onSuccess(Object object);

    void onFailed(int code, String msg);

}
