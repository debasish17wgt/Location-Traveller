package com.wgt.locationtraveller.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.wgt.locationtraveller.model.TrainInfoModel;

import java.util.List;

/**
 * Created by debasish on 20-02-2018.
 */

@Dao
public interface TrainInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addTrainInfo(TrainInfoModel trainInfoModel);

    @Update
    void updateTrain(TrainInfoModel trainInfoModel);

    @Query("SELECT * FROM TrainInfoModel WHERE isDeleted = 0")
    List<TrainInfoModel> getAllTrainInfo();

    @Query("SELECT * FROM TrainInfoModel WHERE trainNo = :trainNo AND isDeleted = 0")
    TrainInfoModel getTrainByTrainNO(int trainNo);

    @Query("UPDATE TrainInfoModel SET isDeleted = 1 WHERE trainNo = :trainNo")
    void deleteTrain(int trainNo);
}
