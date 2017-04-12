package org.blackstork.findfootball;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.app.BaseActivity;
import org.blackstork.findfootball.auth.UserAuth;

public class MainActivity extends BaseActivity{

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
}

