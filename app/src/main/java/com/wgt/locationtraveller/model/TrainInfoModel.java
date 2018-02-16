package com.wgt.locationtraveller.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by debasish on 16-02-2018.
 */

public class TrainInfoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String trainName;
    private String trainNo;
    private List<RouteModel> routeList;

    public TrainInfoModel(String trainName, String trainNo, List<RouteModel> routeList) {
        this.trainName = trainName;
        this.trainNo = trainNo;
        this.routeList = routeList;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public List<RouteModel> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<RouteModel> routeList) {
        this.routeList = routeList;
    }
}
