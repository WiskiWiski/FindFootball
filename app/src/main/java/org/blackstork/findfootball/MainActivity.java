package org.blackstork.findfootball;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.app.BaseActivity;
import org.blackstork.findfootball.user.AppUser;
import org.blackstork.findfootball.user.auth.UserAuth;

public class MainActivity extends BaseActivity {

    private static final String TAG = App.G_TAG + ":MainAct";


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
        initToolbar();

        findViewById(R.id.test_btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        findViewById(R.id.test_btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUser.signOut();
            }
        });

        findViewById(R.id.test_btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUser.getInstance(view.getContext(), true);
            }
        });
    }
}

