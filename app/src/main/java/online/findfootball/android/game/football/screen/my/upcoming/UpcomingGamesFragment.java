package online.findfootball.android.game.football.screen.my.upcoming;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.screen.create.CreateGameActivity;
import online.findfootball.android.game.football.screen.info.GameInfoActivity;
import online.findfootball.android.game.football.screen.my.EventsProvider;
import online.findfootball.android.game.football.screen.my.OnRecyclerViewItemClickListener;
import online.findfootball.android.user.AppUser;

public class UpcomingGamesFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = App.G_TAG + ":UpcGamesFrg";
    public static final String F_TAG = "upcoming_games_frg";

    public UpcomingGamesFragment() {
        // Required empty public constructor
    }

    public static UpcomingGamesFragment newInstance() {
        return new UpcomingGamesFragment();
    }


    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UpcomingGamesAdapter mAdapter;
    private FloatingActionButton addFab;

    private EventsProvider eventsProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upcoming_games, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        addFab = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateGameActivity.class));
            }
        });

        if (mAdapter == null) {
            mAdapter = new UpcomingGamesAdapter();
            mAdapter.setItemClickListener(getItemClickListener());
            mAdapter.setItemLongClickListener(getItemLongClickListener());
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        return rootView;
    }

    private void fillAdapter() {
        if (mAdapter.getItemCount() == 0) {
            Context context = getContext();
            AppUser appUser = AppUser.getInstance(context);
            if (appUser != null) {
                swipeRefreshLayout.setRefreshing(true);
                eventsProvider = new EventsProvider(
                        appUser, new EventsProvider.EventsProviderListener() {
                    @Override
                    public void onProgress(GameObj gameObj) {
                        mAdapter.addGame(gameObj);
                    }

                    @Override
                    public void onSuccess(List<GameObj> gameList) {
                        if (gameList.size() == 0) {
                            Toast.makeText(getContext(), getString(R.string.upcoming_games_fragment_no_data),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (mAdapter.getItemCount() != gameList.size()) {
                                mAdapter.setGameList(gameList);
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        Log.w(TAG, "onFailed [" + code + "] : " + msg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                eventsProvider.getUpcomingGames();
            }
        }
    }

    private OnRecyclerViewItemClickListener getItemClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getContext(), GameInfoActivity.class);
                intent.putExtra(GameInfoActivity.INTENT_GAME_KEY, (Parcelable) mAdapter.getGameList().get(pos));
                startActivity(intent);
            }
        };
    }

    private OnRecyclerViewItemClickListener getItemLongClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(final int pos) {
                final String[] actionsTitles = {getString(R.string.context_menu_item_delete_title)};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.context_menu_title));
                builder.setItems(actionsTitles, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                deleteEvent(pos);
                                break;
                            default:
                                Log.d(TAG, "onClick: unknown action: " + actionsTitles[item] + " p:" + pos);
                        }
                    }
                }).setNegativeButton(getString(R.string.context_menu_btn_cancel_title), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        };
    }

    private void deleteEvent(int pos) {
        Context context = getContext();
        AppUser appUser = AppUser.getInstance(context);
        if (appUser != null) {
            final GameObj game = mAdapter.getGameList().get(pos);
            mAdapter.removeGame(pos);

            appUser.leaveGame(game);
            game.delete();
        }

    }

    @Override
    public void onRefresh() {
        mAdapter.setGameList(new ArrayList<GameObj>());
        fillAdapter();
    }

    @Override
    public void onPause() {
        if (eventsProvider != null) {
            eventsProvider.abortLoading();
            swipeRefreshLayout.setRefreshing(false);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillAdapter();
    }
}
