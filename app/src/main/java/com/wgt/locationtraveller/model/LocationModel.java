package com.wgt.mapintegration.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by debasish on 09-02-2018.
 */

@Entity(foreignKeys = @ForeignKey(entity = UserModel.class,
        parentColumns = "email",
        childColumns = "email"))

public class LocationModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String email;
    @NonNull
    private String date;
    @NonNull
    private String time;
    @NonNull
    private double latitude;
    @NonNull
    private double longitude;

    public LocationModel(String email, String date, String time, double latitude, double longitude) {
        this.email = email;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
