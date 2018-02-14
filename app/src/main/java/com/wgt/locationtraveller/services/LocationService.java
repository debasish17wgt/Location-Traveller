package com.wgt.locationtraveller.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wgt.locationtraveller.R;
import com.wgt.locationtraveller.activity.MainActivity;
import com.wgt.locationtraveller.database.AppDatabase;
import com.wgt.locationtraveller.model.LocationModel;
import com.wgt.locationtraveller.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LocationService extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private IBinder iBinder = new MyBinder();
    private LocationListener listener;

    private GoogleApiClient gac;
    private LocationRequest locationRequest;

    private final long UPDATE_INTERVAL = 2000;//1000*60*2;
    private final long FASTEST_INTERVAL = 2000;//1000*60*2;

    @Override
    public void onCreate() {
        super.onCreate();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constant.ACTION.ACTION_START_LOCATION_SERVICE)) {
            gac.connect();
            notificationBuilder();
        } else if (intent.getAction().equals(Constant.ACTION.ACTION_STOP_LOCATION_SERVICE)) {
            stopForeground(true);
            serviceStoppedBroadcast();
            stopSelf();
        } else {
            gac.connect();
            notificationBuilder();
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        gac.disconnect();
        Toast.makeText(this, "+++++++LOCATION SERVICE DESTROYED+++++++", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        call();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //stopSelf();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        stopForeground(true);
        stopSelf();
    }


    @Override
    public void onLocationChanged(Location location) {
        // send location to listener (Activity)
        if (listener != null) {
            listener.onLocationReceived(location);
        }

        //save location to database
        saveToDatabase(location);
    }

    private void saveToDatabase(Location location) {
        AppDatabase database = AppDatabase.getDatabase(this);
        String dateTime = getDateAndTime();

        long n = database.locationDao()
                .addLocation(
                        new LocationModel(
                                dateTime.split(" ")[0],
                                dateTime.split(" ")[1],
                                location.getLatitude(),
                                location.getLongitude()
                        )
                );
        Log.d("Location : ", "LONG : "+n);
    }

    private String getDateAndTime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    //location call
    private void call() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void notificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //stop intent
        /*Intent stopIntent = new Intent(this, LocationService.class);
        stopIntent.setAction(Constant.ACTION.ACTION_STOP_LOCATION_SERVICE);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0);*/

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Location Service is running")
                .setTicker("Ticker")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingNotificationIntent)
                .setOngoing(true)
                //.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", pendingStopIntent)
                .build();

        startForeground(Constant.NOTIFICATION_ID.LOCATION_SERVICE_NOTIFICATION_ID, notification);
    }

    private void serviceStoppedBroadcast() {
        Intent intent = new Intent(Constant.INTENT.INTENT_LOCATION_SERVICE_STOPPED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //binder class
    public class MyBinder extends Binder {
        public LocationService getServiec() {
            return LocationService.this;
        }
    }

    // location listener interface
    public interface LocationListener {
        void onLocationReceived(Location location);
    }
    public void setLocationListener(LocationListener listener) {
        this.listener = listener;
    }
}
