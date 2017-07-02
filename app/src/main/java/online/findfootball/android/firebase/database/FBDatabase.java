package online.findfootball.android.firebase.database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import online.findfootball.android.app.App;

/**
 * Created by WiskiW on 18.04.2017.
 */

public class FBDatabase {

    private final static String TAG = App.G_TAG + ":FBDatabase";

    public final static String PATH_FOOTBALL_GAMES = "events/football/";
    public final static String PATH_USERS = "users/";

    // Возвращает пусть на струкрутру Firebase Database
    public static DatabaseReference getDatabaseReference(DatabasePackable packable) {
        String path = packable.getPackablePath();
        String key = packable.getPackableKey();
        DatabaseReference reference = getDatabaseReference();
        if (path == null || path.isEmpty()) {
            Log.w(TAG, "Packable path is null! Fix it.", new IOException("null packable path"));
            return reference.child("null_packable_path");
        } else if (key == null || key.isEmpty()) {
            Log.w(TAG, "Packable key is null! Fix it.", new IOException("null packable key"));
            return reference.child("null_packable_key");
        } else {
            return reference.child(path).child(key);
        }
    }

    // Возвращает пусть на струкрутру Firebase Database
    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
