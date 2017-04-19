package org.blackstork.findfootball.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.firebase.database.FBDatabase;
import org.blackstork.findfootball.user.UserObj;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by WiskiW on 17.04.2017.
 */

public class PlayerListObj implements Parcelable {

    public static final String TAG = App.G_TAG + ":PlayerListObj";

    public final static String PATH_PLAYER_COUNT = "/player_count/";
    public final static String PATH_PLAYERS = "/players/";

    private String eid;
    private int playersCount = 4;
    private LinkedHashSet<UserObj> playerList;

    private DatabaseReference databaseReference;
    private ChildEventListener databaseListener;

    public PlayerListObj(String eid) {
        this.eid = eid;
        this.playerList = new LinkedHashSet<>();
    }

    public PlayerListObj(DataSnapshot gameSnapshot) {
        eid = gameSnapshot.getKey();

        playerList = new LinkedHashSet<>();
        HashMap<String, String> hashMap = (HashMap<String, String>) gameSnapshot.child(PATH_PLAYERS).getValue();
        if (hashMap != null) {
            Iterator it = hashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                playerList.add(new UserObj((String) pair.getKey()));
                it.remove(); // avoids a ConcurrentModificationException
            }
        }

        Object teamSize = gameSnapshot.child(PATH_PLAYER_COUNT).getValue();
        if (teamSize != null) {
            setPlayersCount(((Long) teamSize).intValue());
        }
    }

    public HashMap<String, String> getHashMap() {
        HashMap<String, String> hashMap = new LinkedHashMap<>();
        for (UserObj player : playerList) {
            hashMap.put(player.getUid(), player.getUid());
        }
        return hashMap;
    }

    public LinkedHashSet<UserObj> getList() {
        return playerList;
    }

    public void setList(LinkedHashSet<UserObj> list) {
        this.playerList = list;
    }

    public void setChangeListener(final PlayerListChangeListener changeListener) {
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(FBDatabase.PATH_FOOTBALL_GAMES).child(eid)
                .child(PATH_PLAYERS);
        databaseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserObj player = new UserObj(dataSnapshot.getKey());
                if (playerList.add(player)) {
                    Log.d(TAG, "onChildAdded: dataSnapshot:" + dataSnapshot.getKey());
                    changeListener.onListChange(playerList);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: ");
                UserObj player = new UserObj(dataSnapshot.getKey());
                playerList.remove(player);
                changeListener.onListChange(playerList);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };
        databaseReference.addChildEventListener(databaseListener);
    }

    public void removeChangeListener() {
        if (databaseListener != null && databaseReference != null) {
            databaseReference.removeEventListener(databaseListener);
        }
    }

    public static final int LIST_OK = 0;
    public static final int LIST_ALREADY_JOINED = 2;
    public static final int LIST_NO_SPACE = 3;

    public int addPlayer(UserObj player) {
        if (playerList.size() < playersCount) {
            return playerList.add(player) ? LIST_OK : LIST_ALREADY_JOINED;
        }
        return LIST_NO_SPACE;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public void setPlayersCount(int playersCount) {
        this.playersCount = playersCount;
    }

    void joinTheTeam() {

    }

    public boolean removePlayer(UserObj user) {
        if (playerList != null) {
            return playerList.remove(user);
        }
        return false;
    }

    public interface PlayerListListener {
        void onComplete(int resultCode, PlayerListObj playerList);
    }

    public interface PlayerListChangeListener {
        void onListChange(LinkedHashSet<UserObj> playerList);
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
        out.writeInt(playersCount);
        out.writeSerializable(playerList);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<PlayerListObj> CREATOR = new Parcelable.Creator<PlayerListObj>() {
        public PlayerListObj createFromParcel(Parcel in) {
            return new PlayerListObj(in);
        }

        public PlayerListObj[] newArray(int size) {
            return new PlayerListObj[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private PlayerListObj(Parcel in) {
        eid = in.readString();
        playersCount = in.readInt();
        playerList = (LinkedHashSet<UserObj>) in.readSerializable();
    }


}
