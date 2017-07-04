package online.findfootball.android.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.UUID;

import online.findfootball.android.app.App;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.firebase.database.FBDatabase;
import online.findfootball.android.firebase.database.children.PackableObject;
import online.findfootball.android.game.chat.GameChatObj;
import online.findfootball.android.game.football.object.FootballTeamsObj;
import online.findfootball.android.location.LocationObj;
import online.findfootball.android.time.TimeProvider;
import online.findfootball.android.user.UserObj;

/**
 * Created by WiskiW on 02.04.2017.
 */

public class GameObj extends PackableObject {

    private static final String TAG = App.G_TAG + ":GameObj";

    public final static String PATH_TEAM_SIZE = "team_size";
    public final static String PATH_TITLE = "title";
    public final static String PATH_OWNER = "owner";
    public final static String PATH_DESCRIPTION = "description";
    public final static String PATH_CREATE_TIME = "create_time";
    public final static String PATH_EVENT_TIME = "event_time";


    private String eid;
    private UserObj ownerUser;
    private LocationObj location;
    private String title;
    private String description;
    private long eventTime;
    private long createTime;
    private GameChatObj chat;
    private FootballTeamsObj teams;

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
        this.chat = new GameChatObj();
        this.chat.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey());
    }

    public GameChatObj getChat() {
        if (this.chat == null) {
            newChat();
        }
        return this.chat;
    }

    public void setChat(GameChatObj chat) {
        this.chat = chat;
        this.chat.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey());
    }

    public FootballTeamsObj getTeams() {
        if (teams == null) {
            teams = new FootballTeamsObj();
            teams.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey());
        }
        return teams;
    }

    public void setTeams(FootballTeamsObj teams) {
        this.teams = teams;
        this.teams.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey());
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
            location.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey());
        }
        return location;
    }

    public void setLocation(LocationObj location) {
        this.location = location;
        this.location.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey());
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
        super.writeToParcel(out, flags);
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
        super(in);
        eid = in.readString();
        ownerUser = in.readParcelable(UserObj.class.getClassLoader());
        location = in.readParcelable(LocationObj.class.getClassLoader());
        teams = in.readParcelable(FootballTeamsObj.class.getClassLoader());
        chat = in.readParcelable(GameChatObj.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        eventTime = in.readLong();
        createTime = in.readLong();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(getClass() == obj.getClass())) {
            return false;
        }

        GameObj tmp = (GameObj) obj;
        return tmp.getEid().equals(this.getEid());
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
        if (getOwnerUser() == null || getOwnerUser() == UserObj.EMPTY) {
            Log.e(TAG, "Cannot save the game: owner uid is null");
            return new DataInstanceResult(DataInstanceResult.CODE_NOT_ENOUGH_DATA, "Owner uid is null");
        }

        HashMap<String, Object> teamsMap = new HashMap<>();
        getTeams().pack(teamsMap);
        databaseMap.put(getTeams().getPackableKey(), teamsMap);


        HashMap<String, Object> locationMap = new HashMap<>();
        getLocation().pack(locationMap);
        databaseMap.put(getLocation().getPackableKey(), locationMap);

        databaseMap.put(PATH_OWNER, getOwnerUser().getUid());
        databaseMap.put(PATH_TITLE, getTitle());
        databaseMap.put(PATH_DESCRIPTION, getDescription());
        databaseMap.put(PATH_CREATE_TIME, getCreateTime());
        databaseMap.put(PATH_EVENT_TIME, getEventTime());
        databaseMap.put(PATH_TEAM_SIZE, getTeams().getTeamsCapacity() / 2);

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
            getLocation().unpack(dataSnapshot.child(getLocation().getPackableKey()));

            Object uncheckedValue = dataSnapshot.child(PATH_EVENT_TIME).getValue();
            if (uncheckedValue != null) {
                setEventTime((Long) uncheckedValue);
            }

            uncheckedValue = dataSnapshot.child(PATH_CREATE_TIME).getValue();
            if (uncheckedValue != null) {
                setCreateTime((Long) uncheckedValue);
            }

            getTeams().unpack(dataSnapshot.child(getTeams().getPackableKey()));

            uncheckedValue = dataSnapshot.child(PATH_TEAM_SIZE).getValue();
            if (uncheckedValue != null) {
                getTeams().setTeamsCapacity(((Long) uncheckedValue).intValue());
            }


            return r;
        } catch (Exception ex) {
            Log.e(TAG, "unpack exception: ", ex);
            return DataInstanceResult.onFailed(ex.getMessage(), ex);
        }
    }

    @Override
    public DatabasePackable has(@NonNull DatabasePackable packable) {
        DatabasePackable tempPackable = null;
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
    public String getPackablePath() {
        return FBDatabase.PATH_FOOTBALL_GAMES;
    }

    @NonNull
    @Override
    public String getPackableKey() {
        return this.getEid();
    }

    @Override
    public void setPackableKey(String key) {
        this.setEid(key);
    }

}
