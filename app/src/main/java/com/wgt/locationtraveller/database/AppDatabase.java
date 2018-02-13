package com.wgt.mapintegration.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.wgt.mapintegration.dao.LocationDao;
import com.wgt.mapintegration.dao.UserDao;
import com.wgt.mapintegration.model.LocationModel;
import com.wgt.mapintegration.model.UserModel;

@Database(entities = {LocationModel.class, UserModel.class}, version = 1, exportSchema = false)
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

    public abstract UserDao userDao();
    public abstract LocationDao locationDao();


}
