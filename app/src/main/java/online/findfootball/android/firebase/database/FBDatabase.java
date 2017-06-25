package online.findfootball.android.firebase.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by WiskiW on 18.04.2017.
 */

public class FBDatabase {

    public final static String PATH_FOOTBALL_GAMES = "events/football/";
    public final static String PATH_USERS = "users/";

    public static DatabaseReference getDatabaseReference(DatabasePackableInterface packable) {
        // Возвращает пусть на струкрутру Firebase Database
        return FirebaseDatabase.getInstance().getReference().child(packable.getDirectoryPath());
    }
}
