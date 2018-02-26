package com.wgt.locationtraveller.utils;

import android.content.Context;

import com.wgt.locationtraveller.database.AppDatabase;
import com.wgt.locationtraveller.model.RouteModel;
import com.wgt.locationtraveller.model.StatusModel;
import com.wgt.locationtraveller.preference.StatusPref;
import com.wgt.locationtraveller.preference.TrainPref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by debasish on 26-02-2018.
 */

public class StatusHandler {

    long initialDelay = 0;

    private String currentStation;
    String currentStationTime = "";

    String nextStation = "";
    String nextStationTime = "";

    String destStationTime = "";

    private Context context;
    private AppDatabase database;

    public StatusHandler(Context context, String currentStation) {
        this.context = context;
        this.currentStation = currentStation;
        database = AppDatabase.getDatabase(context);
    }

    public void calc() {
        int trainNo = new TrainPref(context).getTrainNumber();
        if (trainNo == 0) return;


        List<RouteModel> routeList = database.routeDao().getRouteByTrainNo(trainNo);

        destStationTime = routeList.get(routeList.size()-1).getArrivalTime();

        for (int i = 0; i < routeList.size(); i++) {
            RouteModel route = routeList.get(i);
            if (currentStation.equals(route.getStationName())) {
                currentStationTime = route.getArrivalTime();
                //check if it is last station , (get rid of indexOutOfBound)
                if (i == routeList.size() - 1) {
                    //last station
                    nextStation = currentStation;
                } else {
                    nextStation = routeList.get(i + 1).getStationName();
                    nextStationTime = routeList.get(i + 1).getArrivalTime();
                    break;
                }
            }
        } //end of for loop


        // error : currentStation not found
        if (currentStationTime.equals("")) return;

        Date nowDate = Calendar.getInstance().getTime();
        Date dDate = getDateFromString(currentStationTime);
        initialDelay = getTimeDiff(nowDate, dDate);  //in minute
        if (initialDelay == -1) return;

        nextStationTime = addDelay(nextStationTime, (int)initialDelay);
        destStationTime = addDelay(destStationTime, (int)initialDelay);

        StatusModel statusModel = new StatusModel();
        statusModel.setDelay(initialDelay);
        statusModel.setNext_station(nextStation);
        statusModel.setNext_exp_time(nextStationTime);
        statusModel.setDest_exp_time(destStationTime);

        //update changes
        new StatusPref(context).saveStatus(statusModel);

    }















    //===================================Time converting helper methods===============================
    private String addDelay(String time, int delay) {
        Date tempDate = getDateFromString(time);
        Calendar cal = Calendar.getInstance();

        if (tempDate != null) {
            cal.setTime(tempDate);
            cal.add(Calendar.MINUTE, delay);
            return convertTimeToString(cal.getTime());
        }
        return time;
    }

    private long getTimeDiff(Date d1, Date d2) {
        if (d1 == null || d2 == null) return -1;
        if (d1.getTime() < d2.getTime()) return 0;
        long diffMs = d1.getTime() - d2.getTime();
        long diffSec = diffMs / 1000;
        long min = diffSec / 60;
        //long sec = diffSec % 60;
        return min;
    }

    private Date getDateFromString(String d) {
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = parser.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private String convertTimeToString(Date d) {
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        return parser.format(d);

    }
}
