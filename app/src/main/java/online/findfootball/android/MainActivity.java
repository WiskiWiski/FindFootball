package online.findfootball.android;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import online.findfootball.android.app.App;
import online.findfootball.android.app.NavDrawerActivity;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseLoader;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.user.AppUser;
import online.findfootball.android.user.UserObj;

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
                final UserObj user = new UserObj("ETV3kzWioRPEgKgT6afY23wqSiu2");
                user.load(new DatabaseLoader.OnLoadListener() {
                    @Override
                    public void onComplete(DataInstanceResult result, DatabasePackable packable) {
                        final UserObj nappUser = (UserObj) packable;
                        Log.d(TAG, "onClick: sex: " + nappUser.getSex());
                        Log.d(TAG, "onClick: age: " + nappUser.getAge());
                        Log.d(TAG, "onClick: rate: " + nappUser.getRate());
                        Log.d(TAG, "onClick: vk: " + nappUser.getContacts()
                                .getContact(UserObj.UserContacts.ContactKey.VK));
                        Log.d(TAG, "onClick: fb: " + nappUser.getContacts()
                                .getContact(UserObj.UserContacts.ContactKey.FB));
                    }
                });
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
                AppUser.getUser(view.getContext(), true);
            }
        });
    }
}

