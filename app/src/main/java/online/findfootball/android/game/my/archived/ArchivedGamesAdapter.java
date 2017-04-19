package online.findfootball.android.game.my.archived;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import online.findfootball.android.R;
import online.findfootball.android.game.my.OnRecyclerViewItemClickListener;
import online.findfootball.android.game.GameObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WiskiW on 13.04.2017.
 */

public class ArchivedGamesAdapter extends RecyclerView.Adapter<ArchivedGamesViewHolder> {

    private List<GameObj> gameList;

    private OnRecyclerViewItemClickListener itemLongClickListener;
    private OnRecyclerViewItemClickListener itemClickListener;
    private OnRecyclerViewItemClickListener itemRecreateBtnClickListener;

    public ArchivedGamesAdapter() {
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

    public void setItemRecreateBtnClickListener(OnRecyclerViewItemClickListener itemRecreateBtnClickListener) {
        this.itemRecreateBtnClickListener = itemRecreateBtnClickListener;
    }

    @Override
    public ArchivedGamesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_archived_game, parent, false);
        return new ArchivedGamesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArchivedGamesViewHolder holder, int position) {
        GameObj game = gameList.get(position);
        holder.setItemClickListener(itemClickListener);
        holder.setItemLongClickListener(itemLongClickListener);
        holder.setRecreateBtnClickListener(itemRecreateBtnClickListener);
        holder.setTitle(game.getTitle());
        holder.setEventTime(game.getEventTime());
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

