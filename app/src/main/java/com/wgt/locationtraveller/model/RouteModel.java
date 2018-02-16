package com.wgt.locationtraveller.model;

import java.io.Serializable;

/**
 * Created by debasish on 16-02-2018.
 */

public class RouteModel implements Serializable{
    private static final long serialVersionUID = 1L;

    private String stationName, arrivalTime, distanceCovered;

    public RouteModel(String stationName, String arrivalTime, String distanceCovered) {
        this.stationName = stationName;
        this.arrivalTime = arrivalTime;
        this.distanceCovered = distanceCovered;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDistanceCovered() {
        return distanceCovered;
    }

    public void setDistanceCovered(String distanceCovered) {
        this.distanceCovered = distanceCovered;
    }
}
