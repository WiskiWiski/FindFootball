package online.findfootball.android.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import online.findfootball.android.app.App;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseSelfPackable;
import online.findfootball.android.firebase.database.FBDatabase;
import online.findfootball.android.firebase.database.children.SelfPackableArrayList;
import online.findfootball.android.firebase.database.children.SelfPackableObject;
import online.findfootball.android.game.chat.MessageObj;
import online.findfootball.android.game.football.object.FootballTeams;
import online.findfootball.android.location.LocationObj;
import online.findfootball.android.time.TimeProvider;
import online.findfootball.android.user.UserObj;

/**
 * Created by WiskiW on 02.04.2017.
 */

public class GameObj extends SelfPackableObject implements Parcelable, Serializable {

    private static final String TAG = App.G_TAG + ":GameObj";

    public final static String PATH_TEAMS = "teams";
    public final static String PATH_TITLE = "title";
    public final static String PATH_OWNER = "owner";
    public final static String PATH_DESCRIPTION = "description";
    public final static String PATH_CREATE_TIME = "create_time";
    public final static String PATH_EVENT_TIME = "event_time";
    public final static String PATH_LOCATION = "location";
    public final static String PATH_CHAT = "chat";


    private String eid;
    private UserObj ownerUser;
    private LocationObj location;
    private String title;
    private String description;
    private long eventTime;
    private long createTime;
    private SelfPackableArrayList<MessageObj> chat;
    private FootballTeams teams;

    private void initObject() {
        createTime = TimeProvider.getUtcTime();
    }

    public GameObj() {
        initObject();
    }

    public GameObj(String eid) {
        initObject();
        this.eid = eid;
    }

    private void newChat() {
        this.chat = new SelfPackableArrayList<MessageObj>() {
            @Override
            protected MessageObj unpackItem(DataSnapshot dataSnapshot) {
                // используется только при вызове .load() на chat
                MessageObj msg = new MessageObj();
                msg.unpack(dataSnapshot);
                return msg;
            }
        };
        this.chat.setDirectoryPath(this.getDirectoryPath() + PATH_CHAT);
    }

    public SelfPackableArrayList<MessageObj> getChat() {
        if (this.chat == null) {
            newChat();
        }
        return this.chat;
    }

    public void setChat(SelfPackableArrayList<MessageObj> chat) {
        this.chat = chat;
    }

    public FootballTeams getTeams() {
        if (teams == null) {
            teams = new FootballTeams();
            teams.setDirectoryPath(getDirectoryPath() + PATH_TEAMS + "/");
        }
        return teams;
    }

    public void setTeams(FootballTeams teams) {
        this.teams = teams;
    }

    public UserObj getOwnerUser() {
        if (this.ownerUser == null) {
            return this.ownerUser = UserObj.EMPTY;
        }
        return this.ownerUser;
    }

    public void setOwnerUser(UserObj ownerUser) {
        this.ownerUser = ownerUser;
    }

    public LocationObj getLocation() {
        if (location == null) {
            location = new LocationObj();
            location.setDirectoryPath(getDirectoryPath() + PATH_LOCATION);
        }
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

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(eid);
        out.writeParcelable(ownerUser, flags);
        out.writeParcelable(location, flags);
        out.writeParcelable(teams, flags);
        out.writeParcelable(chat, flags);
        out.writeString(title);
        out.writeString(description);
        out.writeLong(eventTime);
        out.writeLong(createTime);
    }

    @Override
    public int describeContents() {
        return 0;
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
        ownerUser = in.readParcelable(UserObj.class.getClassLoader());
        location = in.readParcelable(LocationObj.class.getClassLoader());
        teams = in.readParcelable(FootballTeams.class.getClassLoader());
        chat = in.readParcelable(SelfPackableArrayList.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        eventTime = in.readLong();
        createTime = in.readLong();
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
        return 31 * this.getEid().hashCode();
    }

    @Override
    public String toString() {
        return "EventId:" + getEid();
    }

    @Override
    public boolean hasUnpacked() {
        return getLocation().hasUnpacked() && getTeams().hasUnpacked()
                && getChat().hasUnpacked() && getOwnerUser() != UserObj.EMPTY;
    }

    @NonNull
    @Override
    public DataInstanceResult pack(@NonNull HashMap<String, Object> databaseMap) {
        if (getOwnerUser() == null) {
            return new DataInstanceResult(DataInstanceResult.CODE_NOT_ENOUGH_DATA, "Owner uid is null");
        }
        HashMap<String, Object> tempMap = new HashMap<>();

        getTeams().pack(tempMap);
        databaseMap.put(PATH_TEAMS, tempMap);
        tempMap.clear();

        getLocation().pack(tempMap);
        databaseMap.put(PATH_LOCATION, tempMap);
        tempMap.clear();

        databaseMap.put(PATH_OWNER, getOwnerUser());
        databaseMap.put(PATH_TITLE, getTitle());
        databaseMap.put(PATH_DESCRIPTION, getDescription());
        databaseMap.put(PATH_CREATE_TIME, getCreateTime());
        databaseMap.put(PATH_EVENT_TIME, getEventTime());

        return DataInstanceResult.onSuccess();
    }

    @NonNull
    @Override
    public DataInstanceResult unpack(@NonNull DataSnapshot dataSnapshot) {
        DataInstanceResult r = DataInstanceResult.onSuccess();
        try {
            setEid(dataSnapshot.getKey());
            setTitle((String) dataSnapshot.child(PATH_TITLE).getValue());
            setDescription((String) dataSnapshot.child(PATH_DESCRIPTION).getValue());
            setOwnerUser(new UserObj((String) dataSnapshot.child(PATH_OWNER).getValue()));

            Object uncheckedLongValue = dataSnapshot.child(PATH_EVENT_TIME).getValue();
            if (uncheckedLongValue != null) {
                setEventTime((Long) uncheckedLongValue);
            }

            uncheckedLongValue = dataSnapshot.child(PATH_CREATE_TIME).getValue();
            if (uncheckedLongValue != null) {
                setCreateTime((Long) uncheckedLongValue);
            }

            getLocation().unpack(dataSnapshot.child(PATH_LOCATION));
            getTeams().unpack(dataSnapshot.child(PATH_TEAMS));

            return r;
        } catch (Exception ex) {
            Log.e(TAG, "unpack exception: ", ex);
            return DataInstanceResult.onFailed(ex.getMessage(), ex);
        }
    }

    @Override
    public DatabaseSelfPackable has(@NonNull DatabaseSelfPackable packable) {
        DatabaseSelfPackable tempPackable = null;
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    tempPackable = getLocation().has(packable);
                    break;
                case 1:
                    tempPackable = getTeams().has(packable);
                    break;
                case 2:
                    tempPackable = getChat().has(packable);
                    break;
            }
            if (tempPackable != null) {
                return tempPackable;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public String getDirectoryPath() {
        return FBDatabase.PATH_FOOTBALL_GAMES + getEid() + "/";
    }
}
