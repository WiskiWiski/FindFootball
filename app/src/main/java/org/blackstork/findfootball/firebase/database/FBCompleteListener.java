package org.blackstork.findfootball.firebase.database;

import org.blackstork.findfootball.objects.GameObj;

import java.util.List;

/**
 * Created by WiskiW on 12.04.2017.
 */

public interface FBCompleteListener {

    void onSuccess(Object object);

    void onFailed();

}
