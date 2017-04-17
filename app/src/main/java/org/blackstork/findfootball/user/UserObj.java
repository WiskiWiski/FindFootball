package org.blackstork.findfootball.user;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import org.blackstork.findfootball.firebase.database.FBUserDatabase;

import java.util.ArrayList;

/**
 * Created by WiskiW on 17.04.2017.
 */

public class UserObj implements Parcelable {

    private String uid;
    private String displayName;
    private String email;
    private long lastActivityTime;
    private long registerTime;
    private Uri photoUrl;
    private ArrayList<String> gameEids;

    public UserObj() {
    }

    public UserObj(String uid) {
        this.uid = uid;
    }

    public UserObj(FirebaseUser firebaseUser) {
        setUid(firebaseUser.getUid());
        setDisplayName(firebaseUser.getDisplayName());
        setEmail(firebaseUser.getEmail());
        setPhotoUrl(firebaseUser.getPhotoUrl());
    }

    public UserObj(DataSnapshot userSnapshot) {
        // TODO : Try-catch
        setUid(userSnapshot.getKey());
        setDisplayName((String) userSnapshot.child(FBUserDatabase.KEY_DISPLAY_NAME).getValue());
        setPhotoUrl(Uri.parse((String) userSnapshot.child(FBUserDatabase.KEY_PHOTO_URL).getValue()));
        setRegisterTime((Long) userSnapshot.child(FBUserDatabase.KEY_REGISTER_TIME).getValue());
        setLastActivityTime((Long) userSnapshot.child(FBUserDatabase.KEY_LAST_ACTIVITY_TIME).getValue());
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
        out.writeStringList(gameEids);
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
        gameEids = in.readArrayList(String.class.getClassLoader());
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
}
