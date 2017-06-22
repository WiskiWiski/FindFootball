package online.findfootball.android;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;

import online.findfootball.android.app.App;
import online.findfootball.android.app.NavDrawerActivity;
import online.findfootball.android.user.AppUser;

public class MainActivity extends NavDrawerActivity {

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
                AppUser appUser = AppUser.getInstance(getApplicationContext(), true);
                if (appUser != null) {
                    appUser.setCloudMessageToken(FirebaseInstanceId.getInstance().getToken());
                    appUser.save();
                }
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

