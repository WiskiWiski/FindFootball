package org.blackstork.findfootball.lacation.gmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.lacation.gmaps.fragments.LocationSelectFragment;
import org.blackstork.findfootball.lacation.gmaps.fragments.LocationViewFragment;

public class GMapsActivity extends AppCompatActivity implements
        LocationSelectFragment.OnLocationSelectListener {

    public static final String TAG = App.G_TAG + ":GMapsAct";

    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1;
    public static final String LAT_LNG_LOCATION = "lat_lng_result";

    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final int REQUEST_SELECTOR = 1;
    public static final int REQUEST_VIEWER = 2;

    private int mapFragmentType;

    /*
            Executing GMapsActivity
        Location Selector:
            Intent gmapsIntent = new Intent(getApplicationContext(), GMapsActivity.class);
            gmapsIntent.putExtra(GMapsActivity.FRAGMENT_TYPE, GMapsActivity.REQUEST_SELECTOR);
            startActivityForResult(gmapsIntent, GMapsActivity.REQUEST_SELECTOR);

        Point Viewer:
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(LocationViewFragment.MARKER_OPTIONS_LIST, ArrayList <MarkerOptions>);
                или
            bundle.putParcelable(LocationViewFragment.MARKER_OPTIONS, MarkerOptions);

            Intent gmapsIntent = new Intent(getApplicationContext(), GMapsActivity.class);
            gmapsIntent.putExtra(GMapsActivity.FRAGMENT_TYPE, GMapsActivity.REQUEST_VIEWER);
            gmapsIntent.putExtra(App.INTENT_BUNDLE, bundle);
            startActivityForResult(gmapsIntent, GMapsActivity.REQUEST_VIEWER);
     */

/*
        // Catching callback from GMapsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        You must write onActivityResult() in your FirstActivity.Java as follows
//
//        @Override
//        public void onActivityResult(int requestCode, int resultCode, Intent data) {
//           super.onActivityResult(requestCode, resultCode, data);
//        }
//        So this will call your fragment's onActivityResult()
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
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmaps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // запрос прав для android 6 и выше
            // тут подробнее https://developer.android.com/training/permissions/requesting.html
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSIONS_REQUEST_CODE);
        }


        Intent intent = getIntent();
        if (intent != null) {
            mapFragmentType = intent.getIntExtra(FRAGMENT_TYPE, 0);
            if (mapFragmentType == REQUEST_SELECTOR) {
                // Start LocationSelectFragment
                LocationSelectFragment selectLocationFrg = LocationSelectFragment.newInstance();
                setFragment(selectLocationFrg);

            } else if (mapFragmentType == REQUEST_VIEWER) {
                // Start LocationViewFragment
                LocationViewFragment locationViewFrg =
                        LocationViewFragment.newInstance(intent.getBundleExtra(App.INTENT_BUNDLE));
                setFragment(locationViewFrg);
            }

        } else {
            Log.e(TAG, "Got null intent params!");
            finish();
        }


    }

    private void setFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.root_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mapFragmentType == REQUEST_SELECTOR) {
            // Сообщаем об отмене выбора, если запрашивали СЕЛЕКТОР
            setResult(LocationSelectFragment.CANCEL, null);
        }
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), getString(R.string.gmaps_permissions_not_granted),
                            Toast.LENGTH_LONG).show();
                    finish();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }


    @Override
    public void onLocationSelect(LatLng latLng) {
        Intent intent = new Intent();
        Bundle data = new Bundle();
        data.putParcelable(LAT_LNG_LOCATION, latLng);
        intent.putExtra(App.INTENT_BUNDLE, data);
        setResult(LocationSelectFragment.LOCATION, intent);
        finish();
    }

}
