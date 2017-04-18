package org.blackstork.findfootball.game.find.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.game.my.OnRecyclerViewItemClickListener;

import java.util.List;

/**
 * Created by phanvanlinh on 12/04/2017.
 * phanvanlinh.94vn@gmail.com
 */

public class FindGameAdapter extends RecyclerView.Adapter<FindGameViewHolder> {

    private OnRecyclerViewItemClickListener itemLongClickListener;
    private OnRecyclerViewItemClickListener itemClickListener;

    private List<GameObj> gameList;

    public List<GameObj> getGameList() {
        return gameList;
    }


    public FindGameAdapter(List<GameObj> gameList) {
        this.gameList = gameList;
    }

    public void setItemClickListener(OnRecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(OnRecyclerViewItemClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    @Override
    public FindGameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_find_game, parent, false);
        return new FindGameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FindGameViewHolder holder, int position) {
        GameObj game = gameList.get(position);
        holder.setItemClickListener(itemClickListener);
        holder.setItemLongClickListener(itemLongClickListener);
        holder.setTitle(game.getTitle());
        holder.setDay(String.valueOf(game.getEventTime()));
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void addGame(GameObj gameObj) {
        gameList.add(gameObj);
        notifyItemInserted(gameList.size() - 1);
    }

    public void clear(){
        gameList.clear();
        notifyDataSetChanged();
    }
}