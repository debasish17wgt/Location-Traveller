package com.wgt.locationtraveller.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.wgt.locationtraveller.model.RouteModel;

import java.util.List;

/**
 * Created by debasish on 20-02-2018.
 */

@Dao
public interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addRoute(RouteModel routeModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] addRoute(List<RouteModel> routeModel);

    @Query("SELECT * FROM RouteModel WHERE trainNo = :trainNo")
    List<RouteModel> getRouteByTrainNo(int trainNo);
}
