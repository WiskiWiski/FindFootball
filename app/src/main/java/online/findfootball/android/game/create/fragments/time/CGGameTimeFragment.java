package online.findfootball.android.game.create.fragments.time;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.create.BaseCGFragment;
import online.findfootball.android.game.create.fragments.time.view.CustomSpinner;
import online.findfootball.android.time.TimeProvider;

public class CGGameTimeFragment extends BaseCGFragment
        implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String TAG = App.G_TAG + ":CGTimeFrg";

    private static final long MINIMAL_TIME_OFFSET = 12 * 60 * 60 * 1000; // 12h

    private SpinnerArrayAdapter daySpinnerAdapter;
    private SpinnerArrayAdapter timeSpinnerAdapter;

    private DatePickerDialog dayPickerDialog;
    private TimePickerDialog timePickerDialog;

    private Calendar calendar;
    private Calendar currentCal;

    public CGGameTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cg_fragment_game_time, container, false);
        currentCal = TimeProvider.getLocalCalendar();
        calendar = currentCal;


        CustomSpinner daySpinner = (CustomSpinner) rootView.findViewById(R.id.day_spinner);
        List<String> daySpinnerItems = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cg_game_time_frg_day_spinner_list)));
        daySpinnerAdapter = new SpinnerArrayAdapter(getContext(), daySpinnerItems);
        daySpinner.setAdapter(daySpinnerAdapter);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {
                    dayPickerDialog = new DatePickerDialog(getContext(), CGGameTimeFragment.this,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    dayPickerDialog.show();
                } else {
                    currentCal = TimeProvider.getLocalCalendar();
                    calendar.set(
                            currentCal.get(Calendar.YEAR),
                            currentCal.get(Calendar.MONTH),
                            currentCal.get(Calendar.DAY_OF_MONTH)
                    );
                    switch (position) {
                        case 1:
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                            break;
                        case 2:
                            calendar.add(Calendar.DAY_OF_MONTH, 7);
                            break;
                    }
                    updateDaySpinner(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        CustomSpinner timeSpinner = (CustomSpinner) rootView.findViewById(R.id.time_spinner);
        List<String> timeSpinnerItems = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cg_game_time_frg_time_spinner_list)));
        timeSpinnerAdapter = new SpinnerArrayAdapter(getContext(), timeSpinnerItems);
        timeSpinner.setAdapter(timeSpinnerAdapter);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {
                    timePickerDialog = new TimePickerDialog(getContext(), CGGameTimeFragment.this,
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                } else {
                    switch (position) {
                        case 0:
                            calendar.set(Calendar.HOUR_OF_DAY, 10);
                            calendar.set(Calendar.MINUTE, 0);
                            break;
                        case 1:
                            calendar.set(Calendar.HOUR_OF_DAY, 14);
                            calendar.set(Calendar.MINUTE, 0);
                            break;
                        case 2:
                            calendar.set(Calendar.HOUR_OF_DAY, 19);
                            calendar.set(Calendar.MINUTE, 0);
                            break;
                    }
                    updateTimeSpinner(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        updateDaySpinner(year, month, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        updateTimeSpinner(hourOfDay, minute);
    }

    private void updateDaySpinner(int year, int month, int dayOfMonth) {
        Formatter f = new Formatter();
        f.format("%02d.%02d.%04d", dayOfMonth, month + 1, year);
        //  month + 1 т к счет сесяцев тут идет с 0, это странно :\

        daySpinnerAdapter.setTitle(f.toString());
        daySpinnerAdapter.notifyDataSetChanged();
    }

    private void updateTimeSpinner(int hourOfDay, int minute) {
        Formatter f = new Formatter();
        f.format("%d:%02d", hourOfDay, minute);
        timeSpinnerAdapter.setTitle(f.toString());
        timeSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void saveResult(GameObj game) {
        if (game != null && calendar != null) {
            game.setEventTime(calendar.getTimeInMillis());
        }
    }

    @Override
    public void updateView(GameObj game) {
        if (game != null && calendar != null) {
            calendar.setTimeInMillis(game.getCreateTime());
            updateDaySpinner(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            updateTimeSpinner(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }
    }

    @Override
    public boolean verifyData(boolean showToast) {
        long currentMs = TimeProvider.convertToLocal(TimeProvider.getUtcTime());
        long selectMs = TimeProvider.convertToLocal(calendar.getTimeInMillis());
        long diff = selectMs - currentMs;
        //Log.d(TAG, "verifyData: curr: " + currentMs);
        //Log.d(TAG, "verifyData: sell: " + selectMs);
        if (diff <= 0) {
            if (showToast) {
                Toast.makeText(getContext(), getString(R.string.cg_game_time_frg_time_in_the_past), Toast.LENGTH_SHORT).show();
                vibrate();
            }
            return false;
        } else if (diff < MINIMAL_TIME_OFFSET) {
            if (showToast) {
                Formatter formatter = new Formatter();
                formatter.format(getString(R.string.cg_game_time_frg_time_offset_too_small), MINIMAL_TIME_OFFSET / 60 / 60 / 1000);
                Toast.makeText(getContext(), formatter.toString(), Toast.LENGTH_SHORT).show();
                vibrate();
            }
            return false;
        } else {
            return true;
        }
    }

}
