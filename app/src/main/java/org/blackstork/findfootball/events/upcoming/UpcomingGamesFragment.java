package org.blackstork.findfootball.events.upcoming;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import org.blackstork.findfootball.firebase.database.FBCompleteListener;
import org.blackstork.findfootball.objects.GameObj;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingGamesFragment extends Fragment {

    private static final String TAG = App.G_TAG + ":UpcGamesFrg";
    public static final String F_TAG = "upcoming_games_frg";

    public UpcomingGamesFragment() {
        // Required empty public constructor
    }

    public static UpcomingGamesFragment newInstance() {
        return new UpcomingGamesFragment();
    }


    private RecyclerView recyclerView;
    private UpcomingGAdapter mAdapter;
    private List<GameObj> gameList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upcoming_games, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);


        mAdapter = new UpcomingGAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if (gameList == null || gameList.size() == 0) {
            Context context = getContext();
            FirebaseUser user = UserAuth.getUser(context);
            if (user != null) {
                EventsProvider eventsProvider = new EventsProvider(context, user.getUid(), new FBCompleteListener() {
                    @Override
                    public void onSuccess(Object object) {
                        List<GameObj> gameList = (List<GameObj>) object;
                        Log.w(TAG, "onSuccess: list size: " + gameList.size());
                        mAdapter.setGameList(gameList);
                    }

                    @Override
                    public void onFailed() {

                    }
                });
                eventsProvider.getUpcomingGames();
            } else {
                UserAuth.requestUser(context);
            }
        } else {
            mAdapter.setGameList(gameList);
        }

        return rootView;
    }

}
