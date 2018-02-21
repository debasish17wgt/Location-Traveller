package com.wgt.locationtraveller.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

/**
 * Created by debasish on 16-02-2018.
 */

@Entity(
        indices = {@Index(
                value = {"trainNo"},
                unique = true
        )}
)
public class TrainInfoModel implements Serializable {

    @Ignore
    private static final long serialVersionUID = 1L;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int trainNo;
    private String trainName;
    private String source, destination;
    private boolean isDeleted;

    @Ignore
    private List<RouteModel> routeList;


    public TrainInfoModel(int trainNo, String trainName, String source, String destination, List<RouteModel> routeList, boolean isDeleted) {
        this.trainName = trainName;
        this.trainNo = trainNo;
        this.source = source;
        this.destination = destination;
        this.routeList = routeList;
        this.isDeleted = isDeleted;
    }
    public TrainInfoModel(int trainNo, String trainName, String source, String destination, boolean isDeleted) {
        this.trainName = trainName;
        this.trainNo = trainNo;
        this.source = source;
        this.destination = destination;
        this.isDeleted = isDeleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public int getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(int trainNo) {
        this.trainNo = trainNo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<RouteModel> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<RouteModel> routeList) {
        this.routeList = routeList;
    }

    public boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
