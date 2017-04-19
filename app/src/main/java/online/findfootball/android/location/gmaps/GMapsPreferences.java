package online.findfootball.android.location.gmaps;

/**
 * Created by WiskiW on 10.03.2017.
 */

@SuppressWarnings("WeakerAccess")
public class GMapsPreferences {

        /* === Bundle Tags === */
    public static final String BUNDLE_CAMERA_POS = "camera_pos";
    public static final String BUNDLE_MARKER_POS = "marker_pos";
    public static final String BUNDLE_MAP_SCALE = "map_scale";

    public static final float DEVICE_LOCATION_SCALE = 10f; // масштаб при отображении маркера-устройства
    public static final float MARKER_SCALE = 5f; // масштаб при установке одного маркера

}
