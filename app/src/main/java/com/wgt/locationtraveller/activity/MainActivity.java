package com.wgt.locationtraveller.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wgt.locationtraveller.adapter.PagerAdapter;

import com.wgt.locationtraveller.R;
import com.wgt.locationtraveller.fragment.HomeFragment;
import com.wgt.locationtraveller.fragment.MessageFragment;
import com.wgt.locationtraveller.fragment.SettingsFragment;
import com.wgt.locationtraveller.preference.StatusPref;
import com.wgt.locationtraveller.services.LocationService;
import com.wgt.locationtraveller.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LocationService.LocationListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private int stopCount = 0;
    private Handler handler;

    private LocationService locationService;
    private boolean locServiceStatus;
    //private LocationService.LocationListener listener_fragment;

    private List<String> listOfPermissions;
    private final int PERMISSION_REQUEST_CODE = 1;

    private List<Fragment> fragList;
    private LocationService.LocationListener locationListener;
    private StatusPref.SharedPrefChangedListener prefChangedListener;
    private StatusPref statusPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        statusPref = new StatusPref(this);

        getSupportActionBar().setTitle("KHARAGPUR, 100 KM, 02:26 PM");
        getSupportActionBar().hide();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialize permissions
        listOfPermissions = new ArrayList<>();
        listOfPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        listOfPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        //tab layout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_home));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_msg));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_settings));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //2nd logic of fragments
        fragList = new ArrayList<>();
        fragList.add(new HomeFragment());
        fragList.add(new MessageFragment());
        fragList.add(new SettingsFragment());

        //set frag locationListener
        locationListener = (LocationService.LocationListener)fragList.get(0);

        //set Home frag as sp Changed Listener
        prefChangedListener = (StatusPref.SharedPrefChangedListener)fragList.get(0);


        //adapter
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), fragList);

        //view pager
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (stopCount == 9) {
                    //stop service
                    if (isMyServiceRunning()) {
                        unbindService(locationServiceConnection);
                        Intent intent = new Intent(MainActivity.this, LocationService.class);
                        intent.setAction(Constant.ACTION.ACTION_STOP_LOCATION_SERVICE);
                        startService(intent);
                    }
                } else {
                    if (stopCount == 0) {
                        handler.postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        stopCount = 0;
                                    }
                                },
                                1000 * 10
                        );
                    }
                    stopCount++;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //set SharedPref Changed Listener
        statusPref.registerListener(this);

        //check for permission and GPS status
        //start or bind service
        // set location locationListener (after successfully binding)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkUsesPermission()) {
                requestPermission();
                return;
            }
        }

        if (!isLocationEnabled()) {
            showAlert();
            return;
        }


        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(Constant.ACTION.ACTION_START_LOCATION_SERVICE);

        if (isMyServiceRunning()) {
            bindService(intent, locationServiceConnection, BIND_AUTO_CREATE);
        } else {
            startService(intent);
            bindService(intent, locationServiceConnection, BIND_AUTO_CREATE);
        }


        //register broadcast to listen for service to stop
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(
                        serviceStoppedReceiver,
                        new IntentFilter(Constant.INTENT.INTENT_LOCATION_SERVICE_STOPPED)
                );


    }

    @Override
    protected void onPause() {
        //unregister SharedPrefChangedListsner
        statusPref.unregisterListener(this);

        // unbind service if running
        // set loc locationListener to null
        if (isMyServiceRunning()) {
            unbindService(locationServiceConnection);
            if (locationService != null) {
                locationService.setLocationListener(null);
            }
        }
        super.onPause();
    }

    //location callback from service
    @Override
    public void onLocationReceived(Location location) {
        if (locationListener != null) {
            locationListener.onLocationReceived(location);
        }
    }

    //preference callback from SharedPreference
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (prefChangedListener != null) {
            prefChangedListener.onSharedPrefChanged(sharedPreferences, key);
        }
    }

    //==========================================dev defined functions=======================================


    //serviceStoppedReceiver
    private BroadcastReceiver serviceStoppedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            locationService = null;
            locServiceStatus = false;
        }
    };


    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationService = ((LocationService.MyBinder) iBinder).getServiec();
            locServiceStatus = true;

            //set location locationListener to the service to receive location update from service
            locationService.setLocationListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
            locServiceStatus = false;
            Toast.makeText(MainActivity.this, "MAIN++++++LocationService Stopped++++++", Toast.LENGTH_SHORT).show();
        }
    };


    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //========================== Premission methods and callbacks============================

    private boolean checkUsesPermission() {
        for (String permission : listOfPermissions) {
            int result = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            boolean status = (result == PackageManager.PERMISSION_GRANTED);
            if (!status) {
                return false;
            }
        }
        return true;
    }

    private void requestPermission() {
        try {
            ActivityCompat.requestPermissions(this, listToStringArray(listOfPermissions), PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] listToStringArray(List<String> list) {
        String arr[] = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                boolean success = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (!success) {
                    //one of the permission is not granted
                    finish();
                    return;
                }
            }
        }
    }
    //================LocationModel & Connection methods and callbacks=====================

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Toast.makeText(MainActivity.this, "Enable location service", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog d = dialog.create();
        d.show();

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setMargins(10, 0, 10, 0);


        Button pb = d.getButton(DialogInterface.BUTTON_POSITIVE);
        pb.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        pb.setLayoutParams(ll);
        pb.setTextColor(getResources().getColor(android.R.color.white));
        pb.setPadding(10, 0, 10, 0);

        Button nb = d.getButton(DialogInterface.BUTTON_NEGATIVE);
        //nb.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        nb.setLayoutParams(ll);
        nb.setTextColor(getResources().getColor(android.R.color.darker_gray));
        nb.setPadding(10, 0, 10, 0);


    }



}
