package online.findfootball.android.game.football.screen.info.tabs.players.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import online.findfootball.android.R;
import online.findfootball.android.game.football.object.FootballPlayer;
import online.findfootball.android.user.UserObj;


/**
 * Created by WiskiW on 20.04.2017.
 */

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerViewHolder> {

    private ArrayList<FootballPlayer> team;
    private boolean hasAppUser;


    public PlayerListAdapter() {
        team = new ArrayList<>();
    }

    public void setTeam(ArrayList<FootballPlayer> team) {
        this.team = new ArrayList<>(team);
        notifyDataSetChanged();
    }

    public void addAppUserPlayer(FootballPlayer player) {
        addPlayer(player);
        hasAppUser = true;
    }

    public void removeAppUserPayer(FootballPlayer player) {
        removePlayer(player);
        hasAppUser = false;
    }

    public void addPlayer(FootballPlayer player) {
        if (hasAppUser) {
            addPlayer(1, player);
        } else {
            addPlayer(0, player);
        }
    }

    public void addPlayer(int pos, FootballPlayer player) {
        if (!team.contains(player)) {
            team.add(pos, player);
            notifyItemInserted(pos);
        }
    }

    public void removePlayer(FootballPlayer player) {
        int pos = team.indexOf(player);
        if (pos != -1) {
            team.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_player, parent, false);
        return new PlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {
        UserObj userObj = team.get(position);
        holder.setDisplayName(userObj.getDisplayName());
        holder.setPhotoUrl(userObj.getPhotoUrl());
    }


    @Override
    public int getItemCount() {
        return team.size();
    }

    public void clear() {
        int c = team.size();
        team.clear();
        notifyItemRangeRemoved(0, c);
    }


}
