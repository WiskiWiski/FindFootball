package online.findfootball.android.game.info.tabs.players;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.PlayerListObj;
import online.findfootball.android.game.info.tabs.players.recyclerview.PlayerListAdapter;
import online.findfootball.android.user.AppUser;
import online.findfootball.android.user.UserObj;
import online.findfootball.android.user.auth.UserAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class GIPlayersTab extends Fragment {

    private static final String TAG = App.G_TAG + ":GIPlayersTab";


    private GameObj thisGameObj;

    private Button joinLeaveBtn;
    private TextView playersCountTextView;
    private LinearLayout playersLinearLayout;

    private PlayerListAdapter mAdapter;
    private RecyclerView recyclerView;

    private PlayerListObj.PlayersListListener listener;

    public GIPlayersTab() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        if (thisGameObj != null) {
            thisGameObj.getPlayerList().stopLoading();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (thisGameObj != null) {
            mAdapter.clear();
            thisGameObj.getPlayerList().loadList(listener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gi_tab_players, container, false);
        playersCountTextView = (TextView) rootView.findViewById(R.id.count_text);
        playersLinearLayout = (LinearLayout) rootView.findViewById(R.id.players_container);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.player_list);
        joinLeaveBtn = (Button) rootView.findViewById(R.id.join_leave_btn);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new PlayerListAdapter();
        recyclerView.setAdapter(mAdapter);

        listener = new PlayerListObj.PlayersListListener() {
            @Override
            public void onAdd(UserObj player) {
                mAdapter.addPlayer(player);
                updatePlayerNumb();
            }

            @Override
            public void onRemove(UserObj player) {
                mAdapter.removePlayer(player);
                updatePlayerNumb();
            }
        };

        joinLeaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUser appUser = AppUser.getInstance(getContext());
                if (appUser != null) {
                    afterUserAuth(appUser);
                }
            }
        });
        return rootView;
    }

    private void updatePlayerNumb() {
        if (thisGameObj == null || mAdapter == null) {
            return;
        }
        playersCountTextView.setText(mAdapter.getItemCount() + "/" + thisGameObj.getPlayerList().getPlayersCount());
    }

    public void setData(GameObj game) {
        if (thisGameObj != null) {
            thisGameObj.getPlayerList().stopLoading();
        }
        this.thisGameObj = game;
        thisGameObj.getPlayerList().loadList(listener);

    }

    private void afterUserAuth(AppUser appUser) {
        Log.d(TAG, "afterUserAuth: " + thisGameObj.getPlayerList().hasJoined(appUser));
        if (thisGameObj.getPlayerList().hasJoined(appUser)) {
            thisGameObj = appUser.removeFootballGame(thisGameObj);
        } else {
            thisGameObj = appUser.joinToFootballGame(thisGameObj);
        }
    }


    /*
    private void updateView() {
        ArrayList<UserObj> list = thisGameObj.getPlayerList().getList();
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
    */


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UserAuth.AUTH_REQUEST_CODE) {
            switch (resultCode) {
                case UserAuth.RESULT_SUCCESS:
                    AppUser appUser = AppUser.getInstance(getContext(), false);
                    if (appUser != null) {
                        afterUserAuth(appUser);
                    }
                    break;
            }
        }

    }
}
