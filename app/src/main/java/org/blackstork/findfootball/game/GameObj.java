package org.blackstork.findfootball.game;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.blackstork.findfootball.firebase.database.DatabaseInstance;
import org.blackstork.findfootball.firebase.database.FBDatabase;
import org.blackstork.findfootball.location.LocationObj;
import org.blackstork.findfootball.time.TimeProvider;

import java.util.UUID;

/**
 * Created by WiskiW on 02.04.2017.
 */

public class GameObj implements Parcelable, DatabaseInstance {


    public final static String PATH_TITLE = "title";
    public final static String PATH_OWNER = "owner";
    public final static String PATH_DESCRIPTION = "description";
    public final static String PATH_CREATE_TIME = "create_time";
    public final static String PATH_EVENT_TIME = "event_time";
    public final static String PATH_LOCATION_LATITUDE = "/location/latitude/";
    public final static String PATH_LOCATION_LONGITUDE = "/location/longitude/";
    public final static String PATH_LOCATION_CITY_NAME = "/location/city_name/";
    public final static String PATH_LOCATION_COUNTRY_NAME = "/location/country_name/";
    public final static String PATH_PLAYER_COUNT = "/player_count/";
    public final static String PATH_PLAYERS = "/players/";


    private String eid;
    private String ownerUid;
    private LocationObj location;
    private String title;
    private String description;
    private long eventTime;
    private long createTime;

    private PlayerListObj playerList;

    public GameObj() {
        createTime = TimeProvider.getUtcTime();
        playerList = new PlayerListObj(getEid());
    }

    public GameObj(DataSnapshot gameSnapshot) {
        // TODO : Try-catch
        setEid(gameSnapshot.getKey());
        setTitle((String) gameSnapshot.child(PATH_TITLE).getValue());
        setDescription((String) gameSnapshot.child(PATH_DESCRIPTION).getValue());
        setOwnerUid((String) gameSnapshot.child(PATH_OWNER).getValue());
        setEventTime((Long) gameSnapshot.child(PATH_EVENT_TIME).getValue());
        setCreateTime((Long) gameSnapshot.child(PATH_CREATE_TIME).getValue());

        playerList = new PlayerListObj(gameSnapshot);

        double lat = (Double) gameSnapshot.child(PATH_LOCATION_LATITUDE).getValue(); // FIXME: java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.Double
        double lng = (Double) gameSnapshot.child(PATH_LOCATION_LONGITUDE).getValue();
        setLocation(new LocationObj(lat, lng));
    }

    public GameObj(String eid) {
        this.eid = eid;
        playerList = new PlayerListObj(getEid());
    }

    public PlayerListObj getPlayerList() {
        return playerList;
    }

    public void setPlayerList(PlayerListObj playerList) {
        this.playerList = playerList;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public LocationObj getLocation() {
        return location;
    }

    public void setLocation(LocationObj location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEid() {
        if (eid == null) {
            eid = UUID.randomUUID().toString();
        }
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }


    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(eid);
        out.writeString(ownerUid);
        out.writeParcelable(location, flags);
        out.writeParcelable(playerList, flags);
        out.writeString(title);
        out.writeString(description);
        out.writeLong(eventTime);
        out.writeLong(createTime);

        // TODO :
        /*
        out.writeInt(playerCount);
        out.writeSerializable(teamAUids);
        out.writeSerializable(teamBUids);
         */
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<GameObj> CREATOR = new Parcelable.Creator<GameObj>() {
        public GameObj createFromParcel(Parcel in) {
            return new GameObj(in);
        }

        public GameObj[] newArray(int size) {
            return new GameObj[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private GameObj(Parcel in) {
        eid = in.readString();
        ownerUid = in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
        playerList = in.readParcelable(PlayerListObj.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        eventTime = in.readLong();
        createTime = in.readLong();

        // TODO:
        /*
        playerCount = in.readInt();
        teamAUids = (LinkedHashSet<String>) in.readSerializable();
        teamBUids = (LinkedHashSet<String>) in.readSerializable();
        */
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GameObj) {
            return (((GameObj) obj).getEid()).equals(this.getEid());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return this.getEid().hashCode();
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public void load(final OnLoadListener onLoadListener) {
        DatabaseReference thisGameReference = FBDatabase.getDatabaseReference(this);
        thisGameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gameSnapshot) {
                if (gameSnapshot == null) {
                    onLoadListener.onFailed(OnLoadListener.FAILED_NULL_SNAPSHOT, null);
                    return;
                }
                if (!gameSnapshot.hasChildren()) {
                    // ивент был удален из базы игр
                    onLoadListener.onFailed(OnLoadListener.FAILED_HAS_REMOVED, "Game has been delete");
                    return;
                }
                setEid(gameSnapshot.getKey());
                setTitle((String) gameSnapshot.child(PATH_TITLE).getValue());
                setDescription((String) gameSnapshot.child(PATH_DESCRIPTION).getValue());
                setOwnerUid((String) gameSnapshot.child(PATH_OWNER).getValue());
                setEventTime((Long) gameSnapshot.child(PATH_EVENT_TIME).getValue());
                setCreateTime((Long) gameSnapshot.child(PATH_CREATE_TIME).getValue());

                setPlayerList(new PlayerListObj(gameSnapshot));

                double lat = (double) gameSnapshot.child(PATH_LOCATION_LATITUDE).getValue(); // FIXME: java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.Double
                double lng = (double) gameSnapshot.child(PATH_LOCATION_LONGITUDE).getValue();
                setLocation(new LocationObj(lat, lng));
                onLoadListener.onSuccess(GameObj.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onLoadListener.onFailed(databaseError.getCode(), databaseError.getMessage());
            }
        });
    }

    @Override
    public int save(Context context) {
        if (getOwnerUid() == null) {
            return RESULT_FAILED_NULL_UID;
        }
        final DatabaseReference thisGameReference = FBDatabase.getDatabaseReference(this);
        thisGameReference.child(PATH_OWNER).setValue(getOwnerUid());
        thisGameReference.child(PATH_TITLE).setValue(getTitle());
        thisGameReference.child(PATH_DESCRIPTION).setValue(getDescription());
        thisGameReference.child(PATH_CREATE_TIME).setValue(getCreateTime());
        thisGameReference.child(PATH_EVENT_TIME).setValue(getEventTime());

        thisGameReference.child(PATH_PLAYER_COUNT).setValue(getPlayerList().getPlayersCount());
        thisGameReference.child(PATH_PLAYERS).setValue(getPlayerList().getHashMap());

        thisGameReference.child(PATH_LOCATION_LATITUDE).setValue(getLocation().getLatitude());
        thisGameReference.child(PATH_LOCATION_LONGITUDE).setValue(getLocation().getLongitude());

        getLocation().loadFullAddress(context, new LocationObj.LocationListener() {
            @Override
            public void onComplete(int resultCode, LocationObj location, String msg) {
                thisGameReference.child(PATH_LOCATION_CITY_NAME).setValue(location.getCityName());
                thisGameReference.child(PATH_LOCATION_COUNTRY_NAME).setValue(location.getCountryName());
                if (resultCode != 0) {
                    //Log.w(TAG, "onComplete: loading string addresses problem, code: " + resultCode);
                    // TODO : load full location exceptions processor
                }
            }
        });

        return RESULT_SUCCESS;
    }

    public void delete() {
        FBDatabase.getDatabaseReference(this).setValue(null);
    }


    @Override
    public String getDatabasePath() {
        return FBDatabase.PATH_FOOTBALL_GAMES + getEid() + "/";
    }
}
