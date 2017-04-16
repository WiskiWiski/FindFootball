package org.blackstork.findfootball.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by WiskiW on 16.04.2017.
 */

public class LocationObj implements Parcelable {

    private final static int CLOSES_CITES_NUMB = 5;

    private LatLng coordinates;
    private String cityName;
    private String countryName;
    private String[] closesCites;

    public LocationObj(LatLng coordinates) {
        this.coordinates = coordinates;
        closesCites = new String[CLOSES_CITES_NUMB];
    }

    public LocationObj(double lat, double lng) {
        this.coordinates = new LatLng(lat, lng);
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void loadFullAddress(final Context context, final LocationListener callback){
        if (cityName != null && countryName != null){
            callback.onComplete(0, this, null);
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        countryName = address.getCountryName();
                        cityName = address.getLocality();
                        if (cityName == null) {
                            cityName = address.getSubAdminArea();
                        }

                        if (cityName != null && countryName != null){
                            callback.onComplete(0, LocationObj.this, null);
                        } else {
                            callback.onComplete(3, LocationObj.this, "Something was not loaded");
                        }
                    } else {
                        callback.onComplete(2, LocationObj.this, null);
                    }
                } catch (IOException e) {
                    callback.onComplete(1, LocationObj.this, e.getLocalizedMessage());
                }

            }
        }).start();
    }

    public String getCityName() {
        return cityName;
    }

    public double getLongitude(){
        return coordinates.longitude;
    }

    public double getLatitude(){
        return coordinates.latitude;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setClosesCites(String... closesCites) {
        this.closesCites = closesCites;
    }

    public void getClosesCites(Context context, ClosesCitesListener callback) {
        // TODO: getClosesCites()
        //return closesCites;
    }





    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(coordinates, flags);
        out.writeString(cityName);
        out.writeString(countryName);
        out.writeArray(closesCites);
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

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private LocationObj(Parcel in) {
        coordinates = in.readParcelable(LatLng.class.getClassLoader());
        cityName = in.readString();
        countryName = in.readString();
        in.readStringArray(closesCites);
    }







    public interface ClosesCitesListener {
        void onFound(String[] closesCites);

        void onFailed(int code, String msg);
    }

    public interface LocationListener {
        void onComplete(int resultCode, LocationObj location, String msg);
    }




}
