package com.wgt.locationtraveller.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.wgt.locationtraveller.R;
import com.wgt.locationtraveller.activity.MainActivity;
import com.wgt.locationtraveller.utils.StatusHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debasish on 26-02-2018.
 */

public class GeofenceTransitionServices extends IntentService {

    public GeofenceTransitionServices(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //handle geofence event
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        if (event.hasError()) {
            String errorMsg = getErrorString(this, event.getErrorCode());
            Toast.makeText(this, "GeoFNC ERROR :  " + errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        // transition type
        int transitionType = event.getGeofenceTransition();

        List<Geofence> geofenceList = event.getTriggeringGeofences();
        String details = getGeofenceTransitionDetails(this, transitionType, geofenceList);
        sendNotification(details);

        //TODO :
        // 1. depending on Exit or Enter do start or stop our Location services respectively
        // 2. save to SharedPreference

        //on ENTER save status to SharedPref
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            //calc delay , next satation etc...

            StatusHandler statusHandler = new StatusHandler(this, geofenceList.get(0).getRequestId());
            statusHandler.calc();
        }


        //supposed to be changed==============only for testing
//        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ||
//                transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
//            geofenceList = event.getTriggeringGeofences();
//            details = getGeofenceTransitionDetails(this, transitionType, geofenceList);
//            sendNotification(details);
//        } else {
//            Log.e("GeoFnc Error :", getTransitionString(transitionType));
//        }

    }





    //==================================Geofence helper methods==================================
    private static String getErrorString(Context context, int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "geofence_not_available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "geofence_too_many_geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "geofence_too_many_pending_intents";
            default:
                return "unknown_geofence_error";
        }
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Geofence Transition Entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Geofence Transition Exited";
            default:
                return "Unknown Geofence Transition";
        }
    }

    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_home)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_home))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Content goes here")
                .setContentIntent(notificationPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
}
