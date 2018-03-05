package com.wgt.locationtraveller.networking;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wgt.locationtraveller.database.AppDatabase;
import com.wgt.locationtraveller.model.LocationModel;
import com.wgt.locationtraveller.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by debasish on 05-03-2018.
 */

public class UploadLocations {
    private Context context;
    private AppDatabase database;
    private VolleySingleton volley;

    public UploadLocations(Context context) {
        this.context = context;
        database = AppDatabase.getDatabase(context);
        volley = VolleySingleton.getInstance(context);
    }

    public void sync() {
        List<LocationModel> locList = database.locationDao().getAllLocationsForUpload();

        //if there is no data to upload, return
        if (locList == null || locList.size() == 0) {
            return;
        }

        //network Call
        doNetworkCall(locList);
    }

    private void doNetworkCall(final List<LocationModel> locList) {
        StringRequest locationRequest = new StringRequest(
                Request.Method.POST,
                Constant.URL.upload_list_location,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //handle response
                        handleResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: handle error response

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                List<Map<String, String>> paramList = new ArrayList<>();

                //construct paramList from locList
                for (LocationModel loc : locList) {
                    Map<String, String> l = new HashMap<>();
                    l.put("local_id", ""+loc.getId());
                    l.put("loc_time", ""+loc.getTime());
                    l.put("date", ""+loc.getDate());
                    l.put("latitude", ""+loc.getLatitude());
                    l.put("longitude", ""+loc.getLongitude());
                    l.put("address", ""+loc.getAddress());

                    paramList.add(l);
                }


                Map<String, String> params = new HashMap<>();
                //TODO: convert List to Json properly
                params.put("data", paramList.toString());
                return params;
            }


        };
        //add to request queue
        volley.addToRequestQueue(locationRequest);
    }

    private void handleResponse(String response) {
        try {
            JSONObject resObj = new JSONObject(response);
            String status = resObj.getString("success");
            String message = resObj.getString("message");

            //handle successful response
            if (status.equals("1")) {
                String data = message.substring(1, message.length() - 1);
                String idS[] = data.split(",");

                List<Integer> IDs = new ArrayList<>();
                for (int i=0; i<idS.length;i++) {
                    try{
                        IDs.add(Integer.parseInt(idS[i]));
                    }catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                database.locationDao().updateIDsToSynced(IDs);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
