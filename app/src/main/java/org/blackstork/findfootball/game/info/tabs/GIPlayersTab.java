package org.blackstork.findfootball.game.info.tabs;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.game.PlayerListObj;
import org.blackstork.findfootball.user.AppUser;
import org.blackstork.findfootball.user.UserObj;
import org.blackstork.findfootball.user.auth.UserAuth;

import java.util.LinkedHashSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class GIPlayersTab extends Fragment {

    private static final String TAG = App.G_TAG + ":GIPlayersTab";


    private GameObj thisGameObj;

    private TextView playersCountTextView;
    private LinearLayout playersLinearLayout;

    public GIPlayersTab() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        thisGameObj.getPlayerList().removeChangeListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gi_tab_players, container, false);
        playersCountTextView = (TextView) rootView.findViewById(R.id.count_text);
        playersLinearLayout = (LinearLayout) rootView.findViewById(R.id.players_container);


        return rootView;
    }

    public void setData(GameObj game) {
        this.thisGameObj = game;
        thisGameObj.getPlayerList().setChangeListener(new PlayerListObj.PlayerListChangeListener() {
            @Override
            public void onListChange(LinkedHashSet<UserObj> playerList) {
                thisGameObj.getPlayerList().setList(playerList);
                updateView();
            }
        });

        playersLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUser appUser = AppUser.getInstance(getContext());
                if (appUser != null) {
                    afterUserAuth(appUser);
                }
            }
        });
        updateView();
    }

    private void afterUserAuth(AppUser appUser) {
        int result = thisGameObj.getPlayerList().addPlayer(appUser);
        switch (result) {
            case PlayerListObj.LIST_OK:
                appUser.joinToFootballGame(thisGameObj);
                updateView();
                break;
            case PlayerListObj.LIST_ALREADY_JOINED:
                appUser.removeFootballGame(thisGameObj);
                updateView();
                break;
            case PlayerListObj.LIST_NO_SPACE:
                Log.d(TAG, "afterUserAuth: Sorry, there are no space!");
                // TODO: Sorry, there are no space!
                Toast.makeText(getContext(), "Sorry, there are no space!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateView() {
        LinkedHashSet<UserObj> list = thisGameObj.getPlayerList().getList();
        playersCountTextView.setText(list.size() + "/" + thisGameObj.getPlayerList().getPlayersCount());
        playersLinearLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view;
        for (UserObj player : list) {
            view = inflater.inflate(R.layout.gi_team_list_item, playersLinearLayout, false);
            ((TextView) view.findViewById(R.id.player_name)).setText(player.getUid());
            playersLinearLayout.addView(view);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UserAuth.AUTH_REQUEST_CODE) {
            switch (resultCode) {
                case UserAuth.RESULT_SUCCESS:
                    AppUser appUser = AppUser.getInstance(getContext());
                    if (appUser != null) {
                        afterUserAuth(appUser);
                    }
                    break;
            }
        }

    }
}
