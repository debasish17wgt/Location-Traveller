package com.wgt.locationtraveller.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wgt.locationtraveller.database.AppDatabase;
import com.wgt.locationtraveller.model.RouteModel;
import com.wgt.locationtraveller.services.GeofenceTransitionServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debasish on 26-02-2018.
 */

public class GeofenceConfig {

    private Context context;
    private AppDatabase database;

    private GeofencingClient geofencingClient;
    private GeofencingRequest geofencingRequest;
    private List<Geofence> geofenceList;
    private int trainNo;
    private List<RouteModel> routeList;
    private PendingIntent geofencePendingIntent;

    public GeofenceConfig(Context context) {
        this.context = context;
        database = AppDatabase.getDatabase(this.context);
        geofencingClient = LocationServices.getGeofencingClient(this.context);
        geofenceList = new ArrayList<>();
    }

    @SuppressLint("MissingPermission")
    public void addGeofences(int trainNo) {
        this.trainNo = trainNo;
        getTrainDetails();
        setupGeofences();
        setupGeofencesRequest();

        geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Geofence added Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Geofence adding Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //get train details from DB
    private void getTrainDetails() {
        routeList = database.routeDao().getRouteByTrainNo(trainNo);
    }

    //setup geofence for each and every stations
    private void setupGeofences() {
        if (routeList == null || routeList.size() == 0) {
            return;
        }

        // setting up geofence for every station
        for (RouteModel route : routeList) {
            geofenceList.add(
                    new Geofence.Builder()
                            .setRequestId(route.getStationName()) /*set station name as key as they are unique*/
                            .setCircularRegion(route.getLatitude(), route.getLongitude(), route.getRadius() == 0 ? 200 : route.getRadius())
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build()
            );
        }
    }

    //setting up geofences request
    private void setupGeofencesRequest() {
        if (geofenceList == null || geofenceList.size() == 0) {
            return;
        }

        geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build();
    }

    // defining geofence pending intent, for handling transitions
    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(context, GeofenceTransitionServices.class);
        geofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

}
