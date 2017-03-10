package org.blackstork.findfootball.lacation.device;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by WiskiW on 02.03.2017.
 */

public interface DeviceLocationListener {

    void onDeviceLocationReceived(LatLng latLng);

    void onFailed(String msg);

}
