package org.blackstork.findfootball.firebase.database;

import android.content.Context;
import android.location.Address;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.location.LocationHelper;
import org.blackstork.findfootball.objects.GameObj;

/**
 * Created by WiskiW on 06.04.2017.
 */

public class FBGameDatabase {

    public static final String TAG = App.G_TAG + ":FBGameDatabase";

    private final static String FOOTBALL_PATH = "/events/football/";

    private DatabaseReference databaseReference;
    private Context context;
    private String uid;

    public FBGameDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FBGameDatabase newInstance(Context context, String uid) {
        FBGameDatabase database = new FBGameDatabase();
        database.setContext(context);
        database.setUid(uid);
        return database;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void saveGame(final GameObj gameObj) {
        final DatabaseReference thisGameReference = databaseReference.child(FOOTBALL_PATH).child(gameObj.getEid());
        thisGameReference.child("owner").setValue(uid);
        thisGameReference.child("title").setValue(gameObj.getTitle());
        thisGameReference.child("description").setValue(gameObj.getDescription());
        thisGameReference.child("create_time").setValue(gameObj.getCreateTime());
        thisGameReference.child("event_time").setValue(gameObj.getEventTime());

        new Thread(new Runnable() {
            public void run() {
                thisGameReference.child("location").child("latitude").setValue(gameObj.getLocation().latitude);
                thisGameReference.child("location").child("longitude").setValue(gameObj.getLocation().longitude);

                Address address = LocationHelper.getStringAddress(context, gameObj.getLocation());
                if (address != null) {
                    String city = address.getLocality();
                    if (city == null) {
                        city = address.getSubAdminArea();
                    }
                    thisGameReference.child("location").child("city_name").setValue(city);

                    thisGameReference.child("location").child("country_name").setValue(address.getCountryName());
                }
            }
        }).start();

    }

    public GameObj readGame(String eid) {
/*
        Query phoneQuery = gamesReference.orderByChild("eid").equalTo("+923336091371");
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener()
         */
        return null;
    }

}
