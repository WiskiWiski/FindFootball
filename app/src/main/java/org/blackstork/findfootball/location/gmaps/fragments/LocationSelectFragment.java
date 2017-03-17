package org.blackstork.findfootball.location.gmaps.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.location.gmaps.GMapsPreferences;
import org.blackstork.findfootball.location.gmaps.LocationPicker;


public class LocationSelectFragment extends SupportMapFragment {

    public static final String TAG = App.G_TAG + ":LocationSelectFrg";

    public static final String BUNDLE_VIEW_ID = "confirm_view_id";

    private OnLocationSelectListener callback;
    private LocationPicker locationPicker;

    private int confirmViewId;

    /*
        TODO : Implement Search View
        https://github.com/lapism/SearchView
     */

    public LocationSelectFragment() {
        // Required empty public constructor
    }

    public static LocationSelectFragment newInstance(int confirmViewId) {
        Bundle args = new Bundle();
        args.putInt(BUNDLE_VIEW_ID, confirmViewId);
        LocationSelectFragment fragment = new LocationSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle args = getArguments();
        if (args != null){
            confirmViewId = args.getInt(BUNDLE_VIEW_ID);
        }

        locationPicker = new LocationPicker(getContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        initConfirmButton(getActivity().findViewById(confirmViewId));
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    private void initConfirmButton(View view) {
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null && locationPicker != null) {
                        callback.onLocationSelect(locationPicker.getMarkerPosition());
                    }
                }
            });
        } else {
            throw new RuntimeException("Confirm view (id:" + confirmViewId
                    + ") not found on " + getContext().getPackageName());
        }
    }

    public LatLng getMarkerPosition() {
        if (locationPicker != null)
            return locationPicker.getMarkerPosition();
        else return null;
    }

    public LocationSelectFragment setMarkerPosition(LatLng latLng) {
        locationPicker.setMarker(new MarkerOptions().position(latLng));
        locationPicker.moveCamera(latLng, GMapsPreferences.MARKER_SCALE);
        return this;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle != null) {
            // Restore last state
            LatLng markerPos = bundle.getParcelable(GMapsPreferences.BUNDLE_MARKER_POS);
            if (markerPos != null)
                locationPicker.setMarker(new MarkerOptions().position(markerPos));

            LatLng cameraPos = bundle.getParcelable(GMapsPreferences.BUNDLE_CAMERA_POS);
            if (cameraPos != null) {
                locationPicker.setCameraPosition(cameraPos);
            }

            locationPicker.setScale(bundle.getFloat(GMapsPreferences.BUNDLE_MAP_SCALE));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putParcelable(GMapsPreferences.BUNDLE_MARKER_POS, locationPicker.getMarkerPosition());
        bundle.putFloat(GMapsPreferences.BUNDLE_MAP_SCALE, locationPicker.getScale());
        bundle.putParcelable(GMapsPreferences.BUNDLE_CAMERA_POS, locationPicker.getCameraPosition());
    }

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
