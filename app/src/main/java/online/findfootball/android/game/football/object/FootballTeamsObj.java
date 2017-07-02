package online.findfootball.android.game.football.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.firebase.database.children.PackableObject;
import online.findfootball.android.user.UserObj;

/**
 * Created by WiskiW on 02.07.2017.
 */

public class FootballTeamsObj extends PackableObject {


    private static final String KEY_PLAYERS = "players";

    public static final String TEAM_NAME_A = "a"; // если название команды а, то апп крашится на team = source.readParcelable
    public static final String TEAM_NAME_B = "b";

    private FootballTeamObj teamA;
    private FootballTeamObj teamB;

    public FootballTeamsObj() {

    }

    public FootballTeamsObj(Parcel source) {
        super(source);
        teamA = new FootballTeamObj(source);
        teamB = new FootballTeamObj(source);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        teamA.writeToParcel(dest, flags);
        teamB.writeToParcel(dest, flags);
    }

    @Nullable
    @Override
    public DatabasePackable has(@NonNull DatabasePackable packable) {
        if (getTeamA().has(packable) != null) {
            return getTeamA().has(packable);
        } else {
            return getTeamB().has(packable);
        }
    }

    private FootballTeamObj getPlayerTeam(FootballPlayer player) {
        if (player.getTeamName().equals(TEAM_NAME_A)) {
            return getTeamA();
        } else if (player.getTeamName().equals(TEAM_NAME_B)) {
            return getTeamB();
        } else {
            return null;
        }
    }

    public boolean remove(FootballPlayer player) {
        boolean result = false;
        FootballTeamObj team = getPlayerTeam(player);
        if (team != null) {
            result = team.remove(player);
        }
        return result;
    }

    public boolean add(FootballPlayer player) {
        boolean result = false;
        FootballTeamObj team = getPlayerTeam(player);
        if (team != null) {
            result = team.add(player);
        }
        return result;
    }

    public boolean enrollPlayer(FootballPlayer player) {
        boolean result = false;
        FootballTeamObj team = getPlayerTeam(player);
        if (team != null) {
            result = team.enrollPlayer(player);
        }
        return result;
    }

    public boolean unrollPlayer(FootballPlayer player) {
        boolean result = false;
        FootballTeamObj team = getPlayerTeam(player);
        if (team != null) {
            result = team.unrollPlayer(player);
        }
        return result;
    }

    public FootballTeamObj getTeamA() {
        if (this.teamA == null) {
            this.teamA = new FootballTeamObj(TEAM_NAME_A);
            this.teamA.setPackableKey(this.getPackableKey());
        }
        return this.teamA;
    }

    public FootballTeamObj getTeamB() {
        if (this.teamB == null) {
            this.teamB = new FootballTeamObj(TEAM_NAME_B);
            this.teamB.setPackableKey(this.getPackableKey());
        }
        return this.teamB;
    }

    public void setTeamsCapacity(int size) {
        getTeamA().setCapacity(size);
        getTeamB().setCapacity(size);
    }

    public int getTeamsCapacity() {
        return getTeamA().getCapacity() * 2;
    }

    public int getTeamsOccupancy() {
        return getTeamA().getTeamOccupancy() + getTeamB().getTeamOccupancy();
    }

    @Override
    public void setPackablePath(String path) {
        super.setPackablePath(path);
        getTeamA().setPackablePath(path);
        getTeamB().setPackablePath(path);
    }

    @Override
    public void setPackableKey(String key) {
        getTeamA().setPackableKey(key);
        getTeamB().setPackableKey(key);
    }

    public FootballPlayer getPlayer(UserObj user) {
        FootballPlayer player = new FootballPlayer(user);
        if (!hasPlayer(player)) {
            return null;
        }
        int index = getTeamA().indexOf(player);
        if (index != -1) {
            return getTeamA().get(index);
        }
        index = getTeamB().indexOf(player);
        if (index != -1) {
            return getTeamB().get(index);
        }
        return null;
    }

    @NonNull
    @Override
    public String getPackableKey() {
        return KEY_PLAYERS;
    }

    @Override
    public DataInstanceResult pack(HashMap<String, Object> databaseMap) {
        DataInstanceResult r = getTeamA().pack(databaseMap);
        DataInstanceResult.calculateResult(r, getTeamB().pack(databaseMap));
        return r;
    }

    @Override
    public DataInstanceResult unpack(DataSnapshot dataSnapshot) {
        DataInstanceResult r = getTeamA().unpack(dataSnapshot);
        DataInstanceResult.calculateResult(r, getTeamB().unpack(dataSnapshot));
        return r;
    }

    @Override
    public boolean hasUnpacked() {
        return getTeamA().hasUnpacked() && getTeamB().hasUnpacked();
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<FootballTeamsObj> CREATOR =
            new Parcelable.Creator<FootballTeamsObj>() {
                public FootballTeamsObj createFromParcel(Parcel in) {
                    return new FootballTeamsObj(in);
                }

                public FootballTeamsObj[] newArray(int size) {
                    return new FootballTeamsObj[size];
                }
            };


    public boolean hasPlayer(FootballPlayer player) {
        return getTeamA().indexOf(player) != -1 || getTeamB().indexOf(player) != -1;
    }


}
