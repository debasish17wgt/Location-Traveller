package com.wgt.locationtraveller.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.wgt.locationtraveller.model.StatusModel;

/**
 * Created by debasish on 26-02-2018.
 */

public class StatusPref {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public static final String PREF_STATUS_FILE = "status_details";
    public static final String PREF_STATUS_LOC = "location";
    public static final String PREF_STATUS_LAT = "lat";
    public static final String PREF_STATUS_LNG = "lng";
    public static final String PREF_STATUS_DELAY = "delay";

    public static final String PREF_STATUS_NXT_ST = "next_station";
    public static final String PREF_STATUS_NXT_EXP_TIME = "nxt_exp_time";
    public static final String PREF_STATUS_NXT_EXP_AMPM = "nxt_ampm";
    public static final String PREF_STATUS_NXT_DIST = "nxt_dist";

    public static final String PREF_STATUS_DEST_ST = "dest_station";
    public static final String PREF_STATUS_DEST_EXP_TIME = "dest_exp_time";
    public static final String PREF_STATUS_DEST_DIST = "dest_dist";

    public StatusPref(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(PREF_STATUS_FILE, context.MODE_PRIVATE);
    }

    public StatusModel getStatus() {
        return new StatusModel(
                sharedPreferences.getString(PREF_STATUS_LOC, null),
                sharedPreferences.getString(PREF_STATUS_LAT, null),
                sharedPreferences.getString(PREF_STATUS_LNG, null),

                sharedPreferences.getString(PREF_STATUS_NXT_ST, null),
                sharedPreferences.getString(PREF_STATUS_NXT_EXP_TIME, null),
                sharedPreferences.getString(PREF_STATUS_NXT_DIST, null),

                sharedPreferences.getString(PREF_STATUS_DEST_ST, null),
                sharedPreferences.getString(PREF_STATUS_DEST_EXP_TIME, null),
                sharedPreferences.getString(PREF_STATUS_DEST_DIST, null),

                sharedPreferences.getLong(PREF_STATUS_DELAY, -1)
        );
    }

    public void saveStatus(StatusModel status) {
        editor = sharedPreferences.edit();

        if (status.getCurrent_city() != null && !status.getCurrent_city().equals("")) editor.putString(PREF_STATUS_LOC, status.getCurrent_city());
        if (status.getCurrent_lat() != null && !status.getCurrent_lat().equals("")) editor.putString(PREF_STATUS_LAT, status.getCurrent_lat());
        if (status.getCurrent_lng() != null && !status.getCurrent_lng().equals("")) editor.putString(PREF_STATUS_LNG, status.getCurrent_lng());

        if (status.getNext_station() != null && !status.getNext_station().equals("")) editor.putString(PREF_STATUS_NXT_ST, status.getNext_station());
        if (status.getNext_exp_time() != null && !status.getNext_exp_time().equals("")) editor.putString(PREF_STATUS_NXT_EXP_TIME, status.getNext_exp_time());
        if (status.getNext_dist_pending() != null && !status.getNext_dist_pending().equals("")) editor.putString(PREF_STATUS_NXT_DIST, status.getNext_dist_pending());

        if (status.getDest_station() != null && !status.getDest_station().equals("")) editor.putString(PREF_STATUS_DEST_ST, status.getDest_station());
        if (status.getDest_exp_time() != null && !status.getDest_exp_time().equals("")) editor.putString(PREF_STATUS_DEST_EXP_TIME, status.getDest_exp_time());
        if (status.getDest_dist_pending() != null && !status.getDest_dist_pending().equals("")) editor.putString(PREF_STATUS_DEST_DIST, status.getDest_dist_pending());

        if (status.getDelay() != 0 ) editor.putLong(PREF_STATUS_DELAY, status.getDelay());

        editor.apply();

    }

    private SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        this.listener = listener;
        getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }
    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        this.listener = listener;
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }


    public interface SharedPrefChangedListener {
        void onSharedPrefChanged(SharedPreferences sharedPreferences, String key);
    }
}
