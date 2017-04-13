package org.blackstork.findfootball;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.app.BaseActivity;
import org.blackstork.findfootball.auth.UserAuth;
import org.blackstork.findfootball.firebase.database.FBCompleteListener;
import org.blackstork.findfootball.firebase.database.FBFootballDatabase;

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
                Context context = getApplicationContext();
                FBFootballDatabase database = FBFootballDatabase.newInstance(context);
                FBCompleteListener completeListener = new FBCompleteListener() {
                    @Override
                    public void onSuccess(Object object) {
                        Log.d(TAG, "onSuccess: ");
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        Log.d(TAG, "onFailed: ");
                    }
                };
                database.readGame(completeListener, "7d117fed-1d8b-429f-9196-41a7e88e4672");


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

