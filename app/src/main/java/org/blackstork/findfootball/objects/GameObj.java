package org.blackstork.findfootball.objects;

import com.google.android.gms.maps.model.LatLng;

import org.blackstork.findfootball.time.TimeProvider;

import java.util.UUID;

/**
 * Created by WiskiW on 02.04.2017.
 */

public class GameObj {

    private String eid;
    private LatLng location;
    private String title;
    private String description;
    private long eventTime;
    private long createTime;

    public GameObj() {
        createTime = TimeProvider.getUtcTime();
    }

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

    public String getEid() {
        if (eid == null) {
            eid = UUID.randomUUID().toString();
        }
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
}
