package online.findfootball.android.game.find.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import online.findfootball.android.R;
import online.findfootball.android.game.PlayerListObj;
import online.findfootball.android.game.my.OnRecyclerViewItemClickListener;
import online.findfootball.android.location.LocationObj;
import online.findfootball.android.time.TimeProvider;

/**
 * Created by WiskiW on 13.04.2017.
 */

public class FindGameViewHolder extends RecyclerView.ViewHolder {


    private View itemView;
    private TextView titleView;
    private TextView dateTimeView;
    private TextView dateDayView;
    private TextView locationView;
    private TextView playerCountView;

    public FindGameViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        titleView = (TextView) itemView.findViewById(R.id.event_title);
        dateTimeView = (TextView) itemView.findViewById(R.id.event_date_time);
        dateDayView = (TextView) itemView.findViewById(R.id.event_date_day);
        locationView = (TextView) itemView.findViewById(R.id.event_location);
        playerCountView = (TextView) itemView.findViewById(R.id.event_players_count);
    }

    public void setItemClickListener(final OnRecyclerViewItemClickListener itemListener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null)
                    itemListener.onClick(getAdapterPosition());
            }
        });
    }

    public void setItemLongClickListener(final OnRecyclerViewItemClickListener itemListener) {
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemListener != null) {
                    itemListener.onClick(getAdapterPosition());
                    return true;
                }
                return false;
            }
        });
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setEventTime(long eventTime) {
        long localTime = TimeProvider.convertToLocal(eventTime);
        dateDayView.setText(TimeProvider.getStringDate(TimeProvider.FORMAT_DAY_2, localTime));
        dateTimeView.setText(TimeProvider.getStringDate(TimeProvider.FORMAT_TIME, localTime));
    }

    public void setLocation(LocationObj location){
        if (location != null){
            String addressCity = location.getCityName();
            String addressCountry = location.getCountryName();
            if (addressCity != null && addressCountry != null){
                locationView.setText(addressCity + ", " + addressCountry);
            } else {
                if (addressCity != null){
                    locationView.setText(addressCity);
                } else {
                    locationView.setText(addressCountry);
                }
            }
        }
    }

    public void setPlayerList(PlayerListObj playerList){
        if (playerList != null){
            String str = playerList.getList().size() + "/" + playerList.getPlayersCount();
            playerCountView.setText(str);
        }
    }

}
