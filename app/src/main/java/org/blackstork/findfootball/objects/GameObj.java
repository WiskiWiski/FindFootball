package org.blackstork.findfootball.objects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by WiskiW on 02.04.2017.
 */

public class GameObj {

    private LatLng location;
    private String title;
    private String description;
    private long time;
    private long date;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
