package online.findfootball.android.location.gmaps;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import online.findfootball.android.app.App;
import online.findfootball.android.location.device.DeviceLocationListener;
import online.findfootball.android.location.device.DeviceLocationManager;


/**
 * Created by WiskiW on 02.03.2017.
 */

public class LocationPicker implements OnMapReadyCallback, DeviceLocationListener, GoogleMap.OnMarkerDragListener {

    private static final String TAG = App.G_TAG + ":LocationPicker";

    private static final int MARKER_DRAG_VIBRATION = 40;

    private static final int MARKER_LONG_CLICK_VIBRATION = 30;

    private String deviceLocationTitle;
    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private Marker marker;
    private LatLng deviceLatLngLocation;
    private DeviceLocationManager deviceLocationManager;
    private Context context;
    private float scale = GMapsPreferences.MARKER_SCALE;
    private LatLng cameraPos;

    public LocationPicker(Context context, SupportMapFragment mapFragment) {
        this.context = context;
        deviceLocationManager = new DeviceLocationManager(context);
        deviceLocationManager.requestDeviceLocation(this);
        mapFragment.getMapAsync(this);
    }

    public void setDeviceLocationTitle(String deviceLocationTitle) {
        this.deviceLocationTitle = deviceLocationTitle;
    }


    public void setMarker(MarkerOptions markerOptions) {
        deviceLocationManager.cancelRequest(); // отменям запрос, т.к. теперь у нас есть пользовательский маркер
        this.markerOptions = markerOptions
                .draggable(true);
        drawMarker(markerOptions);
    }

    private void drawMarker(MarkerOptions markerOptions) {
        if (mMap != null) {
            if (marker != null) marker.remove(); // удаляем предыдущий маркер
            marker = mMap.addMarker(markerOptions);
        }
    } // отрисовка маркера на карте

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        if (mMap != null) {
            scale = mMap.getCameraPosition().zoom;
        }
        return scale;
    }

    public void moveCamera(LatLng position, float scale) {
        if (mMap != null) {
            if (scale == 0) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
            } else {
                this.scale = scale;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, scale));
            }
        }
    }

    public void setCamera(LatLng position, float scale) {
        if (mMap != null) {
            if (scale == 0) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
            } else {
                this.scale = scale;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, scale));
            }
        }
    }

    public boolean isMapReady() {
        return mMap != null;
    }

    public LatLng getMarkerPosition() {
        if (marker == null) return null;
        return marker.getPosition();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                vibrate(MARKER_LONG_CLICK_VIBRATION);
                MarkerOptions markerOptions = new MarkerOptions()
                        .draggable(true)
                        .position(latLng);
                drawMarker(markerOptions);
            }
        });
        mMap.setOnMarkerDragListener(this);
        if (markerOptions != null) {
            drawMarker(markerOptions);
            if (cameraPos == null){
                cameraPos = markerOptions.getPosition();
                moveCamera(cameraPos, scale);
            } else {
                setCamera(cameraPos, scale);
            }
        } else if (deviceLatLngLocation != null) {
            addDeviceLocationOnMap(); // если карта прорисовалась после того, как получили координаты устройства
        }
    }


    @Override
    public void onMarkerDragStart(Marker marker) {
        vibrate(MARKER_DRAG_VIBRATION);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    public void destroy() {
        if (deviceLocationManager != null) {
            deviceLocationManager.cancelRequest();
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        this.marker = marker; // обновляем сохраненный маркер
        vibrate(MARKER_DRAG_VIBRATION);
    }

    private void addDeviceLocationOnMap() {
        MarkerOptions markerOptions = new MarkerOptions()
                .draggable(true)
                .position(deviceLatLngLocation)
                .title(deviceLocationTitle);
        drawMarker(markerOptions);
        moveCamera(markerOptions.getPosition(), GMapsPreferences.DEVICE_LOCATION_SCALE);
    }

    @Override
    public void onDeviceLocationReceived(LatLng latLng) {
        deviceLatLngLocation = latLng;
        if (mMap != null) {
            addDeviceLocationOnMap(); // если коорды пользователя подъехали после onMapReady
        }
    }

    @Override
    public void onFailed(String msg) {
        Log.w(TAG, "Device location onFailed: " + msg);
        deviceLocationManager.cancelRequest();
    }

    private void vibrate(int duration) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(duration);
    }

    public LatLng getCameraPosition() {
        if (mMap != null)
            cameraPos =  mMap.getCameraPosition().target;
        return cameraPos;
    }

    public void setCameraPosition(LatLng cameraPos) {
        this.cameraPos = cameraPos;
    }
}
