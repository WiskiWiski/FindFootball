package org.blackstork.findfootball.game.my.upcoming;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.game.my.OnRecyclerViewItemClickListener;

/**
 * Created by WiskiW on 12.04.2017.
 */

class UpcomingGamesViewHolder extends RecyclerView.ViewHolder {

    private View itemView;
    private TextView titleTV;
    private TextView dateTimeTV;
    private TextView dateDayTV;

    public UpcomingGamesViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        titleTV = (TextView) itemView.findViewById(R.id.event_title);
        dateTimeTV = (TextView) itemView.findViewById(R.id.event_date_time);
        dateDayTV = (TextView) itemView.findViewById(R.id.event_date_day);
    }

    public void setItemClickListener(final OnRecyclerViewItemClickListener itemListener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onClick(getAdapterPosition());
            }
        });
    }

    public void setItemLongClickListener(final OnRecyclerViewItemClickListener itemListener) {
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemListener.onClick(getAdapterPosition());
                return true;
            }
        });
    }

    public void setTitle(String title) {
        titleTV.setText(title);
    }

    public void setTime(String time) {
        dateTimeTV.setText(time);
    }

    public void setDay(String day) {
        dateDayTV.setText(day);
    }

}
