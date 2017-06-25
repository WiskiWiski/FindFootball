package online.findfootball.android.game.football.screen.my.archived;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ArchivedGamesFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = App.G_TAG + ":ArchGamesFrg";
    public static final String F_TAG = "archived_games_frg";

    private EventsProvider eventsProvider;


    public ArchivedGamesFragment() {
        // Required empty public constructor
    }

    public static ArchivedGamesFragment newInstance() {
        return new ArchivedGamesFragment();
    }

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArchivedGamesAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_archived_games, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if (mAdapter == null) {
            mAdapter = new ArchivedGamesAdapter();
            mAdapter.setItemClickListener(getItemClickListener());
            mAdapter.setItemLongClickListener(getItemLongClickListener());
            mAdapter.setItemRecreateBtnClickListener(getRecreateBtnClickListener());
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
            AppUser user = AppUser.getInstance(context);
            if (user != null) {
                swipeRefreshLayout.setRefreshing(true);
                eventsProvider = new EventsProvider(
                        user, new EventsProvider.EventsProviderListener() {
                    @Override
                    public void onProgress(GameObj gameObj) {
                        mAdapter.addGame(gameObj);
                    }

                    @Override
                    public void onSuccess(List<GameObj> gameList) {
                        //Log.w(TAG, "onComplete: list size: " + gameList.size());
                        if (gameList.size() == 0) {
                            Toast.makeText(getContext(), getString(R.string.archived_games_fragment_no_data),
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
                    }
                });
                eventsProvider.getArchivedGames();
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

    private OnRecyclerViewItemClickListener getRecreateBtnClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int pos) {
                GameObj gameObj = mAdapter.getGameList().get(pos);
                Intent intent = new Intent(getContext(), CreateGameActivity.class);
                intent.putExtra(CreateGameActivity.INTENT_GAME_KEY, (Parcelable) gameObj);
                startActivity(intent);

            }
        };
    }

    private OnRecyclerViewItemClickListener getItemLongClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(final int pos) {
                Toast.makeText(getContext(), getString(R.string.popup_msg_coming_soon), Toast.LENGTH_SHORT).show();
            }
        };
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
