package com.wgt.locationtraveller.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.wgt.locationtraveller.dao.LocationDao;
import com.wgt.locationtraveller.dao.RouteDao;
import com.wgt.locationtraveller.dao.TrainInfoDao;
import com.wgt.locationtraveller.model.LocationModel;
import com.wgt.locationtraveller.model.RouteModel;
import com.wgt.locationtraveller.model.TrainInfoModel;

@Database(entities = {LocationModel.class, TrainInfoModel.class, RouteModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "LocationDatabase")
                            //Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
                            // For testing, allow queries on the main thread.
                            // Don't do this on a real app!
                            .allowMainThreadQueries()
                            // recreate the database if necessary
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract LocationDao locationDao();
    public abstract TrainInfoDao trainInfoDao();
    public abstract RouteDao routeDao();
}
