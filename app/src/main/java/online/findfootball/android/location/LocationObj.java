package online.findfootball.android.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import online.findfootball.android.background.tasks.BgTask;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.children.PackableObject;

/**
 * Created by WiskiW on 16.04.2017.
 */

public class LocationObj extends PackableObject implements Parcelable, Serializable, BgTask {

    public final static String LOCATION_KEY = "location";

    private final static String PATH_LATITUDE = "latitude";
    private final static String PATH_LONGITUDE = "longitude";
    public final static String PATH_CITY_NAME = "city_name";
    public final static String PATH_COUNTRY_NAME = "country_name";
    public final static String PATH_ADDRESS_NAME = "address";

    private double latitude;
    private double longitude;
    private String cityName;
    private String countryName;
    private String addressName;
    private List<String> closesCites; // TODO : get closes cites

    public LocationObj() {
    }

    public LocationObj(LatLng coordinates) {
        this.latitude = coordinates.latitude;
        this.longitude = coordinates.longitude;
    }

    public LocationObj(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }

    @NonNull
    @Override
    public String getPackableKey() {
        return LOCATION_KEY;
    }

    public LatLng getCoordinates() {
        return new LatLng(latitude, longitude);
    }

    public String getCityName() {
        return cityName;
    }

    public String getAddressName(){
        return addressName;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setAddressName(String addressName){this.addressName = addressName;}

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setClosesCites(ArrayList<String> closesCites) {
        this.closesCites = closesCites;
    }

    public List<String> getClosesCites() {
        return closesCites;
    }

    @Override
    public String toString() {
        return "LocationObj:[lng:" + longitude + ", lat:" + latitude + "]";
    }

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private LocationObj(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        addressName = in.readString();
        cityName = in.readString();
        countryName = in.readString();
        closesCites = in.readArrayList(String.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(addressName);
        out.writeString(cityName);
        out.writeString(countryName);
        out.writeList(closesCites);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<LocationObj> CREATOR = new Parcelable.Creator<LocationObj>() {
        public LocationObj createFromParcel(Parcel in) {
            return new LocationObj(in);
        }

        public LocationObj[] newArray(int size) {
            return new LocationObj[size];
        }
    };

    @Override
    public boolean hasUnpacked() {
        return latitude != 0 && cityName != null;
    }

    @NonNull
    @Override
    public DataInstanceResult pack(@NonNull HashMap<String, Object> databaseMap) {
        databaseMap.put(PATH_LATITUDE, latitude);
        databaseMap.put(PATH_LONGITUDE, longitude);
        databaseMap.put(PATH_ADDRESS_NAME,addressName);
        databaseMap.put(PATH_CITY_NAME, cityName);
        databaseMap.put(PATH_COUNTRY_NAME, countryName);
        return DataInstanceResult.onSuccess();
    }

    @NonNull
    @Override
    public DataInstanceResult unpack(@NonNull DataSnapshot dataSnapshot) {
        try {
            setCountryName((String) dataSnapshot.child(PATH_COUNTRY_NAME).getValue());
            setLatitude((Double) dataSnapshot.child(PATH_LATITUDE).getValue());
            setLongitude((Double) dataSnapshot.child(PATH_LONGITUDE).getValue());// FIXME: java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.Double
            setAddressName((String) dataSnapshot.child(PATH_ADDRESS_NAME).getValue());
            setCityName((String) dataSnapshot.child(PATH_CITY_NAME).getValue());
            return DataInstanceResult.onSuccess();
        } catch (Exception ex) {
            return new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE);
        }
    }

    @Override
    public int doInBackground(Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressName = address.getAddressLine(0).substring(0,address.getAddressLine(0).indexOf(","));
                countryName = address.getCountryName();
                cityName = address.getLocality();
                if (cityName == null) {
                    cityName = address.getSubAdminArea();
                }

                if (addressName != null && cityName != null && countryName != null) {
                    return 0;
                } else {
                    //resultMsg = "Something was not loaded";
                    return 3;
                }
            } else {
                // TODO : Do this in DeviceLocationManager
                // http://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
                countryName = context.getResources().getConfiguration().locale.getDisplayCountry(Locale.ENGLISH);
                //resultMsg = "City was not loaded; country by device configuration";
                return 3;
            }
        } catch (IOException e) {
            //resultMsg = e.getLocalizedMessage();
            return 1;
        }
    }
}
