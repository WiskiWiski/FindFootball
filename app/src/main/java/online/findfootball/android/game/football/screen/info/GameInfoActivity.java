package online.findfootball.android.game.football.screen.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.app.BaseActivity;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseLoader;
import online.findfootball.android.firebase.database.DatabasePackableInterface;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.screen.info.tabs.about.GIAboutTab;
import online.findfootball.android.game.football.screen.info.tabs.chat.GIChatTab;
import online.findfootball.android.game.football.screen.info.tabs.players.GIPlayersTab;
import online.findfootball.android.user.UserObj;

public class GameInfoActivity extends BaseActivity {

    private static final String TAG = App.G_TAG + ":GameInfoAct";

    public static final String INTENT_GAME_KEY = "game_in_intent";
    public static final String INTENT_TAB_NUM_KEY = "tab_num";

    private GIAboutTab aboutTab;
    private GIPlayersTab playersTab;
    private GIChatTab chatTab;

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

            thisGameObj.load(new DatabaseLoader.OnLoadListener() {
                @Override
                public void onComplete(DataInstanceResult result, DatabasePackableInterface packable) {
                    if (result.getCode() == DataInstanceResult.CODE_SUCCESS) {
                        thisGameObj = (GameObj) packable;
                        setupViewPager(viewPager);
                        tabLayout.setupWithViewPager(viewPager);
                        if (aboutTab != null) {
                            aboutTab.setData(thisGameObj);
                        }
                        if (playersTab != null) {
                            playersTab.setData(thisGameObj);
                        }
                        if (chatTab != null) {
                            chatTab.setData(thisGameObj);
                        }
                    } else {
                        Log.e(TAG, "onFailed [" + result.getCode() + "]: " + result.getMessage(), result.getCause());
                    }
                }
            });

        } else {
            intentDataNotFound();
        }
    }

    private void intentDataNotFound() {
        Log.e(TAG, "onCreate: you must put game object into intent!");
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new TabsAdapter(getSupportFragmentManager());
        aboutTab = new GIAboutTab();
        chatTab = new GIChatTab();
        playersTab = new GIPlayersTab();

        mAdapter.addFragment(aboutTab, getString(R.string.game_info_activity_tab_title_about));
        mAdapter.addFragment(chatTab, getString(R.string.game_info_activity_tab_title_chat));
        mAdapter.addFragment(playersTab, getString(R.string.game_info_activity_tab_title_players));
        viewPager.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (playersTab != null) {
            playersTab.onActivityResult(requestCode, resultCode, data);
        }
    }
}
