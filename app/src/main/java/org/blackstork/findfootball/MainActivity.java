package org.blackstork.findfootball;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.auth.AuthUiActivity;
import org.blackstork.findfootball.auth.UserAuth;
import org.blackstork.findfootball.lacation.gmaps.GMapsActivity;
import org.blackstork.findfootball.lacation.gmaps.fragments.LocationSelectFragment;
import org.blackstork.findfootball.lacation.gmaps.fragments.LocationViewFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = App.G_TAG + ":MapsActivity";

    private UserAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userAuth = UserAuth.getInstance(this);
        findViewById(R.id.test_btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userAuth.requestUser(new Intent(getApplicationContext(), MainActivity.class))){
                    FirebaseUser user = userAuth.getUser();
                    Log.d(TAG, "onClick: user name: " + user.getDisplayName());
                    Toast.makeText(getApplicationContext(), user.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.test_btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuth.signOut();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        userAuth.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userAuth.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        You must write onActivityResult() in your FirstActivity.Java as follows

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
           super.onActivityResult(requestCode, resultCode, data);
        }
        So this will call your fragment's onActivityResult()
         */
        if (data == null) return;
        switch (requestCode) {
            case GMapsActivity.REQUEST_SELECTOR:
                // ответ с выбора местоположения
                switch (resultCode) {
                    case LocationSelectFragment.LOCATION:
                        // местоположение выбрано
                        LatLng latLng = data.getBundleExtra(App.INTENT_BUNDLE)
                                .getParcelable(GMapsActivity.LAT_LNG_LOCATION);
                        if (latLng != null) {
                            Log.d(TAG, "onLocationSelect: " + latLng.latitude);
                            Toast.makeText(getApplicationContext(), "new loc latitude: " + latLng.latitude,
                                    Toast.LENGTH_LONG).show();
                        }
                        break;

                    case LocationSelectFragment.CANCEL:
                        // выбор местоположения отменен
                        break;
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
