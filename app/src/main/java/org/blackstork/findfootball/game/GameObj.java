package org.blackstork.findfootball.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import org.blackstork.findfootball.firebase.database.FBFootballDatabase;
import org.blackstork.findfootball.location.LocationObj;
import org.blackstork.findfootball.time.TimeProvider;

import java.util.LinkedHashSet;
import java.util.UUID;

/**
 * Created by WiskiW on 02.04.2017.
 */

public class GameObj implements Parcelable {

    private String eid;
    private String ownerUid;
    private LocationObj location;
    private String title;
    private String description;
    private long eventTime;
    private long createTime;

    private PlayerListObj playerList;
    //private int playerCount = 4;
    //private LinkedHashSet<String> teamAUids;
    //private LinkedHashSet<String> teamBUids;

    public GameObj() {
        createTime = TimeProvider.getUtcTime();
    }

    public GameObj(DataSnapshot gameSnapshot) {
        // TODO : Try-catch
        setEid(gameSnapshot.getKey());
        setTitle((String) gameSnapshot.child(FBFootballDatabase.KEY_TITLE).getValue());
        setDescription((String) gameSnapshot.child(FBFootballDatabase.KEY_DESCRIPTION).getValue());
        setOwnerUid((String) gameSnapshot.child(FBFootballDatabase.KEY_OWNER).getValue());
        setEventTime((Long) gameSnapshot.child(FBFootballDatabase.KEY_EVENT_TIME).getValue());
        setCreateTime((Long) gameSnapshot.child(FBFootballDatabase.KEY_CREATE_TIME).getValue());

        playerList = new PlayerListObj(gameSnapshot);
        /*
        setTeamAUids((LinkedHashSet<String>) gameSnapshot.child(FBFootballDatabase.KEY_A_TEAM).getValue());
        setTeamBUids((LinkedHashSet<String>) gameSnapshot.child(FBFootballDatabase.KEY_B_TEAM).getValue());
        */

        double lat = (double) gameSnapshot.child(FBFootballDatabase.KEY_LOCATION_LATITUDE).getValue(); // FIXME: java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.Double
        double lng = (double) gameSnapshot.child(FBFootballDatabase.KEY_LOCATION_LONGITUDE).getValue();
        setLocation(new LocationObj(lat, lng));
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


    public interface GameListener {
        void onComplete(int resultCode, GameObj game);
    }
}
