package online.findfootball.android.game.football.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.children.PackableObject;
import online.findfootball.android.game.GameTeam;
import online.findfootball.android.user.UserObj;

/**
 * Created by WiskiW on 30.05.2017.
 */

public class FootballTeams extends PackableObject implements Parcelable {

    public final static String DIR_NAME_TEAM_A = "team_a";
    public final static String DIR_NAME_TEAM_B = "team_b";

    private GameTeam<FootballPlayer> teamA;
    private GameTeam<FootballPlayer> teamB;

    public FootballTeams() {

    }

    public GameTeam<FootballPlayer> getTeamA() {
        if (teamA == null) {
            teamA = new GameTeam<>();
            teamA.setDirectoryPath(getDirectoryPath() + DIR_NAME_TEAM_A + "/");
        }
        return teamA;
    }

    public void setTeamA(GameTeam<FootballPlayer> teamA) {
        this.teamA = teamA;
    }

    public GameTeam<FootballPlayer> getTeamB() {
        if (teamB == null) {
            teamB = new GameTeam<>();
            teamB.setDirectoryPath(getDirectoryPath() + DIR_NAME_TEAM_B + "/");
        }
        return teamB;
    }

    public boolean hasPlayer(FootballPlayer player) {
        return (teamA != null && teamA.hasPlayer(player)) ||
                (teamB != null && teamB.hasPlayer(player));
    }

    public boolean removePlayer(FootballPlayer player) {
        boolean r = false;
        if (teamA != null) {
            r = teamA.removePlayer(player);
        }
        if (!r && teamB != null) {
            r = teamB.removePlayer(player);
        }
        return r;
    }

    public boolean removePlayer(UserObj u) {
        return removePlayer(new FootballPlayer(u.getUid()));
    }

    public void setTeamB(GameTeam<FootballPlayer> teamB) {
        this.teamB = teamB;
    }

    public int getTeamsCapacity() {
        return getTeamA().getMaxPlayerCount() + getTeamB().getMaxPlayerCount();
    }

    public int getTeamsOccupancy() {
        return getTeamA().getPlayerCount() + getTeamB().getPlayerCount();
    }

    @Override
    public boolean hasLoaded() {
        return teamA.hasLoaded() && teamB.hasLoaded();
    }

    @Override
    public DataInstanceResult pack(DatabaseReference databaseReference) {
        DataInstanceResult r = DataInstanceResult.onSuccess();
        DataInstanceResult.calculateResult(r, getTeamA().pack(databaseReference.child(DIR_NAME_TEAM_A)));
        DataInstanceResult.calculateResult(r, getTeamB().pack(databaseReference.child(DIR_NAME_TEAM_B)));
        return r;
    }

    //@SuppressWarnings("unchecked")
    @Override
    public DataInstanceResult unpack(DataSnapshot dataSnapshot) {
        DataInstanceResult r = DataInstanceResult.onSuccess();
        DataInstanceResult.calculateResult(r, getTeamA().unpack(dataSnapshot.child(DIR_NAME_TEAM_A)));
        DataInstanceResult.calculateResult(r, getTeamB().unpack(dataSnapshot.child(DIR_NAME_TEAM_B)));
        return r;
    }

    private FootballTeams(Parcel in) {
        teamA = in.readParcelable(GameTeam.class.getClassLoader());
        teamB = in.readParcelable(GameTeam.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(teamA, flags);
        dest.writeParcelable(teamB, flags);
    }

    public static final Parcelable.Creator<FootballTeams> CREATOR = new Parcelable.Creator<FootballTeams>() {
        public FootballTeams createFromParcel(Parcel in) {
            return new FootballTeams(in);
        }

        public FootballTeams[] newArray(int size) {
            return new FootballTeams[size];
        }
    };
}
