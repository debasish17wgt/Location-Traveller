package com.wgt.locationtraveller.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.wgt.locationtraveller.model.LocationModel;

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

    @Query("SELECT * FROM LocationModel WHERE isSynced = 0")
    List<LocationModel> getAllLocationsForUpload();

    @Query("UPDATE LocationModel SET isSynced = 1 WHERE id = :ids")
    void updateIDsToSynced(List<Integer> ids);

    /*@Query("SELECT * FROM LocationModel WHERE email = :email")
    List<LocationModel> getAllLocationsByEmail(String email);*/
}
