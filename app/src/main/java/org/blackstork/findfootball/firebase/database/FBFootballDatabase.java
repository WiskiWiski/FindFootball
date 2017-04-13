package org.blackstork.findfootball.firebase.database;

import android.content.Context;
import android.location.Address;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.location.LocationHelper;
import org.blackstork.findfootball.objects.GameObj;

/**
 * Created by WiskiW on 06.04.2017.
 */

public class FBFootballDatabase {

    public static final String TAG = App.G_TAG + ":FBFootballDatabase";

    public final static String FOOTBALL_PATH = "/events/football/";

    private final static String KEY_TITLE = "title";
    private final static String KEY_OWNER = "owner";
    private final static String KEY_DESCRIPTION = "description";
    private final static String KEY_CREATE_TIME = "create_time";
    private final static String KEY_EVENT_TIME = "event_time";
    private final static String KEY_LOCATION_LATITUDE = "/location/latitude/";
    private final static String KEY_LOCATION_LONGITUDE = "/location/longitude/";
    private final static String KEY_LOCATION_CITY_NAME = "/location/city_name/";
    private final static String KEY_LOCATION_COUNTRY_NAME = "/location/country_name/";

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

        new Thread(new Runnable() {
            public void run() {
                thisGameReference.child(KEY_LOCATION_LATITUDE).setValue(gameObj.getLocation().latitude);
                thisGameReference.child(KEY_LOCATION_LONGITUDE).setValue(gameObj.getLocation().longitude);

                Address address = LocationHelper.getStringAddress(context, gameObj.getLocation());
                if (address != null) {
                    String city = address.getLocality();
                    if (city == null) {
                        city = address.getSubAdminArea();
                    }
                    thisGameReference.child(KEY_LOCATION_CITY_NAME).setValue(city);
                    thisGameReference.child(KEY_LOCATION_COUNTRY_NAME).setValue(address.getCountryName());
                }
            }
        }).start();
    }


    public void readGame(final FBCompleteListener callback, final String eid) {
        ValueEventListener referenceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gameSnapshot) {
                if (gameSnapshot.getValue() == null) {
                    callback.onSuccess(null);
                    return;
                }

                GameObj game = new GameObj();
                game.setEid(gameSnapshot.getKey());
                game.setTitle((String) gameSnapshot.child(KEY_TITLE).getValue());
                game.setDescription((String) gameSnapshot.child(KEY_DESCRIPTION).getValue());
                game.setEventTime((Long) gameSnapshot.child(KEY_EVENT_TIME).getValue());
                game.setCreateTime((Long) gameSnapshot.child(KEY_CREATE_TIME).getValue());

                double lat = (double) gameSnapshot.child(KEY_LOCATION_LATITUDE).getValue();
                double lng = (double) gameSnapshot.child(KEY_LOCATION_LONGITUDE).getValue();
                game.setLocation(new LatLng(lat, lng));

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
