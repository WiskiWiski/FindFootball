package org.blackstork.findfootball.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import org.blackstork.findfootball.app.App;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by WiskiW on 07.04.2017.
 */

public class LocationHelper {


    public static final String TAG = App.G_TAG + ":LocationHelper";


    public static Address getStringAddress(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                /*
                Address address = addresses.get(0);
                Log.d(TAG, "getStringAddress:: " + address.getLocality()); // Navapolatsk
                Log.d(TAG, "getStringAddress:: " + address.getCountryName()); // Belarus, Russia
                Log.d(TAG, "getStringAddress:: " + address.getCountryCode()); // BY, RU
                Log.d(TAG, "getStringAddress:: " + address.getSubAdminArea()); // Polatsk District, null

                Log.d(TAG, "getStringAddress:: " + address.getAddressLine(0)); // vulica Vasilieŭcy 8, Goncharnaya ulitsa, 4 строение 4
                Log.d(TAG, "getStringAddress:: " + address.getAddressLine(1)); // Navapolack, Moskva
                Log.d(TAG, "getStringAddress:: " + address.getAddressLine(2)); // Belarus, Russia
                */
                return addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
