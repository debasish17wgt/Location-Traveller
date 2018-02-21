package com.wgt.locationtraveller.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by debasish on 16-02-2018.
 */

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = TrainInfoModel.class,
                        parentColumns = "trainNo",
                        childColumns = "trainNo",
                        onDelete = ForeignKey.RESTRICT,
                        onUpdate = ForeignKey.CASCADE
                )
        }
)
public class RouteModel implements Serializable{
    private static final long serialVersionUID = 1L;


    @PrimaryKey(autoGenerate = true)
    private int id;
    private int trainNo;
    private String stationName, arrivalTime, distanceCovered;
    private int pinCode;
    private double latitude, longitude;
    private int radius;

    public RouteModel(int trainNo, String stationName, String arrivalTime, String distanceCovered, int pinCode, double latitude, double longitude, int radius) {
        this.trainNo = trainNo;
        this.stationName = stationName;
        this.arrivalTime = arrivalTime;
        this.distanceCovered = distanceCovered;
        this.pinCode = pinCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(int trainNo) {
        this.trainNo = trainNo;
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

    public int getPinCode() {
        return pinCode;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
