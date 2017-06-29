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
    public static DatabaseReference getDatabaseReference(DatabaseSelfPackable packable) {
        String dirPath = packable.getDirectoryPath();
        DatabaseReference reference = getDatabaseReference();
        if (dirPath == null || dirPath.isEmpty()) {
            Log.w(TAG, "Packable dir path is null! Fix it.", new IOException("null dir path"));
            return reference.child("null_dir_path");
        } else {
            return reference.child(dirPath);
        }
    }

    // Возвращает пусть на струкрутру Firebase Database
    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
