package online.findfootball.android.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseSelfPackable;
import online.findfootball.android.firebase.database.FBDatabase;
import online.findfootball.android.firebase.database.children.SelfPackableArrayList;
import online.findfootball.android.firebase.database.children.SelfPackableObject;
import online.findfootball.android.user.UserObj;

/**
 * Created by WiskiW on 04.06.2017.
 */

public class GameTeam<Player extends UserObj> extends SelfPackableObject implements Parcelable {

    private final static String PATH_MAX_PLAYER_COUNT = "max_player_count";
    public final static String PATH_PLAYERS = "players";

    private int maxPlayerCount;
    private SelfPackableArrayList<Player> playerList;

    public GameTeam() {

    }

    public int getPlayerCount() {
        if (playerList != null) {
            return playerList.size();
        } else {
            return 0;
        }
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    public SelfPackableArrayList<Player> getPlayerList() {
        if (playerList == null) {
            newPlayerList();
        }
        return playerList;
    }

    public void setPlayerList(SelfPackableArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    private void newPlayerList() {
        playerList = new SelfPackableArrayList<Player>() {
            @Override
            protected Player unpackItem(DataSnapshot dataSnapshot) {
                Player p = (Player) new UserObj(dataSnapshot.getKey());
                p.unpack(dataSnapshot);
                return p;
            }
        };
        playerList.setDirectoryPath(getDirectoryPath() + PATH_PLAYERS);
    }

    public boolean addPlayer(int i, Player player) {
        if (playerList == null) {
            newPlayerList();
        }
        if (!hasPlayer(player)) {
            playerList.add(i, player);
            return true;
        } else {
            return false;
        }
    }

    public boolean addPlayer(Player player) {
        if (playerList == null) {
            newPlayerList();
        }

        if (!hasPlayer(player)) {
            playerList.add(player);
            return true;
        }
        return false;
    }

    public void enrollPlayer(Player p) {
        // добавляем игрока в локальный список
        addPlayer(p);

        // добавляем игрока в список в бд
        HashMap<String, Object> playerMap = new HashMap<>();
        p.pack(playerMap);
        FBDatabase.getDatabaseReference(getPlayerList()).child(p.getUid()).setValue(playerMap);
    }

    public void unrollPlayer(Player p) {
        // удаляем игрока из локального списка
        removePlayer(p);

        // удаляем игрока из команды в бд
        FBDatabase.getDatabaseReference(getPlayerList()).child(p.getUid()).removeValue();
    }

    public Player get(int index) {
        if (playerList != null) {
            return playerList.get(index);
        } else {
            return null;
        }
    }

    public int getPosition(Player player) {
        if (playerList == null) {
            return -1;
        } else {
            return playerList.indexOf(player);
        }
    }

    public boolean hasPlayer(Player player) {
        return playerList != null && playerList.contains(player);
    }

    public boolean removePlayer(Player player) {
        if (playerList == null) {
            return false;
        } else {
            return playerList.remove(player);
        }
    }

    public boolean removePlayer(int index) {
        if (playerList == null) {
            return false;
        } else if (playerList.size() <= index) {
            return false;
        } else {
            playerList.remove(index);
            return true;
        }
    }

    @NonNull
    @Override
    public DataInstanceResult pack(@NonNull HashMap<String, Object> databaseMap) {
        databaseMap.put(PATH_MAX_PLAYER_COUNT, maxPlayerCount);
        if (playerList == null) {
            newPlayerList();
        }

        HashMap<String, Object> playerListMap = new HashMap<>();
        playerList.pack(playerListMap);
        databaseMap.put(PATH_PLAYERS, playerListMap);
        return DataInstanceResult.onSuccess();
    }

    @NonNull
    @Override
    public DataInstanceResult unpack(@NonNull DataSnapshot dataSnapshot) {
        try {
            Long l = (Long) dataSnapshot.child(PATH_MAX_PLAYER_COUNT).getValue();
            if (l != null) {
                setMaxPlayerCount(((Long) dataSnapshot.child(PATH_MAX_PLAYER_COUNT).getValue()).intValue());
            } else {
                return new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE);
            }

            if (playerList == null) {
                newPlayerList();
            } else {
                playerList.clear();
            }
            playerList.unpack(dataSnapshot.child(PATH_PLAYERS));
            return DataInstanceResult.onSuccess();
        } catch (Exception ex) {
            return new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE);
        }
    }

    @Override
    public DatabaseSelfPackable has(@NonNull DatabaseSelfPackable packable) {
        return getPlayerList().has(packable);
    }

    @Override
    public boolean hasUnpacked() {
        return playerList != null && playerList.hasUnpacked();
    }

    private GameTeam(Parcel in) {
        maxPlayerCount = in.readInt();
        playerList = in.readParcelable(SelfPackableArrayList.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(maxPlayerCount);
        dest.writeParcelable(playerList, flags);
    }

    public static final Parcelable.Creator<GameTeam> CREATOR = new Parcelable.Creator<GameTeam>() {
        public GameTeam createFromParcel(Parcel in) {
            return new GameTeam(in);
        }

        public GameTeam[] newArray(int size) {
            return new GameTeam[size];
        }
    };
}
