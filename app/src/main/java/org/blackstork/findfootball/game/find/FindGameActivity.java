package org.blackstork.findfootball.game.find;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.app.BaseActivity;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.game.find.recyclerview.EndlessRecyclerOnScrollListener;
import org.blackstork.findfootball.game.find.recyclerview.FindGameAdapter;
import org.blackstork.findfootball.game.my.*;

import java.util.ArrayList;
import java.util.List;

public class FindGameActivity extends BaseActivity {

    private static final String TAG = App.G_TAG + ":FindGameAct";


    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FindGameAdapter mAdapter;
    private Button applyButton;

    private FindGameDataProvider findGameDataProvider;

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);
        initToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        applyButton = (Button) findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findGameDataProvider.loadData();
            }
        });

        if (mAdapter == null) {
            mAdapter = new FindGameAdapter(new ArrayList<GameObj>());
            mAdapter.setItemClickListener(getItemClickListener());
            mAdapter.setItemLongClickListener(getItemLongClickListener());
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        findGameDataProvider = new FindGameDataProvider(getApplicationContext(),
                new FindGameDataProvider.EventsProviderListener() {
            @Override
            public void onProgress(GameObj gameObj) {
                mAdapter.addGame(gameObj);
            }

            @Override
            public void onSuccess(List<GameObj> gameList) {
                Log.w(TAG, "onSuccess: " + gameList.size());
            }

            @Override
            public void onFailed(int code, String msg) {
                Log.d(TAG, "onFailed: " + msg);
            }

        });


        findGameDataProvider.setCity("Минск");
        findGameDataProvider.setCountry("Беларусь");

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.w(TAG, "onLoadMore: " + current_page);
                findGameDataProvider.loadData();
            }
        });
        findGameDataProvider.loadData();


    }


    private OnRecyclerViewItemClickListener getItemClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int pos) {
                Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private OnRecyclerViewItemClickListener getItemLongClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(final int pos) {
                Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
            }
        };
    }


}
