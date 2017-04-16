package org.blackstork.findfootball.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by WiskiW on 16.04.2017.
 */

public class LocationObj implements Parcelable {

    private LatLng coordinates;
    private String cityName;
    private String countryName;
    private List<String> closesCites;

    public LocationObj(LatLng coordinates) {
        this.coordinates = coordinates;
        closesCites = new ArrayList<>();
    }

    public LocationObj(double lat, double lng) {
        this.coordinates = new LatLng(lat, lng);
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void loadFullAddress(final Context context, final LocationListener callback) {
        if (cityName != null && countryName != null) {
            callback.onComplete(0, this, null);
            return;
        }
        new LoadStringLocation(context, callback).execute();
    }

    public String getCityName() {
        return cityName;
    }

    public double getLongitude() {
        return coordinates.longitude;
    }

    public double getLatitude() {
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

    public void setClosesCites(ArrayList<String> closesCites) {
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

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private LocationObj(Parcel in) {
        coordinates = in.readParcelable(LatLng.class.getClassLoader());
        cityName = in.readString();
        countryName = in.readString();
        closesCites = in.readArrayList(String.class.getClassLoader());
    }


    public interface ClosesCitesListener {
        void onFound(String[] closesCites);

        void onFailed(int code, String msg);
    }

    public interface LocationListener {
        void onComplete(int resultCode, LocationObj location, String msg);
    }

    private class LoadStringLocation extends AsyncTask<Void, Void, Integer> {

        private Context context;
        private LocationListener callback;
        private String resultMsg = null;

        public LoadStringLocation(Context context, LocationListener callback) {
            this.context = context;
            this.callback = callback;
            this.resultMsg = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            try {
                List<Address> addresses = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    countryName = address.getCountryName();
                    cityName = address.getLocality();
                    if (cityName == null) {
                        cityName = address.getSubAdminArea();
                    }

                    if (cityName != null && countryName != null) {
                        return 0;
                    } else {
                        resultMsg = "Something was not loaded";
                        return 3;
                    }
                } else {
                    // TODO : Do this in DeviceLocationManager
                    // http://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
                    countryName = context.getResources().getConfiguration().locale.getDisplayCountry(Locale.ENGLISH);
                    resultMsg = "City was not loaded; country by device configuration";
                    return 3;
                }
            } catch (IOException e) {
                resultMsg = e.getLocalizedMessage();
                return 1;
            }
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            super.onPostExecute(resultCode);
            if (resultCode == 0) {
                callback.onComplete(0, LocationObj.this, resultMsg);
            } else {
                callback.onComplete(resultCode, LocationObj.this, resultMsg);
            }
        }
    }


}
