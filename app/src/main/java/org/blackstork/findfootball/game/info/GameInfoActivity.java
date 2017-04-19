package org.blackstork.findfootball.game.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.app.BaseActivity;
import org.blackstork.findfootball.firebase.database.DatabaseInstance;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.game.info.tabs.GIAboutTab;
import org.blackstork.findfootball.game.info.tabs.GIPlayersTab;
import org.blackstork.findfootball.user.UserObj;

public class GameInfoActivity extends BaseActivity {

    private static final String TAG = App.G_TAG + ":GameInfoAct";

    public static final String INTENT_GAME_KEY = "game_in_intent";
    public static final String INTENT_TAB_NUM_KEY = "tab_num";

    private GIAboutTab aboutTab;
    private GIPlayersTab playersTab;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAdapter mAdapter;

    private GameObj thisGameObj;
    private UserObj thisGameOwnerUser;

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);
        initToolbar();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        Intent intent = getIntent();
        if (intent != null) {
            thisGameObj = intent.getParcelableExtra(INTENT_GAME_KEY);
            if (thisGameObj == null) {
                intentDataNotFound();
            }
            getSupportActionBar().setTitle(thisGameObj.getTitle());
            int tabNum = intent.getIntExtra(INTENT_TAB_NUM_KEY, -1);
            if (tabNum >= 0 && tabNum < mAdapter.getCount()) {
                viewPager.setCurrentItem(tabNum);
            }

            thisGameOwnerUser = new UserObj(thisGameObj.getOwnerUid());
            loadUser();

        } else {
            intentDataNotFound();
        }
    }

    private void loadUser(){
        if (thisGameOwnerUser == null || thisGameOwnerUser.isLoading()){
            return;
        }
        thisGameOwnerUser.load(new DatabaseInstance.OnLoadListener() {
            @Override
            public void onSuccess(DatabaseInstance instance) {
                thisGameOwnerUser = (UserObj) instance;
                onOwnerLoadComplete();
            }

            @Override
            public void onFailed(int code, String msg) {
                onOwnerLoadComplete();
            }
        });
    }

    private void onOwnerLoadComplete() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        if (aboutTab != null) {
            aboutTab.setData(thisGameObj, thisGameOwnerUser);
        }
        if (playersTab != null) {
            playersTab.setData(thisGameObj);
        }
    }

    private void intentDataNotFound() {
        Log.e(TAG, "onCreate: you must put game object into intent!");
        finish();
    }


    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new TabsAdapter(getSupportFragmentManager());
        aboutTab = new GIAboutTab();
        playersTab = new GIPlayersTab();

        mAdapter.addFragment(aboutTab, "About");
        mAdapter.addFragment(playersTab, "Players");
        viewPager.setAdapter(mAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        finish();
        return super.onNavigationItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (playersTab != null) {
            playersTab.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        if (thisGameOwnerUser != null){
            thisGameOwnerUser.abortLoading();
        }
        thisGameOwnerUser = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (thisGameOwnerUser != null && !thisGameOwnerUser.hasLoaded()){
            loadUser();
        }
    }
}
