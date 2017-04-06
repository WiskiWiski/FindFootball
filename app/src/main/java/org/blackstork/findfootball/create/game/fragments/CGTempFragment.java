package org.blackstork.findfootball.create.game.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.create.game.BaseCGFragment;
import org.blackstork.findfootball.objects.GameObj;

/**
 * A simple {@link Fragment} subclass.
 */
public class CGTempFragment extends BaseCGFragment {


    public CGTempFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.cg_fragment_temp, container, false);


        return rootView;
    }

    @Override
    public boolean saveResult(GameObj game) {
        return true;
    }

    @Override
    public void updateView(GameObj game) {

    }
}
