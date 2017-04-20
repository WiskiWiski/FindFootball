package online.findfootball.android.game.info.tabs.players.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import online.findfootball.android.R;
import online.findfootball.android.user.UserObj;


/**
 * Created by WiskiW on 20.04.2017.
 */

public class PlayerListAdapter  extends RecyclerView.Adapter<PlayerViewHolder>  {

    private ArrayList<UserObj> playerList;


    public PlayerListAdapter() {
        playerList = new ArrayList<>();
    }

    public void setPlayerList(ArrayList<UserObj> playerList){
        this.playerList = playerList;
        notifyDataSetChanged();
    }

    public void addPlayer(UserObj player){
        if (playerList.indexOf(player) == -1) {
            playerList.add(player);
            notifyItemInserted(playerList.size() - 1);
        }
    }

    public void removePlayer(UserObj player){
        int index = playerList.indexOf(player);
        if (index != -1){
            playerList.remove(index);
            notifyItemRemoved(index);
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
        UserObj player = playerList.get(position);
        holder.setDisplayName(player.getDisplayName());
        holder.setPhotoUrl(player.getPhotoUrl());
    }


    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public void clear() {
        playerList.clear();
        notifyDataSetChanged();
    }
}
