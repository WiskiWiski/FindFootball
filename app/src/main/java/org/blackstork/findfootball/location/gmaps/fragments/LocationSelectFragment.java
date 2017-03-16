package org.blackstork.findfootball.location.gmaps.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.location.gmaps.LocationPicker;


public class LocationSelectFragment extends SupportMapFragment {

    public static final String TAG = App.G_TAG + ":LocationSelectFrg";

    private OnLocationSelectListener callback;
    private LocationPicker locationPicker;

    private Button confirmBtn;

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
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        locationPicker = new LocationPicker(getContext(), this);
        initConfirmButton();
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    private void initConfirmButton() {
        if (confirmBtn != null) {
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null && locationPicker != null) {
                        callback.onLocationSelect(locationPicker.getMarkerPosition());
                    }
                }
            });
        }
    }

    public LatLng getMarkerPosition() {
        if (locationPicker != null)
            return locationPicker.getMarkerPosition();
        else return null;
    }

    public LocationSelectFragment setMarkerPosition(LatLng latLng){
        locationPicker.setMarker(new MarkerOptions().position(latLng));
        return this;
    }

    public LocationSelectFragment setConfirmButton(Button button) {
        confirmBtn = button;
        initConfirmButton();
        return this;
    }

    /*
        TODO : setSearchView
    public void setSearchView(View view){
        this.view = view;
    }
    */


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocationSelectListener) {
            callback = (OnLocationSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLocationSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
        if (locationPicker != null) {
            locationPicker.destroy();
        }
    }

    public interface OnLocationSelectListener {
        void onLocationSelect(LatLng latLng);
    }
}
