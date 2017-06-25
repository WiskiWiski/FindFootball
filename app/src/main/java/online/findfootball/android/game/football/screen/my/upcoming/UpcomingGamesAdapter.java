package online.findfootball.android.game.football.screen.my.upcoming;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import online.findfootball.android.R;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.screen.my.OnRecyclerViewItemClickListener;

/**
 * Created by WiskiW on 12.04.2017.
 */

public class UpcomingGamesAdapter extends RecyclerView.Adapter<UpcomingGamesViewHolder> {

    private List<GameObj> gameList;

    private OnRecyclerViewItemClickListener itemLongClickListener;
    private OnRecyclerViewItemClickListener itemClickListener;

    public UpcomingGamesAdapter() {
        gameList = new ArrayList<>();
    }

    void setGameList(List<GameObj> gameList) {
        this.gameList = gameList;
        notifyDataSetChanged();
    }

    public List<GameObj> getGameList() {
        return gameList;
    }

    public void setItemClickListener(OnRecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnRecyclerViewItemClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    @Override
    public UpcomingGamesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_upcoming_game, parent, false);
        return new UpcomingGamesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UpcomingGamesViewHolder holderSelf, int position) {
        GameObj game = gameList.get(position);
        holderSelf.setItemClickListener(itemClickListener);
        holderSelf.setItemLongClickListener(itemLongClickListener);
        holderSelf.setTitle(game.getTitle());
        holderSelf.setEventTime(game.getEventTime());
        holderSelf.setLocation(game.getLocation());
        holderSelf.setTeamsSize(game.getTeams().getTeamsCapacity(),
                game.getTeams().getTeamsOccupancy());
    }

    @Override
    public int getItemCount() {
        if (gameList != null) {
            return gameList.size();
        } else {
            return 0;
        }
    }

    void addGame(GameObj gameObj) {
        gameList.add(gameObj);
        notifyItemInserted(gameList.size() - 1);
    }

    void removeGame(int pos) {
        gameList.remove(pos);
        notifyItemRemoved(pos);
    }

}
