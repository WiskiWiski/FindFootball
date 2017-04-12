package org.blackstork.findfootball.events.upcoming;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.objects.GameObj;

import java.util.List;

/**
 * Created by WiskiW on 12.04.2017.
 */

public class UpcomingGAdapter extends RecyclerView.Adapter<UpcomingGamesViewHolder> {


    private List<GameObj> gameList;

    public UpcomingGAdapter() {
    }

    public void setGameList(List<GameObj> gameList){
        this.gameList = gameList;
        notifyDataSetChanged();
    }

    @Override
    public UpcomingGamesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_upcoming_game, parent, false);
        return new UpcomingGamesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UpcomingGamesViewHolder holder, int position) {
        GameObj game = gameList.get(position);
        holder.setTitle(game.getTitle());
        holder.setDay(String.valueOf(game.getEventTime()));
    }

    @Override
    public int getItemCount() {
        if (gameList != null) {
            return gameList.size();
        } else {
            return 0;
        }
    }
}
