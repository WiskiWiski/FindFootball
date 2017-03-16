package org.blackstork.findfootball.location.gmaps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.location.gmaps.GMapsPreferences;

/**
 * Created by WiskiW on 09.03.2017.
 */

public class LocationViewFragment extends SupportMapFragment implements OnMapReadyCallback {

    public static final String TAG = App.G_TAG + ":LocationViewFrg";

    public static final String MARKER_OPTIONS = "marker_options"; // Single MarkerOptions bundle key

    private MarkerOptions markerOptions;
    private Marker marker;

    private GoogleMap googleMap;


    public LocationViewFragment() {
        // Required empty public constructor
    }

    public static LocationViewFragment newInstance(MarkerOptions markerOptions) {
        Bundle args = new Bundle();
        args.putParcelable(LocationViewFragment.MARKER_OPTIONS, markerOptions);
        LocationViewFragment fragment = new LocationViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            markerOptions = args.getParcelable(MARKER_OPTIONS);
            setMarker(markerOptions);
        }
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        getMapAsync(this);
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public void setMarker(MarkerOptions markerOptions) {
        if (googleMap != null && markerOptions != null) {
            drawMarker(markerOptions);
            moveCamera(markerOptions.getPosition(), GMapsPreferences.MARKER_SCALE);
        }
    }

    private void drawMarker(MarkerOptions markerOptions) {
        if (googleMap != null) {
            if (marker != null) marker.remove(); // удаляем предыдущий маркер
            marker = googleMap.addMarker(markerOptions);
        }
    } // отрисовка маркера на карте

    private void moveCamera(LatLng position, float scale) {
        if (googleMap != null) {
            if (scale == 0) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
            } else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, scale));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (markerOptions != null) {
            drawMarker(markerOptions);
            moveCamera(markerOptions.getPosition(), GMapsPreferences.MARKER_SCALE);
        }
    }
}
