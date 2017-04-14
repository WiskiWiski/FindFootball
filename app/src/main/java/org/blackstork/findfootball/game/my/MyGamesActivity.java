package org.blackstork.findfootball.game.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.blackstork.findfootball.MainActivity;
import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.app.BaseActivity;
import org.blackstork.findfootball.auth.UserAuth;
import org.blackstork.findfootball.game.create.CreateGameActivity;
import org.blackstork.findfootball.game.my.archived.ArchivedGamesFragment;
import org.blackstork.findfootball.game.my.upcoming.UpcomingGamesFragment;

public class MyGamesActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        BaseActivity.OnRootActivity {

    private static final String TAG = App.G_TAG + ":MyGamesAct";

    private UpcomingGamesFragment upcomingGamesFragment;
    private ArchivedGamesFragment archivedGamesFragment;

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void setToolbarTitle(String title) {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_games);
        initToolbar();
        if (savedInstanceState == null) {
            UserAuth.updateLastUserOnline(this);
            // регистрируем главный NavDraw.ItemClickListener и передаем menu-id главного экрана
            registerRootActivity(this);
            addRootActivityChildes(R.id.nav_upcoming_games, R.id.nav_archived_games);
            setDefaultMenuItemId(R.id.nav_upcoming_games);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {


        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        // Получаем menuItem из Navigation Drawer текущей активити, а не той, откуда он прищел
        menuItem = getMenuItemById(itemId);

        closeDrawer();
        if (getCurrentMenuItemId() == itemId) {
            return false;
        } else {
            super.updateMenuItemSelection(menuItem); // Обновляем выделение в Navigation Drawer
            super.updateMenuItemId(itemId); // Обновляем current menu item id

            switch (itemId) {
                case R.id.nav_main:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    break;

                case R.id.nav_upcoming_games:
                    setUpcomingGamesFragment();
                    break;

                case R.id.nav_archived_games:
                    setArchivedGamesFragment();
                    break;

                case R.id.nav_create_game:
                    startActivity(new Intent(getApplicationContext(), CreateGameActivity.class));
                    break;

                default:
                    return false;
            }
            return true;
        }
    }

    private void setUpcomingGamesFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.root_container, getUpcomingGamesFragment(), UpcomingGamesFragment.F_TAG)
                .commitAllowingStateLoss();
        setToolbarTitle(getString(R.string.my_games_activity_title_upcoming_games));
    }

    private void setArchivedGamesFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.root_container, getArchivedGamesFragment(), ArchivedGamesFragment.F_TAG)
                .commitAllowingStateLoss();
        setToolbarTitle(getString(R.string.my_games_activity_title_archived_games));
    }

    private UpcomingGamesFragment getUpcomingGamesFragment() {
        if (getIntent().getExtras() != null) {
            upcomingGamesFragment = (UpcomingGamesFragment) getSupportFragmentManager()
                    .findFragmentByTag(UpcomingGamesFragment.F_TAG);
        }
        if (upcomingGamesFragment == null) {
            upcomingGamesFragment = UpcomingGamesFragment.newInstance();
        }
        return upcomingGamesFragment;
    }

    private ArchivedGamesFragment getArchivedGamesFragment() {
        if (getIntent().getExtras() != null) {
            archivedGamesFragment = (ArchivedGamesFragment) getSupportFragmentManager()
                    .findFragmentByTag(ArchivedGamesFragment.F_TAG);
        }
        if (archivedGamesFragment == null) {
            archivedGamesFragment = ArchivedGamesFragment.newInstance();
        }
        return archivedGamesFragment;
    }

    @Override
    public int getCurrentViewItemId() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root_container);
        if (fragment instanceof UpcomingGamesFragment) {
            return R.id.nav_upcoming_games;
        } else if (fragment instanceof ArchivedGamesFragment) {
            return R.id.nav_archived_games;
        } else {
            return BaseActivity.getCurrentMenuItemId();
        }
    }
}
