package org.blackstork.findfootball;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.app.BaseActivity;
import org.blackstork.findfootball.location.gmaps.fragments.LocationSelectFragment;
import org.blackstork.findfootball.location.gmaps.fragments.LocationViewFragment;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LocationSelectFragment.OnLocationSelectListener {

    private static final String TAG = App.G_TAG + ":MapsActivity";

    private LocationSelectFragment locationSelectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onNavigationItemSelected(getCurrentMenuItem());

        locationSelectFragment = LocationSelectFragment.newInstance(R.id.test_btn_3);

        findViewById(R.id.test_btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.root_container, locationSelectFragment)
                        .commit();

            }
        });

        findViewById(R.id.test_btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.root_container, LocationViewFragment
                                .newInstance(new MarkerOptions().position(new LatLng(50, -46.684))))
                        .commit();
            }
        });


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


        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        super.onNavigationClick(item);

        // Handle MainActivity's fragments navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_main:
                //setAFragment();

                break;
            default:
                return super.onNavigationItemSelected(item);
        }
        return true;
    }

    @Override
    public void onLocationSelect(LatLng latLng) {
        Log.d(TAG, "onLocationSelect: " + latLng.latitude);
        Toast.makeText(getApplicationContext(), "new loc latitude: " + latLng.latitude,
                Toast.LENGTH_LONG).show();
    }
}
