package online.findfootball.android.game.football.screen.create.fragments.time;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import online.findfootball.android.R;
import online.findfootball.android.app.App;

/**
 * Created by WiskiW on 28.05.2017.
 */

public class SpinnerArrayAdapter extends ArrayAdapter<String> {

    private static final String TAG = App.G_TAG + ":SpinnerArrayAdapter";

    private static final int spinnerDropDownItemResource = R.layout.cg_date_spinner_dropdown_item;
    private static final int spinnerItemResourceId = R.layout.cg_date_spinner_item;
    private static final int spinnerItemTextViewResource = R.id.custom_spinner_item;

    private final LayoutInflater mInflater;
    private final Context context;
    private String title;

    private List<String> mutableList;

    public SpinnerArrayAdapter(Context context, List<String> immutableList) {
        super(context, spinnerItemResourceId, immutableList);
        this.mutableList = new ArrayList<>(immutableList);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        setDropDownViewResource(spinnerDropDownItemResource);
    }


    public void setMutableItem(int pos, String val) {
        mutableList.set(pos, val);
    }

    public String getMutableItem(int pos) {
        return mutableList.get(pos);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private View createMutableView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view;
        final TextView text;

        if (convertView == null) {
            view = mInflater.inflate(spinnerItemResourceId, parent, false);
        } else {
            view = convertView;
        }

        try {
            //  Otherwise, find the TextView field within the layout
            text = (TextView) view.findViewById(spinnerItemTextViewResource);
            if (text == null) {
                throw new RuntimeException("Failed to find view with ID "
                        + context.getResources().getResourceName(spinnerItemResourceId)
                        + " in item layout");
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        //Log.d(TAG, "createMutableView: p:" + position + " --- " + getTitle());
        text.setText(getTitle());
        return view;
    }

    private View createImmutableView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view;
        final TextView text;

        if (convertView == null) {
            view = mInflater.inflate(spinnerDropDownItemResource, parent, false);
        } else {
            view = convertView;
        }

        try {
            //  Otherwise, find the TextView field within the layout
            text = (TextView) view.findViewById(spinnerItemTextViewResource);
            if (text == null) {
                throw new RuntimeException("Failed to find view with ID "
                        + context.getResources().getResourceName(spinnerDropDownItemResource)
                        + " in item layout");
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        //Log.d(TAG, "createImmutableView: p:" + position + " --- " + getItem(position));
        text.setText(getItem(position));
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createImmutableView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (getTitle() != null) {
            return createMutableView(position, convertView, parent);
        }
        return super.getView(position, convertView, parent);
    }
}
