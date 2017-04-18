package org.blackstork.findfootball.firebase.database;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.location.LocationObj;

/**
 * Created by WiskiW on 06.04.2017.
 */

public class FBFootballDatabase {

    public static final String TAG = App.G_TAG + ":FBFootballDatabase";

    public final static String FOOTBALL_PATH = "/events/football/";

    public final static String KEY_TITLE = "title";
    public final static String KEY_OWNER = "owner";
    public final static String KEY_DESCRIPTION = "description";
    public final static String KEY_CREATE_TIME = "create_time";
    public final static String KEY_EVENT_TIME = "event_time";
    public final static String KEY_LOCATION_LATITUDE = "/location/latitude/";
    public final static String KEY_LOCATION_LONGITUDE = "/location/longitude/";
    public final static String KEY_LOCATION_CITY_NAME = "/location/city_name/";
    public final static String KEY_LOCATION_COUNTRY_NAME = "/location/country_name/";
    public final static String KEY_PLAYER_COUNT = "/player_count/";
    public final static String KEY_PLAYERS = "/players/";
    public final static String KEY_A_TEAM = "/teams/team_b/";
    public final static String KEY_B_TEAM = "/teams/team_b/";

    private DatabaseReference databaseReference;
    private Context context;

    public FBFootballDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FBFootballDatabase newInstance(Context context) {
        FBFootballDatabase database = new FBFootballDatabase();
        database.setContext(context);
        return database;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public void saveGame(final GameObj gameObj, String uid) {
        final DatabaseReference thisGameReference = databaseReference.child(FOOTBALL_PATH).child(gameObj.getEid());
        thisGameReference.child(KEY_OWNER).setValue(uid);
        thisGameReference.child(KEY_TITLE).setValue(gameObj.getTitle());
        thisGameReference.child(KEY_DESCRIPTION).setValue(gameObj.getDescription());
        thisGameReference.child(KEY_CREATE_TIME).setValue(gameObj.getCreateTime());
        thisGameReference.child(KEY_EVENT_TIME).setValue(gameObj.getEventTime());

        thisGameReference.child(KEY_PLAYER_COUNT).setValue(gameObj.getPlayerList().getPlayersCount());
        thisGameReference.child(KEY_PLAYERS).setValue(gameObj.getPlayerList().getHashMap());
        //thisGameReference.child(KEY_PLAYERS).setValue(gameObj.getHashMap().getPlayerCount());
        //thisGameReference.child(KEY_A_TEAM).setValue(gameObj.getTeamAUids());
        //thisGameReference.child(KEY_B_TEAM).setValue(gameObj.getTeamBUids());

        thisGameReference.child(KEY_LOCATION_LATITUDE).setValue(gameObj.getLocation().getLatitude());
        thisGameReference.child(KEY_LOCATION_LONGITUDE).setValue(gameObj.getLocation().getLongitude());

        gameObj.getLocation().loadFullAddress(context, new LocationObj.LocationListener() {
            @Override
            public void onComplete(int resultCode, LocationObj location, String msg) {
                thisGameReference.child(KEY_LOCATION_CITY_NAME).setValue(location.getCityName());
                thisGameReference.child(KEY_LOCATION_COUNTRY_NAME).setValue(location.getCountryName());
                if (resultCode != 0) {
                    Log.w(TAG, "onComplete: loading string addresses problem, code: " + resultCode);
                    // TODO : load full location exceptions processor
                }
            }
        });
    }


    public void readGame(final FBCompleteListener callback, final String eid) {
        ValueEventListener referenceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gameSnapshot) {
                if (gameSnapshot.getValue() == null) {
                    callback.onSuccess(null);
                    return;
                }

                GameObj game = new GameObj(gameSnapshot);
                callback.onSuccess(game);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailed(1, databaseError.getMessage());
            }
        };
        DatabaseReference gameReference = databaseReference.child(FOOTBALL_PATH).child(eid);
        gameReference.addListenerForSingleValueEvent(referenceListener);
    }

    public GameObj readGame(String eid) {
/*
        Query phoneQuery = gamesReference.orderByChild("eid").equalTo("+923336091371");
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener()
         */
        return null;
    }

    public void deleteGame(String eid) {
        databaseReference.child(FOOTBALL_PATH).child(eid).removeValue();
    }
}
