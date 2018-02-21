package com.wgt.locationtraveller.fragment;

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
import com.wgt.locationtraveller.model.TrainInfoModel;
import com.wgt.locationtraveller.preference.TrainPref;
import com.wgt.locationtraveller.services.LocationService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements LocationService.LocationListener {

    private TextView tv_current_loc, tv_current_city, tv_delay, tv_lat_lon,
            tv_next_station_exp_time, tv_next_station, tv_next_station_dist,
            tv_final_dest_data, tv_pending_distance;//, tv_final_exp_time;

    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setRetainInstance(true);
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

    //==========================================dev's defined methods=================================
    private void initUIComponents(View view) {
        tv_current_loc = view.findViewById(R.id.tv_current_location_data);
        tv_current_city = view.findViewById(R.id.tv_current_city);
        tv_delay = view.findViewById(R.id.tv_late_data);
        tv_lat_lon = view.findViewById(R.id.tv_lat_lon_data);
        tv_next_station_exp_time = view.findViewById(R.id.tv_exp_time_data);
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
        if (routes != null && routes.size() >=1) {
            dest = routes.get(routes.size()-1);
        }
       if (train != null) {
           tv_final_dest_data.setText(train.getDestination());
       }
       if (dest != null) {
           tv_pending_distance.setText(dest.getDistanceCovered()+" KM, "+convertTime(dest.getArrivalTime()));
       }
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
