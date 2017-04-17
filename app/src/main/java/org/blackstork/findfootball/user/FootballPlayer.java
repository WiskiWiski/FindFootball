package org.blackstork.findfootball.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by WiskiW on 17.04.2017.
 */

public class FootballPlayer extends UserObj {

    public enum FootballPosition {
        FORWARD, MIDFIELDER, DEFENDER, GOALKEEPER
    }


    private FootballPosition position;

    public FootballPlayer() {
    }

    public FootballPlayer(String uid) {
        super(uid);
    }

    public FootballPlayer(FirebaseUser firebaseUser) {
        super(firebaseUser);
    }

    public FootballPosition getPosition() {
        return position;
    }

    public void setPosition(FootballPosition position) {
        this.position = position;
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeSerializable(position);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<FootballPlayer> CREATOR = new Parcelable.Creator<FootballPlayer>() {
        public FootballPlayer createFromParcel(Parcel in) {
            return new FootballPlayer(in);
        }

        public FootballPlayer[] newArray(int size) {
            return new FootballPlayer[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private FootballPlayer(Parcel in) {
        super(in);
        position = (FootballPosition) in.readSerializable();
    }

}
