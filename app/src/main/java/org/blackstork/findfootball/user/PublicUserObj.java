package org.blackstork.findfootball.user;

import com.google.firebase.database.DataSnapshot;

import org.blackstork.findfootball.firebase.database.FBUserDatabase;

/**
 * Created by WiskiW on 16.04.2017.
 */

public class PublicUserObj {

    private String uid;
    private String displayName;
    private String photoUrl;
    private long registerTime;
    private long lastActivityTime;


    public PublicUserObj(String uid) {
        this.uid = uid;
    }

    public PublicUserObj(DataSnapshot userSnapshot) {
        // TODO : Try-catch
        setUid(userSnapshot.getKey());
        setDisplayName((String) userSnapshot.child(FBUserDatabase.KEY_DISPLAY_NAME).getValue());
        setPhotoUrl((String) userSnapshot.child(FBUserDatabase.KEY_PHOTO_URL).getValue());
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }


}
