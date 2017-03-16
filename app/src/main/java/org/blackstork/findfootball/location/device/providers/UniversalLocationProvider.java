package org.blackstork.findfootball.location.device.providers;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.blackstork.findfootball.location.device.DeviceLocationManager;


/**
 * Created by WiskiW on 02.03.2017.
 */

@SuppressWarnings("MissingPermission")
public class UniversalLocationProvider implements LocationListener {


    private static final String TAG = DeviceLocationManager.TAG;
    private DeviceLocationManager owner;
    private LocationManager locationManager;

    public UniversalLocationProvider(LocationManager locationManager, DeviceLocationManager deviceLocationManager) {
        owner = deviceLocationManager;
        this.locationManager = locationManager;
    }

    public void requestLocation(String provider) {
        locationManager.requestLocationUpdates(provider, 1000, 1, this);
    }

    public void cancelRequest() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        owner.locationReceived(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.w(TAG, "Warning: " + provider.toUpperCase() + " provider disabled!");
        owner.failed();
    }

}
