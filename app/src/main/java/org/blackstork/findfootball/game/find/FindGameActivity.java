package org.blackstork.findfootball.game.find;

import android.content.Intent;
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
import org.blackstork.findfootball.game.find.dialogs.FGSelectLocationDialog;
import org.blackstork.findfootball.game.find.recyclerview.EndlessRecyclerOnScrollListener;
import org.blackstork.findfootball.game.find.recyclerview.FindGameAdapter;
import org.blackstork.findfootball.game.info.GameInfoActivity;
import org.blackstork.findfootball.game.my.*;
import org.blackstork.findfootball.location.LocationObj;

import java.util.ArrayList;
import java.util.List;

public class FindGameActivity extends BaseActivity {

    private static final String TAG = App.G_TAG + ":FindGameAct";


    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FindGameAdapter mAdapter;
    private Button applyButton;

    private FindGameDataProvider findGameDataProvider;

    private FGSelectLocationDialog selectLocationDialog;
    private LocationObj searchLocation;

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initDialogs() {
        selectLocationDialog = FGSelectLocationDialog.newInstance();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);
        initToolbar();
        initDialogs();

        selectLocationDialog.setListener(new FGSelectLocationDialog.LocationDialogListener() {
            @Override
            public void onSelect(LocationObj location) {
                mAdapter.clear();
                findGameDataProvider.reset();
                searchLocation = location;
                loadData();
            }

            @Override
            public void onCancel() {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        applyButton = (Button) findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLocationDialog.show(getSupportFragmentManager(), FGSelectLocationDialog.F_TAG);
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


        findGameDataProvider = new FindGameDataProvider(new FindGameDataProvider.EventsProviderListener() {
            @Override
            public void onProgress(GameObj gameObj) {
                mAdapter.addGame(gameObj);
            }

            @Override
            public void onSuccess(List<GameObj> gameList) {
                progressBar.setVisibility(View.GONE);
                Log.w(TAG, "onSuccess: " + gameList.size());
                //Toast.makeText(getApplication(), "onSuccess: " + gameList.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code, String msg) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailed: " + msg);
                // Can't create handler inside thread that has not called Looper.prepare() android
                Toast.makeText(getApplication(), "onFailed: " + msg, Toast.LENGTH_LONG).show();
            }

        });

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                loadData();
            }
        });
        loadData();
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        if (searchLocation != null) {
            searchLocation.loadFullAddress(getApplicationContext(), new LocationObj.LocationListener() {
                @Override
                public void onComplete(int resultCode, LocationObj location, String msg) {
                    Log.d(TAG, "onComplete: city found: " + location.getCityName());
                    Log.d(TAG, "onComplete: country found: " + location.getCountryName());
                    findGameDataProvider.setLocation(location);
                    findGameDataProvider.loadData();
                }
            });
        } else {
            findGameDataProvider.loadData();
        }
    }


    private OnRecyclerViewItemClickListener getItemClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getApplicationContext(), GameInfoActivity.class);
                intent.putExtra(GameInfoActivity.INTENT_GAME_KEY, mAdapter.getGameList().get(pos));
                startActivity(intent);
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
