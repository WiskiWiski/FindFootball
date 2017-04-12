package org.blackstork.findfootball.events.archived;


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
public class ArchivedGamesFragment extends Fragment {

    private static final String TAG = App.G_TAG + ":ArchGamesFrg";
    public static final String F_TAG = "archived_games_frg";

    public ArchivedGamesFragment() {
        // Required empty public constructor
    }

    public static ArchivedGamesFragment newInstance(){
        return new ArchivedGamesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_archived_games, container, false);
    }

}
