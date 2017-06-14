package online.findfootball.android.game.football.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.user.UserObj;

/**
 * Created by WiskiW on 17.04.2017.
 */

public class FootballPlayer extends UserObj {

    private final static String PATH_POSITION = "position/";

    public enum FootballPosition {
        UNSET, FORWARD, MIDFIELDER, DEFENDER, GOALKEEPER;
    }

    private FootballPosition position = FootballPosition.UNSET;


    public FootballPlayer(String uid) {
        super(uid);
    }

    public FootballPlayer(UserObj u) {
        super(u.getUid());
        setDisplayName(u.getDisplayName());
        setEmail(u.getEmail());
        setLastActivityTime(u.getLastActivityTime());
        setRegisterTime(u.getRegisterTime());
        setPhotoUrl(u.getPhotoUrl());
        setGameList(u.getGameList());
    }

    public static FootballPlayer newPlayer(UserObj u) {
        return new FootballPlayer(u.getUid());
    }

    public FootballPosition getPosition() {
        return position;
    }

    public FootballPlayer setPosition(FootballPosition position) {
        this.position = position;
        return this;
    }

    @Override
    public String getDirectoryPath() {
        return getUid() + "/";
    }

    @Override
    public DataInstanceResult pack(DatabaseReference databaseReference) {
        databaseReference.child(PATH_POSITION).setValue(position.name().toLowerCase());
        return DataInstanceResult.onSuccess();
    }

    @Override
    public DataInstanceResult unpack(DataSnapshot dataSnapshot) {
        //super.unpack(dataSnapshot);
        try {
            String stringPosition = (String) dataSnapshot.child(PATH_POSITION).getValue();
            if (stringPosition != null) {
                position = FootballPosition.valueOf(stringPosition.toUpperCase());
            } else {
                return new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE, "Football position not found");
            }
            return DataInstanceResult.onSuccess();
        } catch (Exception ex) {
            return new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE, ex);
        }
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
