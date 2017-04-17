package org.blackstork.findfootball.game.my.archived;


import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.user.AppUser;
import org.blackstork.findfootball.user.auth.UserAuth;
import org.blackstork.findfootball.game.create.CreateGameActivity;
import org.blackstork.findfootball.game.info.GameInfoActivity;
import org.blackstork.findfootball.game.my.EventsProvider;
import org.blackstork.findfootball.game.my.OnRecyclerViewItemClickListener;
import org.blackstork.findfootball.firebase.database.FBCompleteListener;
import org.blackstork.findfootball.firebase.database.FBFootballDatabase;
import org.blackstork.findfootball.firebase.database.FBUserDatabase;
import org.blackstork.findfootball.game.GameObj;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArchivedGamesFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = App.G_TAG + ":ArchGamesFrg";
    public static final String F_TAG = "archived_games_frg";

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

        fillAdapter();

        return rootView;
    }

    private void fillAdapter() {
        if (mAdapter.getItemCount() == 0) {
            Context context = getContext();
            AppUser user = AppUser.getInstance(context);
            if (user != null) {
                swipeRefreshLayout.setRefreshing(true);
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
                eventsProvider.getArchivedGames();
            }
        }
    }

    private OnRecyclerViewItemClickListener getItemClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getContext(), GameInfoActivity.class);
                intent.putExtra(GameInfoActivity.INTENT_GAME_KEY, mAdapter.getGameList().get(pos));
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
                intent.putExtra(CreateGameActivity.INTENT_GAME_KEY, gameObj);
                startActivity(intent);

            }
        };
    }

    private OnRecyclerViewItemClickListener getItemLongClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(final int pos) {
                Toast.makeText(getContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void deleteEvent(int pos) {
        Context context = getContext();
        AppUser user = AppUser.getInstance(context);
        if (user != null) {
            final GameObj game = mAdapter.getGameList().get(pos);
            mAdapter.removeGame(pos);
            FBUserDatabase userDatabase = FBUserDatabase.newInstance(context, user.getUid());
            userDatabase.removeFootballEvent(new FBCompleteListener() {
                @Override
                public void onSuccess(Object object) {
                    if (object != null) {
                        String status = (String) object;
                        if (status.equals(FBUserDatabase.USER_ROLE_OWNER)) {
                            FBFootballDatabase footballDatabase = FBFootballDatabase.newInstance(getContext());
                            footballDatabase.deleteGame(game.getEid());
                            Toast.makeText(getContext(), game.getTitle() + " successfully deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailed(int code, String msg) {
                    Toast.makeText(getContext(), game.getTitle() + " successfully deleted", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailed: delete event error!");
                }
            }, game.getEid());
        }

    }

    @Override
    public void onRefresh() {
        mAdapter.setGameList(new ArrayList<GameObj>());
        fillAdapter();
    }

}
