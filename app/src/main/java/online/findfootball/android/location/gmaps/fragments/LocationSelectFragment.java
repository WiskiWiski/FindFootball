package online.findfootball.android.location.gmaps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import online.findfootball.android.app.App;
import online.findfootball.android.location.LocationObj;
import online.findfootball.android.location.gmaps.GMapsPreferences;
import online.findfootball.android.location.gmaps.LocationPicker;


public class LocationSelectFragment extends SupportMapFragment {

    private static final String TAG = App.G_TAG + ":LocationSelectFrg";
    public static final String F_TAG = "location_select_frg";

    private LocationPicker locationPicker;

    public LocationSelectFragment() {
        // Required empty public constructor
    }

    public static LocationSelectFragment newInstance() {
        return new LocationSelectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        locationPicker = new LocationPicker(getContext(), this);
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public LocationObj getMarkerLocation() {
        if (locationPicker != null) {
            LatLng coordinates = locationPicker.getMarkerPosition();
            if (coordinates != null) {
                return new LocationObj(coordinates);
            }
        }
        return null;
    }

    public LocationSelectFragment setMarkerPosition(LocationObj locationObj) {
        if (locationObj != null) {
            setMarkerPosition(locationObj.getCoordinates());
        }
        return this;
    }

    public LocationSelectFragment setMarkerPosition(LatLng latLng) {
        if (locationPicker != null && latLng != null) {
            locationPicker.setMarker(new MarkerOptions().position(latLng));
            locationPicker.moveCamera(latLng, GMapsPreferences.MARKER_SCALE);
        }
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
    public void onDetach() {
        super.onDetach();
        if (locationPicker != null) {
            locationPicker.destroy();
        }
    }
}
