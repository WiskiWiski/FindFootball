package online.findfootball.android.game.football.screen.find;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.List;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.app.NavDrawerActivity;
import online.findfootball.android.background.tasks.BgTaskMaker;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.screen.create.CreateGameActivity;
import online.findfootball.android.game.football.screen.find.dialogs.FGSelectLocationDialog;
import online.findfootball.android.game.football.screen.find.recyclerview.EndlessRecyclerOnScrollListener;
import online.findfootball.android.game.football.screen.find.recyclerview.FindGameAdapter;
import online.findfootball.android.game.football.screen.info.GameInfoActivity;
import online.findfootball.android.game.football.screen.my.OnRecyclerViewItemClickListener;
import online.findfootball.android.location.LocationObj;

public class FindGameActivity extends NavDrawerActivity {

    private static final String TAG = App.G_TAG + ":FindGameAct";


    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FindGameAdapter mAdapter;
    private Button applyButton;
    private FloatingActionButton addFab;

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
                findGameDataProvider.abortLoading();
                findGameDataProvider.reset();
                mAdapter.clear();
                searchLocation = location;
                loadData();
            }

            @Override
            public void onCancel() {

            }
        });

        addFab = (FloatingActionButton) findViewById(R.id.fab_add);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateGameActivity.class));
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
                //Log.w(TAG, "onSuccess: " + gameList.size());
            }

            @Override
            public void onFailed(int code, String msg) {
                progressBar.setVisibility(View.GONE);
                switch (code) {
                    case FindGameDataProvider.CODE_NO_DATA:
                        Toast.makeText(getApplication(), getString(R.string.find_game_activity_no_data), Toast.LENGTH_LONG).show();
                        break;
                    /*
                    case FindGameDataProvider.CODE_NO_MORE_DATA:
                        Toast.makeText(getApplication(), getString(R.string.find_game_activity_no_more_data), Toast.LENGTH_LONG).show();

                        break;
                    */
                    default:
                        Log.w(TAG, "onFailed: " + msg);
                        if (code == FindGameDataProvider.CODE_NO_MORE_DATA) {
                            return;
                        }
                        Toast.makeText(getApplication(), "onFailed: " + msg, Toast.LENGTH_LONG).show();
                }
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
            BgTaskMaker.newMaker(getApplicationContext())
                    .setCompleteListener(new BgTaskMaker.OnBgCompleteListener() {
                        @Override
                        public void onComplete() {
                            findGameDataProvider.setLocation(searchLocation);
                            findGameDataProvider.loadData();
                        }
                    }).make(searchLocation);
        } else {
            findGameDataProvider.loadData();
        }
    }

    @Override
    public void onPause() {
        if (findGameDataProvider != null) {
            findGameDataProvider.abortLoading();
            progressBar.setVisibility(View.GONE);
        }
        super.onPause();
    }

    private OnRecyclerViewItemClickListener getItemClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent(getApplicationContext(), GameInfoActivity.class);
                intent.putExtra(GameInfoActivity.INTENT_GAME_KEY, (Parcelable) mAdapter.getGameList().get(pos));
                startActivity(intent);
            }
        };
    }

    private OnRecyclerViewItemClickListener getItemLongClickListener() {
        return new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(final int pos) {
                Toast.makeText(getApplicationContext(), getString(R.string.popup_msg_coming_soon), Toast.LENGTH_SHORT).show();
            }
        };
    }


}
