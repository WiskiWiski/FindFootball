package org.blackstork.findfootball.user;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

import org.blackstork.findfootball.user.auth.UserAuth;

/**
 * Created by WiskiW on 17.04.2017.
 */

public class AppUser extends UserObj {


    public AppUser() {
    }

    public AppUser(String uid) {
        super(uid);
    }

    public AppUser(FirebaseUser firebaseUser) {
        super(firebaseUser);
    }

    public static AppUser getInstance(Context context) {
        return getInstance(context, true, null);
    }

    public static AppUser getInstance(Context context, boolean requestAuth) {
        return getInstance(context, requestAuth, null);
    }

    public static AppUser getInstance(Context context, boolean requestAuth, String singInMsg) {
        FirebaseUser firebaseUser = UserAuth.getUser(context);
        if (requestAuth && firebaseUser == null) {
            UserAuth.requestUser(context, singInMsg);
            return null;
        } else {
            return new AppUser(firebaseUser);
        }
    }

    public static void signOut(){
        UserAuth.signOut();
    }


}
