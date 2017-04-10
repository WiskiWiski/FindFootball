package org.blackstork.findfootball;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.app.BaseActivity;
import org.blackstork.findfootball.auth.UserAuth;
import org.blackstork.findfootball.create.game.CreateGameActivity;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = App.G_TAG + ":MapsActivity";


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
        setContentView(R.layout.activity_main);
        // регистрируем главный NavDraw.ItemClickListener и передаем menu-id главного экрана
        registerRootActivity(this, R.id.nav_main);
        initToolbar();

        if (savedInstanceState == null) {
            UserAuth.updateLastUserOnline(this);
        }

        findViewById(R.id.test_btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: current U: " + UserAuth.getUser(getApplicationContext()));
                Toast.makeText(getApplicationContext(), "U: " + UserAuth.getUser(getApplicationContext()), Toast.LENGTH_SHORT).show();
/*
                GameObj gameObj = new GameObj();
                gameObj.setTitle("This is title");
                gameObj.setDescription("This is description");
                gameObj.setLocation(new LatLng(55.53505845593329, 28.582239858806137));
*/
            }
        });

        findViewById(R.id.test_btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAuth.signOut();
            }
        });

        findViewById(R.id.test_btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAuth.requestUser(view.getContext());
            }
        });
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
                    //setAFragment();

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

}

