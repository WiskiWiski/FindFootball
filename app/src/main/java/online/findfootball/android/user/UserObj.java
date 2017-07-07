package online.findfootball.android.user;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.firebase.database.children.PackableObject;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.object.FootballGameList;

/**
 * Created by WiskiW on 17.04.2017.
 */

@SuppressWarnings("NullableProblems")
public class UserObj extends PackableObject {

    private final static String PATH_USERS = "users/";
    final static String PATH_GAMES_FOOTBALL = "/events/football/";

    public final static String EMPTY_UID = "empty_uid";

    public final static String PATH_DISPLAY_NAME = "display_name";
    public final static String PATH_EMAIL = "email";
    public final static String PATH_PHOTO_URL = "photo_url";
    public final static String PATH_REGISTER_TIME = "register_time";
    public final static String PATH_LAST_ACTIVITY_TIME = "last_activity_time";
    public final static String PATH_AUTH_PROVIDER = "auth_provider";
    public final static String PATH_CLOUDE_MESSAGE_TOKEN = "cm_token";

    public final static UserObj EMPTY = new UserObj(EMPTY_UID);

    private String uid;
    private String displayName;
    private String email;
    private String cloudMessageToken;
    private long lastActivityTime;
    private long registerTime;
    private Uri photoUrl;
    private String authProvider;
    private FootballGameList gameList;

    protected UserObj() {
        this.uid = EMPTY_UID;
    }

    public UserObj(String uid) {
        this.uid = uid;
    }

    public UserObj(FirebaseUser firebaseUser) {
        this.initByFirebaseUser(firebaseUser);
    }

    public void initByFirebaseUser(FirebaseUser fUser) {
        setUid(fUser.getUid());
        setDisplayName(fUser.getDisplayName());
        setEmail(fUser.getEmail());
        setPhotoUrl(fUser.getPhotoUrl());

    }

    public boolean isEmpty() {
        return this.uid.equals(EMPTY_UID);
    }

    private void initGameList(FootballGameList list) {
        list.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey());
    }

    public FootballGameList getGameList() {
        if (gameList == null) {
            gameList = new FootballGameList();
            initGameList(gameList);
        }
        return gameList;
    }

    public String getCloudMessageToken() {
        return this.cloudMessageToken;
    }

    public void setCloudMessageToken(String cloudMessageToken) {
        this.cloudMessageToken = cloudMessageToken;
    }

    public void setGameList(FootballGameList gameList) {
        initGameList(gameList);
        this.gameList = gameList;
    }

    public void addGame(GameObj gameObj) {
        if (gameList.contains(gameObj)) {
            gameList.remove(gameObj);
        }
        gameList.add(gameObj);
    }

    public void removeGame(GameObj gameObj) {
        if (gameList != null) {
            gameList.remove(gameObj);
        }
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "UserId:" + getUid();
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(uid);
        out.writeString(displayName);
        out.writeString(email);
        out.writeString(cloudMessageToken);
        out.writeString(authProvider);
        out.writeLong(lastActivityTime);
        out.writeLong(registerTime);
        out.writeParcelable(photoUrl, flags);
        out.writeParcelable(gameList, flags);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<UserObj> CREATOR = new Parcelable.Creator<UserObj>() {
        public UserObj createFromParcel(Parcel in) {
            return new UserObj(in);
        }

        public UserObj[] newArray(int size) {
            return new UserObj[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    public UserObj(Parcel in) {
        super(in);
        uid = in.readString();
        displayName = in.readString();
        email = in.readString();
        cloudMessageToken = in.readString();
        authProvider = in.readString();
        lastActivityTime = in.readLong();
        registerTime = in.readLong();
        photoUrl = in.readParcelable(Uri.class.getClassLoader());
        in.readParcelable(GameObj.class.getClassLoader());
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        UserObj tmp = (UserObj) obj;
        return tmp.getUid().equals(this.getUid());
    }

    @Override
    public int hashCode() {
        return 31 * this.getUid().hashCode();
    }

    @Override
    public String getPackablePath() {
        return PATH_USERS;
    }

    @Override
    public void setPackableKey(String key) {
        super.setPackableKey(key);
        uid = key;
    }

    @Override
    public String getPackableKey() {
        return uid;
    }

    @Override
    public boolean hasUnpacked() {
        return photoUrl != null && email != null && displayName != null
                && getGameList().hasUnpacked();
    }

    @NonNull
    @Override
    public DataInstanceResult unpack(DataSnapshot dataSnapshot) {
        DataInstanceResult r = DataInstanceResult.onSuccess();
        try {
            setUid(dataSnapshot.getKey());
            setDisplayName((String) dataSnapshot.child(PATH_DISPLAY_NAME).getValue());
            String url = (String) dataSnapshot.child(PATH_PHOTO_URL).getValue();
            if (url != null) {
                setPhotoUrl(Uri.parse(url));
            } else {
                DataInstanceResult.calculateResult(r, new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE));
            }
            setEmail((String) dataSnapshot.child(PATH_EMAIL).getValue());
            setCloudMessageToken((String) dataSnapshot.child(PATH_CLOUDE_MESSAGE_TOKEN).getValue());
            setAuthProvider((String) dataSnapshot.child(PATH_AUTH_PROVIDER).getValue());

            Object regTimeObj;
            regTimeObj = dataSnapshot.child(PATH_REGISTER_TIME).getValue();
            if (regTimeObj != null) {
                setRegisterTime((Long) regTimeObj);
            } else {
                DataInstanceResult.calculateResult(r, new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE));
            }

            Object lastActObj = dataSnapshot.child(PATH_LAST_ACTIVITY_TIME).getValue();
            if (lastActObj != null) {
                setLastActivityTime((Long) lastActObj);
            } else {
                DataInstanceResult.calculateResult(r, new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE));
            }

            if (gameList == null) {
                gameList = new FootballGameList();
                initGameList(gameList);
            }
            gameList.unpack(dataSnapshot.child(PATH_GAMES_FOOTBALL));

            return r;
        } catch (Exception ex) {
            return DataInstanceResult.onFailed(ex.getMessage(), ex);
        }
    }

    @Override
    public DatabasePackable has(@NonNull DatabasePackable packable) {
        return getGameList().has(packable);
    }

    @NonNull
    @Override
    public DataInstanceResult pack(@NonNull HashMap<String, Object> databaseMap) {
        return new DataInstanceResult(DataInstanceResult.CODE_NO_PERMISSIONS);
    }

}
