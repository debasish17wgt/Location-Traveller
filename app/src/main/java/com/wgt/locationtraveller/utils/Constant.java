package com.wgt.locationtraveller.utils;

/**
 * Created by debasish on 13-02-2018.
 */

public class Constant {

    public interface ACTION {
        String ACTION_START_LOCATION_SERVICE = "com.wgt.locationtraveller.action.START_LOCATION_SERVICE";
        String ACTION_STOP_LOCATION_SERVICE = "com.wgt.locationtraveller.action.STOP_LOCATION_SERVICE";
    }

    public interface NOTIFICATION_ID {
        int LOCATION_SERVICE_NOTIFICATION_ID = 100;
    }

    public interface INTENT {
        String INTENT_LOCATION_BROADCAST = "com.wgt.mapintegration.intent.location_broadcast";
        String INTENT_LOCATION_LAT = "com.wgt.mapintegration.intent.location_lat";
        String INTENT_LOCATION_LONG = "com.wgt.mapintegration.intent.location_long";

        String INTENT_LOCATION_SERVICE_STOPPED = "com.wgt.mapintegration.intent.location.stopped";
    }

    public interface URL {
        String train_info = "https://kaiserwgt.000webhostapp.com/webservices/getRouteInfo.php";
    }
}
