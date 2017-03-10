package by.wiskiw.findfootball.lacation.gmaps.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import by.wiskiw.findfootball.R;
import by.wiskiw.findfootball.app.App;
import by.wiskiw.findfootball.lacation.gmaps.GMapsPreferences;

/**
 * Created by WiskiW on 09.03.2017.
 */

public class LocationViewFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = App.G_TAG + ":LocationViewFrg";

    public static final String MARKER_OPTIONS_LIST = "marker_options_list"; // MarkerOptions ArrayList bundle key
    public static final String MARKER_OPTIONS = "marker_options"; // Single MarkerOptions bundle key

    private int markerNumb;
    private ArrayList<MarkerOptions> markerOptionsList;
    private MarkerOptions markerOptions;

    private int currentMarkerIndex;
    private GoogleMap googleMap;


    public LocationViewFragment() {
        // Required empty public constructor
    }

    public static LocationViewFragment newInstance(Bundle bundle) {
        LocationViewFragment fragment = new LocationViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle params = getArguments();
        if (params != null) {
            markerOptions = params.getParcelable(MARKER_OPTIONS);
            if (markerOptions != null){
                markerNumb = 1; // пришел один markerOptions
            } else{
                markerOptionsList = params.getParcelableArrayList(MARKER_OPTIONS_LIST);
                if (markerOptionsList != null){
                    markerNumb = markerOptionsList.size(); // пришел ArrayList
                    if (markerNumb == 1){
                        markerOptions = markerOptionsList.get(0);  // пришел один markerOptions, но в ArrayList
                    }
                }
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_view, container, false);

        if (markerNumb > 1) {
            Button previewBtn = (Button) rootView.findViewById(R.id.preview_btn);
            Button nextBtn = (Button) rootView.findViewById(R.id.next_btn);
            View buttonsContainer = rootView.findViewById(R.id.btns_container);
            buttonsContainer.setEnabled(true);
            buttonsContainer.setVisibility(View.VISIBLE);

            previewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMarker(markerOptionsList.get(previewM()));
                }
            });

            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMarker(markerOptionsList.get(nextM()));
                }
            });
        }

        SupportMapFragment gMapFragment = (SupportMapFragment)
                getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        gMapFragment.getMapAsync(this);

        return rootView;
    }

    private void showMarker(MarkerOptions markerOptions) {
        if (googleMap != null)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    markerOptions.getPosition(), GMapsPreferences.MARKER_SCALE)
            );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (markerNumb == 1) {
            googleMap.addMarker(markerOptions);
            googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), GMapsPreferences.MARKER_SCALE)
            );
        } else if (markerNumb > 1) {
            for (MarkerOptions markerOptions : markerOptionsList) {
                googleMap.addMarker(markerOptions);
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    markerOptionsList.get(currentMarkerIndex).getPosition(), GMapsPreferences.MULTIPLE_MARKER_SCALE)
            );
        }


    }

    private int nextM() {
        if (markerNumb > 1) {
            if (currentMarkerIndex + 1 >= markerNumb) {
                currentMarkerIndex = 0;
            } else {
                currentMarkerIndex++;
            }
        }
        return currentMarkerIndex;
    }

    private int previewM() {
        if (markerNumb > 1) {
            if (currentMarkerIndex - 1 < 0) {
                currentMarkerIndex = markerNumb - 1;
            } else {
                currentMarkerIndex--;
            }
        }
        return currentMarkerIndex;
    }
}
