package org.blackstork.findfootball.firebase.database;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.blackstork.findfootball.time.TimeProvider;
import org.blackstork.findfootball.user.UserObj;

/**
 * Created by WiskiW on 18.04.2017.
 */

public class FBDatabase {

    public final static String PATH_FOOTBALL_GAMES = "/events/football/";
    public final static String PATH_USERS = "/users/";


    public static DatabaseReference getDatabaseReference(DatabaseInstance instance) {
        // Возвращает пусть на струкрутру Firebase Database
        return FirebaseDatabase.getInstance().getReference().child(instance.getDatabasePath());
    }

    public static void signUpUser(FirebaseUser user) {
        final DatabaseReference thisUserReference = FirebaseDatabase.getInstance().getReference()
                .child(PATH_USERS).child(user.getUid());
        thisUserReference.child(UserObj.PATH_EMAIL).setValue(user.getEmail());
        thisUserReference.child(UserObj.PATH_DISPLAY_NAME).setValue(user.getDisplayName());
        Uri photoUri = user.getPhotoUrl();
        if (photoUri != null) {
            thisUserReference.child(UserObj.PATH_PHOTO_URL).setValue(String.valueOf(photoUri));
        }

        thisUserReference.child(UserObj.PATH_REGISTER_TIME).setValue(TimeProvider.getUtcTime());
        thisUserReference.child(UserObj.PATH_LAST_ACTIVITY_TIME).setValue(TimeProvider.getUtcTime());
    }

}
