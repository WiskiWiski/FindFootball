package org.blackstork.findfootball.events.upcoming;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.auth.UserAuth;
import org.blackstork.findfootball.events.EventsProvider;
import org.blackstork.findfootball.objects.GameObj;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
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
    private UpcomingGAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upcoming_games, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        if (mAdapter == null) {
            mAdapter = new UpcomingGAdapter();
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setRefreshing(true);
        fillAdapter();

        return rootView;
    }

    private void fillAdapter() {
        if (mAdapter.getItemCount() == 0) {
            Context context = getContext();
            FirebaseUser user = UserAuth.getUser(context);
            if (user != null) {
                EventsProvider eventsProvider = new EventsProvider(context,
                        user.getUid(), new EventsProvider.EventsProviderListener() {
                    @Override
                    public void onProgress(GameObj gameObj) {
                        mAdapter.addGame(gameObj);
                    }

                    @Override
                    public void onSuccess(List<GameObj> gameList) {
                        Log.w(TAG, "onSuccess: list size: " + gameList.size());
                        if (mAdapter.getItemCount() != gameList.size()) {
                            mAdapter.setGameList(gameList);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        Log.d(TAG, "onFailed [" + code + "] : " + msg);
                    }
                });
                eventsProvider.getUpcomingGames();
            } else {
                UserAuth.requestUser(context);
            }
        }
    }

    @Override
    public void onRefresh() {
        mAdapter.setGameList(new ArrayList<GameObj>());
        fillAdapter();
    }
}
