package org.blackstork.findfootball.events.upcoming;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.blackstork.findfootball.R;

/**
 * Created by WiskiW on 12.04.2017.
 */

class UpcomingGamesViewHolder extends RecyclerView.ViewHolder {

    private TextView titleTV;
    private TextView dateTimeTV;
    private TextView dateDayTV;

    public UpcomingGamesViewHolder(View itemView) {
        super(itemView);
        titleTV = (TextView) itemView.findViewById(R.id.event_title);
        dateTimeTV = (TextView) itemView.findViewById(R.id.event_date_time);
        dateDayTV = (TextView) itemView.findViewById(R.id.event_date_day);
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
