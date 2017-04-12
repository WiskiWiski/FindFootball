package org.blackstork.findfootball.events.upcoming;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingGamesFragment extends Fragment {

    private static final String TAG = App.G_TAG + ":UpcGamesFrg";
    public static final String F_TAG = "upcoming_games_frg";

    public UpcomingGamesFragment() {
        // Required empty public constructor
    }

    public static UpcomingGamesFragment newInstance(){
        return new UpcomingGamesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_games, container, false);
    }

}
