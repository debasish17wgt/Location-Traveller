package com.wgt.mapintegration.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.wgt.mapintegration.model.LocationModel;

import java.util.List;

/**
 * Created by debasish on 09-02-2018.
 */
@Dao
public interface LocationDao {

    @Insert
    long addLocation(LocationModel locationModel);

    @Query("SELECT * FROM LocationModel")
    List<LocationModel> getAllLocations();

    @Query("SELECT * FROM LocationModel WHERE email = :email")
    List<LocationModel> getAllLocationsByEmail(String email);
}
