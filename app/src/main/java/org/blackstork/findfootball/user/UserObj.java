package org.blackstork.findfootball.user;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.blackstork.findfootball.firebase.database.DatabaseInstance;
import org.blackstork.findfootball.firebase.database.FBDatabase;
import org.blackstork.findfootball.game.GameObj;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by WiskiW on 17.04.2017.
 */

public class UserObj implements Parcelable, Serializable, DatabaseInstance {

    public final static String PATH_GAMES_FOOTBALL = "/events/football/";

    public final static String PATH_DISPLAY_NAME = "display_name";
    public final static String PATH_EMAIL = "email";
    public final static String PATH_PHOTO_URL = "photo_url";
    public final static String PATH_REGISTER_TIME = "register_time";
    public final static String PATH_LAST_ACTIVITY_TIME = "last_activity_time";


    private String uid;
    private String displayName;
    private String email;
    private long lastActivityTime;
    private long registerTime;
    private Uri photoUrl;
    private HashSet<GameObj> gameSet;


    public UserObj(String uid) {
        this.uid = uid;
        this.gameSet = new HashSet<>();
    }

    public UserObj(FirebaseUser firebaseUser) {
        this.gameSet = new HashSet<>();
        setUid(firebaseUser.getUid());
        setDisplayName(firebaseUser.getDisplayName());
        setEmail(firebaseUser.getEmail());
        setPhotoUrl(firebaseUser.getPhotoUrl());
    }

    public UserObj(DataSnapshot userSnapshot) {
        this.gameSet = new HashSet<>();
        // TODO : Try-catch
        setUid(userSnapshot.getKey());
        setDisplayName((String) userSnapshot.child(PATH_DISPLAY_NAME).getValue());
        setPhotoUrl(Uri.parse((String) userSnapshot.child(PATH_PHOTO_URL).getValue()));
        setRegisterTime((Long) userSnapshot.child(PATH_REGISTER_TIME).getValue());
        setLastActivityTime((Long) userSnapshot.child(PATH_LAST_ACTIVITY_TIME).getValue());
    }

    public HashSet<GameObj> getGameSet() {
        return gameSet;
    }

    public void setGameSet(List<GameObj> gameList) {
        gameSet.clear();
        gameSet = new HashSet<>(gameList);
    }

    public void setGameSet(HashSet<GameObj> gameSet) {
        this.gameSet = gameSet;
    }

    public void pushGameToSet(GameObj gameObj) {
        if (gameSet.contains(gameObj)) {
            gameSet.remove(gameObj);
        }
        gameSet.add(gameObj);
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


    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uid);
        out.writeString(displayName);
        out.writeString(email);
        out.writeLong(lastActivityTime);
        out.writeLong(registerTime);
        out.writeParcelable(photoUrl, flags);
        out.writeSerializable(gameSet);
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
    UserObj(Parcel in) {
        uid = in.readString();
        displayName = in.readString();
        email = in.readString();
        lastActivityTime = in.readLong();
        registerTime = in.readLong();
        photoUrl = in.readParcelable(Uri.class.getClassLoader());
        gameSet = (HashSet<GameObj>) in.readSerializable();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserObj) {
            return (((UserObj) obj).getUid()).equals(this.getUid());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return this.getUid().hashCode();
    }


    @Override
    public boolean isLoaded() {
        return photoUrl != null && email != null && displayName != null;
    }

    @Override
    public void load(final OnLoadListener onLoadListener) {
        DatabaseReference thisGameReference = FBDatabase.getDatabaseReference(this);
        thisGameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                if (userSnapshot == null) {
                    onLoadListener.onFailed(OnLoadListener.FAILED_NULL_SNAPSHOT, null);
                    return;
                }
                setUid(userSnapshot.getKey());
                setDisplayName((String) userSnapshot.child(PATH_DISPLAY_NAME).getValue());
                setPhotoUrl(Uri.parse((String) userSnapshot.child(PATH_PHOTO_URL).getValue()));
                setEmail((String) userSnapshot.child(PATH_EMAIL).getValue());
                setRegisterTime((Long) userSnapshot.child(PATH_REGISTER_TIME).getValue());
                setLastActivityTime((Long) userSnapshot.child(PATH_LAST_ACTIVITY_TIME).getValue());

                HashMap<String, String> gameEids = (HashMap<String, String>) userSnapshot.child(PATH_GAMES_FOOTBALL).getValue();
                Iterator it = gameEids.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    pushGameToSet(new GameObj(pair.getKey().toString()));
                    it.remove(); // avoids a ConcurrentModificationException
                }

                onLoadListener.onSuccess(UserObj.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onLoadListener.onFailed(databaseError.getCode(), databaseError.getMessage());
            }
        });


    }

    @Override
    public int save(Context context) {
        return RESULT_FAILED_NO_PERMISSIONS;
    }

    @Override
    public String getDatabasePath() {
        return FBDatabase.PATH_USERS + getUid() + "/";
    }
}
