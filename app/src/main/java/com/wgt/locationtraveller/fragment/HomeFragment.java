package com.wgt.locationtraveller.fragment;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wgt.locationtraveller.R;
import com.wgt.locationtraveller.database.AppDatabase;
import com.wgt.locationtraveller.model.RouteModel;
import com.wgt.locationtraveller.model.StatusModel;
import com.wgt.locationtraveller.model.TrainInfoModel;
import com.wgt.locationtraveller.preference.StatusPref;
import com.wgt.locationtraveller.preference.TrainPref;
import com.wgt.locationtraveller.services.LocationService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements LocationService.LocationListener,
        StatusPref.SharedPrefChangedListener {

    private TextView tv_current_loc, tv_current_city, tv_delay, tv_lat_lon,
            tv_next_station_exp_time, tv_next_ampm, tv_next_station, tv_next_station_dist,
            tv_final_dest_data, tv_pending_distance;//, tv_final_exp_time;

    private Handler handler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreSharedPref();
//        if (getActivity() != null) {
//            statusPref = new StatusPref(getActivity());
//            statusPref.registerListener(this);
//            StatusModel model = statusPref.getStatus();
//            updateUI(model);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        backUpToSharedPref();
    }

    //TODO : update the whole UI with available data
    private void updateUI(StatusModel model) {
        if (model == null) return;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUIComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUI();
        /*TrainInfoModel destModel = AppDatabase.getDatabase(getContext()).trainInfoDao().getTrainInfo();
        if (destModel != null) {
            tv_final_dest_data.setText(destModel.getDestination());
        }*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onLocationReceived(final Location location) {
        //TODO :set location to home fragment
        tv_lat_lon.setText(location.getLatitude() + "::" + location.getLongitude());
        new LatLngToAddressAsync(location).execute();
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
                    if(null!=listAddresses&&listAddresses.size()>0){
                        //Address address = listAddresses.get(0);
                        Address address = listAddresses.get(1);
                        tv_current_loc.setText(address.getFeatureName()+", "+address.getSubLocality());
                        tv_current_city.setText(address.getLocality());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/

    }

    @Override
    public void onSharedPrefChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case StatusPref.PREF_STATUS_DELAY:
                tv_delay.setText(sharedPreferences.getString(key, tv_delay.getText().toString()));
                break;
            case StatusPref.PREF_STATUS_NXT_ST:
                tv_next_station.setText(sharedPreferences.getString(key, tv_next_station.getText().toString()));
                break;
            case StatusPref.PREF_STATUS_NXT_EXP_TIME:
                tv_next_station_exp_time.setText(sharedPreferences.getString(key, tv_next_station_exp_time.getText().toString()));
                break;
            case StatusPref.PREF_STATUS_DEST_EXP_TIME:
                String tTime = tv_pending_distance.getText().toString();
                String dist = tTime.split(",")[0];

                String tempTime = sharedPreferences.getString(key, null);
                if (tempTime != null) {
                    tv_pending_distance.setText(dist+", "+convertTime(tempTime));
                }
                break;

        }
    }

    //==========================================dev's defined methods=================================
    private void initUIComponents(View view) {
        tv_current_loc = view.findViewById(R.id.tv_current_location_data);
        tv_current_city = view.findViewById(R.id.tv_current_city);
        tv_delay = view.findViewById(R.id.tv_late_data);
        tv_lat_lon = view.findViewById(R.id.tv_lat_lon_data);
        tv_next_station_exp_time = view.findViewById(R.id.tv_exp_time_data);
        tv_next_ampm = view.findViewById(R.id.tv_next_ampm);
        tv_next_station = view.findViewById(R.id.tv_next_station_data);
        tv_next_station_dist = view.findViewById(R.id.tv_dist_data);
        tv_final_dest_data = view.findViewById(R.id.tv_final_dest_data);
        tv_pending_distance = view.findViewById(R.id.tv_pending_distance);
        // tv_final_exp_time = view.findViewById(R.id.tv_final_exp_time);

    }

    private String convertTime(String arrivalTime) {
        String ampm = "AM";
        String[] temp = arrivalTime.split(":");
        int hr = Integer.parseInt(temp[0]);
        if (hr > 12) {
            hr = hr - 12;
            ampm = "PM";
        }
        return hr + ":" + temp[1] + ampm;
    }

    private String[] convertTimeTo12Arr(String arrivalTime) {
        String ampm = "AM";
        String[] temp = arrivalTime.split(":");
        int hr = Integer.parseInt(temp[0]);
        if (hr > 12) {
            hr = hr - 12;
            ampm = "PM";
        }
        return new String[]{hr + ":" + temp[1], ampm};
    }

    private String convertTimeTo24(String t) {
        String ampm = t.substring(t.length()-2, t.length()-1);
        if (ampm.equalsIgnoreCase("AM")) {
            return t.substring(0, t.length()-2);
        }else {
            String tempTime = t.substring(0, t.length()-2);
            String tt[] = tempTime.split(":");
            int hr = Integer.parseInt(tt[0]);
            return (hr+12)+":"+tt[1];
        }
    }

    private long formatLateToMinute(String d) {
        int h = Integer.parseInt(d.split(":")[0]);
        int m = Integer.parseInt(d.split(":")[1]);
        if (h == 0) {
            return m;
        } else {
            return (h*60)+m;
        }
    }

    private String formatLate(long d) {
        return d/60+":"+d%60;
    }

    //refresh the ui
    public void updateUI() {
       /* TrainInfoModel trainInfoModel = new TrainInfoHandler(getContext()).getTrainDetails();
        if (trainInfoModel == null) {
            Toast.makeText(getContext(), "ERROR : failed to refresh UI\nREASON : train info not found", Toast.LENGTH_SHORT).show();
            return;
        }
        RouteModel routeModel = trainInfoModel.getRouteList().get(trainInfoModel.getRouteList().size() - 1);
        tv_final_dest_data.setText(routeModel.getStationName());
        tv_pending_distance.setText(routeModel.getDistanceCovered() + "KM, " + convertTime(routeModel.getArrivalTime()));*/

        int trainNo = new TrainPref(getContext()).getTrainNumber();
        if (trainNo == 0) {
            return;
        }
        TrainInfoModel train = AppDatabase.getDatabase(getContext()).trainInfoDao().getTrainByTrainNO(trainNo);
        List<RouteModel> routes = AppDatabase.getDatabase(getContext()).routeDao().getRouteByTrainNo(trainNo);
        RouteModel dest = null;
        if (routes != null && routes.size() >= 1) {
            dest = routes.get(routes.size() - 1);
        }
        if (train != null) {
            tv_final_dest_data.setText(train.getDestination());
        }
        if (dest != null) {
            tv_pending_distance.setText(dest.getDistanceCovered() + " KM, " + convertTime(dest.getArrivalTime()));
        }
    }

    private void restoreSharedPref() {
        StatusModel sm = new StatusPref(getActivity()).getStatus();

       if (sm.getCurrent_city() != null) tv_current_city.setText(sm.getCurrent_city());
        if (sm.getCurrent_lat() != null && sm.getCurrent_lng()!= null) tv_lat_lon.setText(sm.getCurrent_lat()+"::"+sm.getCurrent_lng());
        if (sm.getDelay() != -1) tv_delay.setText(formatLate(sm.getDelay()));

        if (sm.getNext_station() != null) tv_next_station.setText(sm.getNext_station());
        if (sm.getNext_exp_time() != null){
            String[] tt = convertTimeTo12Arr(sm.getNext_exp_time());
            tv_next_station_exp_time.setText(tt[0]);
            tv_next_ampm.setText(tt[1]);
        }
        if (sm.getNext_dist_pending() != null) tv_next_station_dist.setText(sm.getNext_dist_pending());

        if (sm.getDest_station() != null) tv_final_dest_data.setText(sm.getDest_station());
        //if (sm.getDest_dist_pending() != null )tv_pending_distance.setText(sm.getDest_dist_pending()+", "+convertTime(sm.getDest_exp_time()));
        if (sm.getDest_dist_pending() != null && sm.getDest_exp_time() != null) tv_pending_distance.setText(sm.getDest_dist_pending()+", "+convertTime(sm.getDest_exp_time()));
    }

    private void backUpToSharedPref() {
        String location = tv_current_city.getText().toString();
        String late = tv_delay.getText().toString();
        String latlng = tv_lat_lon.getText().toString();
        String nxt_time = tv_next_station_exp_time.getText().toString();
        String nxt_ampm = tv_next_ampm.getText().toString();
        String nxt_st = tv_next_station.getText().toString();
        String nxt_dist = tv_next_station_dist.getText().toString();
        String final_st = tv_final_dest_data.getText().toString();

        if (nxt_ampm.equalsIgnoreCase("PM")) {
            nxt_time = convertTimeTo24(nxt_time+"PM");
        }


        String final_details = tv_pending_distance.getText().toString();
        String temp[] = final_details.split(",");

        String final_dist = temp[0];
        String final_time = convertTimeTo24(temp[1].trim());
        String[] ll = latlng.split("::");
        new StatusPref(getActivity()).saveStatus(new StatusModel(
                location,
                ll.length==2?ll[0]:"LATITUDE",
                ll.length==2?ll[1]:"LONGITUDE",
                nxt_st,
                nxt_time,
                nxt_dist,
                final_st,
                final_time,
                final_dist,
                formatLateToMinute(late)

        ));

    }





    //latLng to address converter AsyncTask
    private class LatLngToAddressAsync extends AsyncTask<Void, Void, List<Address>> {
        private Location location;

        LatLngToAddressAsync(Location location) {
            this.location = location;
        }

        @Override
        protected List<Address> doInBackground(Void... voids) {
            List<Address> listAddresses = null;
            Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            try {
                listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listAddresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (null != addresses && addresses.size() > 0) {
                Address address1 = addresses.get(0);
                Address address = addresses.get(1);
                tv_current_loc.setText(address.getFeatureName() + ", " + address.getSubLocality());
                tv_current_city.setText(address.getLocality());
            }
        }
    }
}
