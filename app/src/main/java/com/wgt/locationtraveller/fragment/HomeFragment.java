package com.wgt.locationtraveller.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wgt.locationtraveller.R;
import com.wgt.locationtraveller.services.LocationService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements LocationService.LocationListener{

    private TextView tv_current_loc, tv_current_city, tv_delay, tv_lat_lon,
            tv_next_station_exp_time, tv_next_station, tv_next_station_dist,
            tv_final_dest_data, tv_pending_distance;//, tv_final_exp_time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUIComponents(view);
        return view;
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

    @Override
    public void onLocationReceived(Location location) {
        //TODO :set location to home fragment
        tv_lat_lon.setText(location.getLatitude()+"::"+location.getLongitude());
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
}
