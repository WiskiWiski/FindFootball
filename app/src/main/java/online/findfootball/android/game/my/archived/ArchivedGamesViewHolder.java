package online.findfootball.android.game.my.archived;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import online.findfootball.android.R;
import online.findfootball.android.game.my.OnRecyclerViewItemClickListener;

/**
 * Created by WiskiW on 13.04.2017.
 */

public class ArchivedGamesViewHolder extends RecyclerView.ViewHolder {


    private View itemView;
    private TextView titleTV;
    private TextView dateTimeTV;
    private TextView dateDayTV;
    private ImageView recreateBtn;

    public ArchivedGamesViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        recreateBtn = (ImageView) itemView.findViewById(R.id.recreate_button);
        titleTV = (TextView) itemView.findViewById(R.id.event_title);
        dateTimeTV = (TextView) itemView.findViewById(R.id.event_date_time);
        dateDayTV = (TextView) itemView.findViewById(R.id.event_date_day);
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

    public void setRecreateBtnClickListener(final OnRecyclerViewItemClickListener itemListener) {
        recreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null)
                    itemListener.onClick(getAdapterPosition());
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
