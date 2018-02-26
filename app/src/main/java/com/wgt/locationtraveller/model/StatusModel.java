package com.wgt.locationtraveller.model;

import java.io.Serializable;

/**
 * Created by debasish on 26-02-2018.
 */

public class StatusModel {
    private String current_lat, current_lng, next_station, next_exp_time, next_dist_pending, dest_station, dest_exp_time, dest_dist_pending;
    private long delay;

    public StatusModel() {
    }

    public StatusModel(String current_lat, String current_lng,
                       String next_station, String next_exp_time, String next_dist_pending,
                       String dest_station, String dest_exp_time, String dest_dist_pending,
                       long delay) {
        this.current_lat = current_lat;
        this.current_lng = current_lng;
        this.next_station = next_station;
        this.next_exp_time = next_exp_time;
        this.next_dist_pending = next_dist_pending;
        this.dest_station = dest_station;
        this.dest_exp_time = dest_exp_time;
        this.dest_dist_pending = dest_dist_pending;
        this.delay = delay;
    }

    public String getCurrent_lat() {
        return current_lat;
    }

    public void setCurrent_lat(String current_lat) {
        this.current_lat = current_lat;
    }

    public String getCurrent_lng() {
        return current_lng;
    }

    public void setCurrent_lng(String current_lng) {
        this.current_lng = current_lng;
    }

    public String getNext_station() {
        return next_station;
    }

    public void setNext_station(String next_station) {
        this.next_station = next_station;
    }

    public String getNext_exp_time() {
        return next_exp_time;
    }

    public void setNext_exp_time(String next_exp_time) {
        this.next_exp_time = next_exp_time;
    }

    public String getNext_dist_pending() {
        return next_dist_pending;
    }

    public void setNext_dist_pending(String next_dist_pending) {
        this.next_dist_pending = next_dist_pending;
    }

    public String getDest_station() {
        return dest_station;
    }

    public void setDest_station(String dest_station) {
        this.dest_station = dest_station;
    }

    public String getDest_exp_time() {
        return dest_exp_time;
    }

    public void setDest_exp_time(String dest_exp_time) {
        this.dest_exp_time = dest_exp_time;
    }

    public String getDest_dist_pending() {
        return dest_dist_pending;
    }

    public void setDest_dist_pending(String dest_dist_pending) {
        this.dest_dist_pending = dest_dist_pending;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
