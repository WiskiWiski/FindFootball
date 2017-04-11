package org.blackstork.findfootball.create.game.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.create.game.BaseCGFragment;
import org.blackstork.findfootball.objects.GameObj;
import org.blackstork.findfootball.time.TimeProvider;

import java.util.Calendar;
import java.util.Formatter;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class CGTempFragment extends BaseCGFragment {

    private static final String TAG = App.G_TAG + ":CGTempFrg";

    private static final long MINIMAL_TIME_OFFSET = 60 * 60 * 1000; // ms
    private DatePicker datePicker;
    private TimePicker timePicker;


    public CGTempFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.cg_fragment_temp, container, false);

        datePicker = (DatePicker) rootView.findViewById(R.id.date_picker);
        timePicker = (TimePicker) rootView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        return rootView;
    }

    @Override
    public boolean saveResult(GameObj game) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        cal.set(Calendar.MONTH, datePicker.getMonth());
        cal.set(Calendar.YEAR, datePicker.getYear());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cal.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            cal.set(Calendar.MINUTE, timePicker.getMinute());
        } else {
            cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());

        }
        long utcEventTime = cal.getTimeInMillis();
        long utcCurrentTime = TimeProvider.getUtcTime();
        // + 60 * 1000 - для компенсации минимальной цены деления в 1 минуту в TimePicker'e
        if (utcEventTime + 60 * 1000 < utcCurrentTime) { // + 1 min
            Toast.makeText(getContext(), getString(R.string.cg_game_time_frg_time_in_the_past), Toast.LENGTH_SHORT).show();
            return false;
        } else if (utcEventTime - utcCurrentTime < MINIMAL_TIME_OFFSET + 60 * 1000) {
            Formatter formatter = new Formatter();
            formatter.format(getString(R.string.cg_game_time_frg_time_offset_too_small), MINIMAL_TIME_OFFSET / 60 / 1000);
            Toast.makeText(getContext(), formatter.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        game.setEventTime(utcEventTime);
        return true;
    }

    @Override
    public void updateView(GameObj game) {

    }
}
