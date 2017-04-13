package org.blackstork.findfootball.create.game.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.create.game.BaseCGFragment;
import org.blackstork.findfootball.location.gmaps.fragments.LocationSelectFragment;
import org.blackstork.findfootball.objects.GameObj;


public class CGLocationFragment extends BaseCGFragment {

    private static final String TAG = App.G_TAG + ":CGLocationFrg";

    private LocationSelectFragment locationSelectFragment;

    public CGLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cg_fragment_location, container, false);
        locationSelectFragment = (LocationSelectFragment) getChildFragmentManager().findFragmentById(R.id.gmap_fragment);
        return rootView;
    }

    private LatLng getLocation() {
        LatLng location = null;
        if (locationSelectFragment != null) {
            location = locationSelectFragment.getMarkerPosition();
        } else {
            Log.e(TAG, "getLocation: LocationSelectFragment is null!");
        }
        return location;
    }

    @Override
    public boolean saveResult(boolean checkForCorrect, GameObj game) {
        LatLng location = getLocation();
        if (checkForCorrect && location == null) {
            Toast.makeText(getContext(), "Please, select game location!", Toast.LENGTH_LONG).show();
            return false;
        }
        game.setLocation(location);
        return true;
    }

    @Override
    public void updateView(GameObj game) {
        if (locationSelectFragment != null) {
            locationSelectFragment.setMarkerPosition(game.getLocation());
        }
    }
}
