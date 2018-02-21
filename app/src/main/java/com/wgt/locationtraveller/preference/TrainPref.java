package com.wgt.locationtraveller.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by debasish on 21-02-2018.
 */

public class TrainPref {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String PREF_TRAIN_FILE = "current_train_details";
    public static final String PREF_TRAIN_NUMBER = "train_number";

    public TrainPref(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(PREF_TRAIN_FILE, context.MODE_PRIVATE);
    }

    public int getTrainNumber() {
        return sharedPreferences.getInt(PREF_TRAIN_NUMBER, 0);
    }

    public void saveTrainNumber(int number) {
        editor = sharedPreferences.edit();
        editor.putInt(PREF_TRAIN_NUMBER, number);
        editor.apply();
    }
}
