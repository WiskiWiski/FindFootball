package org.blackstork.findfootball.firebase.database;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.time.TimeProvider;

/**
 * Created by WiskiW on 10.04.2017.
 */

public class FBUserDatabase {

    public static final String TAG = App.G_TAG + ":FBUserDatabase";

    private final static String USERS_PATH = "/users/";

    private DatabaseReference databaseReference;
    private Context context;
    private String uid;

    public FBUserDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FBUserDatabase newInstance(Context context, String uid) {
        FBUserDatabase database = new FBUserDatabase();
        database.setContext(context);
        database.setUid(uid);
        return database;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static void signUpUser(FirebaseUser user) {
        final DatabaseReference thisUserReference = FirebaseDatabase.getInstance().getReference()
                .child(USERS_PATH).child(user.getUid());
        thisUserReference.child("email").setValue(user.getEmail());
        thisUserReference.child("display_name").setValue(user.getDisplayName());
        Uri photoUri = user.getPhotoUrl();
        if (photoUri != null) {
            thisUserReference.child("photo_url").setValue(String.valueOf(photoUri));
        }

        thisUserReference.child("null_test").setValue(null);
        thisUserReference.child("register_time").setValue(TimeProvider.getUtcTime());
        thisUserReference.child("last_activity_time").setValue(TimeProvider.getUtcTime());
    }

    public void updateLastUserOnline() {
        final DatabaseReference thisUserReference = databaseReference.child(USERS_PATH).child(getUid());
        thisUserReference.child("last_activity_time").setValue(TimeProvider.getUtcTime());
    }

}
