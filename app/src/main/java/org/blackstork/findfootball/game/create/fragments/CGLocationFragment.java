package org.blackstork.findfootball.game.create.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.game.create.BaseCGFragment;
import org.blackstork.findfootball.location.LocationObj;
import org.blackstork.findfootball.location.gmaps.fragments.LocationSelectFragment;
import org.blackstork.findfootball.game.GameObj;


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

    private LocationObj getLocation() {
        LocationObj location = null;
        if (locationSelectFragment != null) {
            location = locationSelectFragment.getMarkerLocation();
        } else {
            Log.e(TAG, "getLocation: LocationSelectFragment is null!");
        }
        return location;
    }

    @Override
    public boolean saveResult(boolean checkForCorrect, GameObj game) {
        LocationObj location = getLocation();
        if (checkForCorrect && location == null) {
            Toast.makeText(getContext(), getString(R.string.cg_game_location_frg_location_not_selected),
                    Toast.LENGTH_LONG).show();
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
