package org.blackstork.findfootball.firebase.database;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.time.TimeProvider;
import org.blackstork.findfootball.user.UserObj;

/**
 * Created by WiskiW on 10.04.2017.
 */

public class FBUserDatabase {

    public static final String TAG = App.G_TAG + ":FBUserDatabase";

    public final static String USERS_PATH = "/users/";

    public final static String KEY_DISPLAY_NAME = "display_name";
    public final static String KEY_EMAIL = "email";
    public final static String KEY_PHOTO_URL = "photo_url";
    public final static String KEY_REGISTER_TIME = "register_time";
    public final static String KEY_LAST_ACTIVITY_TIME = "last_activity_time";

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


    public static final String USER_ROLE_MEMBER = "member";
    public static final String USER_ROLE_OWNER = "owner";

    public void addFootballEvent(String eid, String role) {
        FirebaseDatabase.getInstance().getReference()
                .child(USERS_PATH).child(uid).child("events").child("football").child(eid).setValue(role);
    }

    public void removeFootballEvent(final FBCompleteListener callback, final String eid) {
        // Убирает игру из списка игр пользователя (не удаляет сам ивент!)
        //FirebaseDatabase.getInstance().getReference().child(USERS_PATH).child(uid).child("events").child("football").child(eid).removeValue();
        Log.d(TAG, "removeFootballEvent: eid: " + eid);
        final DatabaseReference event = FirebaseDatabase.getInstance().getReference()
                .child(USERS_PATH).child(uid).child("events").child("football").child(eid);
        event.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event.removeValue();
                if (dataSnapshot == null) {
                    if (callback != null) {
                        callback.onSuccess(null);
                    }
                } else {
                    if (callback != null) {
                        callback.onSuccess(dataSnapshot.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (callback != null)
                    callback.onFailed(1, databaseError.getMessage());
            }
        });
    }


    public static void signUpUser(FirebaseUser user) {
        final DatabaseReference thisUserReference = FirebaseDatabase.getInstance().getReference()
                .child(USERS_PATH).child(user.getUid());
        thisUserReference.child(KEY_EMAIL).setValue(user.getEmail());
        thisUserReference.child(KEY_DISPLAY_NAME).setValue(user.getDisplayName());
        Uri photoUri = user.getPhotoUrl();
        if (photoUri != null) {
            thisUserReference.child(KEY_PHOTO_URL).setValue(String.valueOf(photoUri));
        }

        thisUserReference.child(KEY_REGISTER_TIME).setValue(TimeProvider.getUtcTime());
        thisUserReference.child(KEY_LAST_ACTIVITY_TIME).setValue(TimeProvider.getUtcTime());
    }

    public void updateLastUserOnline() {
        final DatabaseReference thisUserReference = databaseReference.child(USERS_PATH).child(getUid());
        thisUserReference.child(KEY_LAST_ACTIVITY_TIME).setValue(TimeProvider.getUtcTime());
    }

    public static void loadUserByUid(final FBCompleteListener callback, String uid) {
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                .child(USERS_PATH).child(uid);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UserObj publicUser = new UserObj(dataSnapshot);
                    callback.onSuccess(publicUser);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailed(1, databaseError.getMessage());
            }
        });
    }

}
