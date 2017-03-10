package org.blackstork.findfootball.lacation.gmaps.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.lacation.gmaps.LocationPicker;


public class LocationSelectFragment extends Fragment {

    public static final String TAG = App.G_TAG + ":LocationSelectFrg";

    private OnLocationSelectListener mListener;
    private LocationPicker locationPicker;

    public static final int LOCATION = 1;
    public static final int CANCEL = 2;

    /*
        TODO : Implement Search View here
        https://github.com/lapism/SearchView
     */

    public LocationSelectFragment() {
        // Required empty public constructor
    }

    public static LocationSelectFragment newInstance() {
        return new LocationSelectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_select, container, false);

        SupportMapFragment gMapFragment = (SupportMapFragment)
                getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        Button confirmBtn = (Button) rootView.findViewById(R.id.confirm_btn);

        locationPicker = new LocationPicker(getContext(), gMapFragment);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onLocationSelect(locationPicker.getMarkerPosition());
                }
            }
        });

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocationSelectListener) {
            mListener = (OnLocationSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLocationSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (locationPicker != null){
            locationPicker.destroy();
        }
    }

    public interface OnLocationSelectListener {
        void onLocationSelect(LatLng latLng);
    }
}
