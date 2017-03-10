package org.blackstork.findfootball.lacation.device;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.lacation.device.providers.UniversalLocationProvider;


/**
 * Created by WiskiW on 02.03.2017.
 */

@SuppressWarnings("MissingPermission")
public class DeviceLocationManager {

    public static final String TAG = App.G_TAG + ":DeviceLocation";

    private Context context;
    private DeviceLocationListener callback;
    private LocationManager locationManager;

    private UniversalLocationProvider networkLocationProvider;
    private UniversalLocationProvider gpsLocationProvider;

    private int providersFailed = 0;


    public DeviceLocationManager(Context context) {
        this.context = context;
        if (isAccessFineLocation() && isAccessCoarseLocation()) { // проверка на доступ к правам
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            gpsLocationProvider = new UniversalLocationProvider(locationManager, this);
            networkLocationProvider = new UniversalLocationProvider(locationManager, this);
        } else {
            Log.e(TAG, "Access denied to device location");
        }
    }

    public void failed() {
        providersFailed++;
        if (providersFailed == 2 && gpsLocationProvider != null) {
            // если оба провайдера облажались, берем последние известные координаты
            LatLng location = getLastKnownLocation();
            if (location != null) {
                callback.onDeviceLocationReceived(location);
            } else {
                callback.onFailed("Failed to get device location");
            }

        }
    }

    private LatLng getLastKnownLocation() {
        // Перебор всех доступны провайдеров местоположения
        // И выбор лучшей точности
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) return null;
        Log.d(TAG, "Receiving last known " + bestLocation.getProvider().toUpperCase() + " location");
        return new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
    }


    public void requestDeviceLocation(DeviceLocationListener callback) {
        this.callback = callback;
        if (gpsLocationProvider != null && networkLocationProvider != null) {
            gpsLocationProvider.requestLocation(LocationManager.GPS_PROVIDER);
            networkLocationProvider.requestLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    public void cancelRequest() {
        if (gpsLocationProvider != null && networkLocationProvider != null) {
            gpsLocationProvider.cancelRequest();
            networkLocationProvider.cancelRequest();
        }
    }

    public void locationReceived(Location location) {
        // тут можно выбирать местоположение по качеству, или еще чему.
        // но я верну первое полученое
        Log.d(TAG, "Device location received (provider: " + location.getProvider().toUpperCase() + ")");
        if (callback != null) {
            cancelRequest();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            callback.onDeviceLocationReceived(latLng); // возвращаем координаты
        }
    }


    private boolean isAccessFineLocation() {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isAccessCoarseLocation() {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

}
