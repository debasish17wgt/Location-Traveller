package com.wgt.locationtraveller.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wgt.locationtraveller.R;
import com.wgt.locationtraveller.database.AppDatabase;
import com.wgt.locationtraveller.model.RouteModel;
import com.wgt.locationtraveller.model.TrainInfoModel;
import com.wgt.locationtraveller.networking.VolleySingleton;
import com.wgt.locationtraveller.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by debasish on 20-02-2018.
 */

public class AddTrainDialogUtil implements TextWatcher, Response.ErrorListener, Response.Listener<String> {

    @BindView(R.id.et_train_no)
    EditText train_no;
    @BindView(R.id.spnr_source)
    Spinner spnr_source;
    @BindView(R.id.spnr_destination)
    Spinner spnr_destination;
    @BindView(R.id.img_add_train)
    ImageView img_add_train;
    @BindView(R.id.layout_details)
    RelativeLayout layout_details;
    @BindView(R.id.btn_clear)
    Button btn_clear;

    private Context context;
    private Dialog dialog;
    private ProgressDialog progressDialog;

    private AppDatabase database;

    private List<String> source = new ArrayList<>();
    private List<String> dest = new ArrayList<>();

    private ArrayAdapter<String> destAdapter;

    private TrainInfoModel trainInfoModel = null;
    private List<RouteModel> routeModels = new ArrayList<>();
    private String trainName = "";
    private int trainNo;
    private String sourceName, destName;
    //private String message = "";

    private boolean isNewData = false;

    private TrainAddedListener listener;

    //listener interface
    public interface TrainAddedListener {
        void onTrainAdded();
    }


    public AddTrainDialogUtil(Context context, TrainAddedListener listener) {
        this.context = context;
        this.listener = listener;
        dialog = new Dialog(this.context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_train_dialog);
        ButterKnife.bind(this, dialog);

        database = AppDatabase.getDatabase(context);

        progressDialog = new ProgressDialog(this.context);
        progressDialog.setTitle("Getting train details");
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        //listen to train no to be 5 digit
        train_no.addTextChangedListener(this);

        spnr_source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!source.get(position).equals("Select a Source Station")) {
                    sourceName = source.get(position);

                    //fill destination with stations, starting from Station{source+1}
                    dest.clear();
                    dest.add("Select a Destination Station");
                    for (int i = source.indexOf(sourceName) + 1; i < source.size(); i++) {
                        dest.add(source.get(i));
                    }
                    if (destAdapter != null) {
                        destAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnr_destination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!dest.get(position).equals("Select a Destination Station")) {
                    destName = dest.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showDialog() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // first check in local DB for availability.
        // if not present then, call network to fetch train details
        if (s.toString().length() == 5) {
            routeModels.clear();

            try {
                trainNo = Integer.parseInt(s.toString());
                List<RouteModel> routeList = database.routeDao().getRouteByTrainNo(trainNo);
                if (routeList == null || routeList.size() < 1) {
                    isNewData = true;
                    doNetworkJob("" + trainNo);
                } else {
                    isNewData = false;
                    routeModels.addAll(routeList);
                    populateSpinner();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "ERROR : Train number not valid", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @OnClick(R.id.btn_clear)
    public void clear_clicked() {
        showDetailPanel(false);
    }

    @OnClick(R.id.img_add_train)
    public void addTrainDetails() {

        //validation
        if (train_no.getText().toString().length() != 5) {
            Toast.makeText(context, "please enter train number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sourceName == null) {
            Toast.makeText(context, "please select a source station first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destName == null) {
            Toast.makeText(context, "please select a destination station first", Toast.LENGTH_SHORT).show();
            return;
        }
        /*int s = source.indexOf(sourceName);
        int d = dest.indexOf(destName);
        if (s >= d) {
            Toast.makeText(context, "wrong destination", Toast.LENGTH_SHORT).show();
            return;
        }*/

        //add or update train details to DB, either fetched from remote or from local
        trainInfoModel = new TrainInfoModel(trainNo, trainName, sourceName, destName, routeModels, false);
        if (isNewData) {
            database.trainInfoDao().addTrainInfo(trainInfoModel);
            database.routeDao().addRoute(routeModels);
        } else {
            database.trainInfoDao().updateTrain(trainInfoModel);
        }

        dialog.dismiss();
        Toast.makeText(context, isNewData?"Train Details Saved":"Train Details Updated", Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onTrainAdded();
        }
    }


    //===============================================NETWORK CALL=================================================
    private void doNetworkJob(final String no) {
        if (!progressDialog.isShowing())
            progressDialog.show();

        StringRequest trainDetailsRequest = new StringRequest(Request.Method.POST, Constant.URL.train_info, this, this) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("trainNO", no);
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(trainDetailsRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(context, "ERROR : cant fetch train details.\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(String response) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        try {
            JSONObject resObj = new JSONObject(response);
            String status = resObj.getString("success");
            String message = resObj.getString("message");
            String trainName = "";
            if (status.equals("1")) {
                handleResponse(resObj);
            } else {
                Toast.makeText(context, "ERROR : " + message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "ERROR : parsing error\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleResponse(JSONObject response) {
        String message = "";
        try {
            trainName = response.getString("trainName");
            message = response.getString("message");
            JSONArray routeArr = new JSONArray(message);
            for (int i = 0; i < routeArr.length(); i++) {
                JSONObject obj = routeArr.getJSONObject(i);
                routeModels.add(
                        new RouteModel(
                                trainNo,
                                obj.getString("stationName"),
                                obj.getString("arrival"),
                                obj.getString("distTravelled"),
                                obj.getString("stationPincode").equals("") ? 0 : Integer.parseInt(obj.getString("stationPincode")),
                                obj.getString("stationLatitude").equals("") ? 0 : Double.parseDouble(obj.getString("stationLatitude")),
                                obj.getString("stationLongitude").equals("") ? 0 : Double.parseDouble(obj.getString("stationLongitude")),
                                obj.getString("stationRadius").equals("") ? 0 : Integer.parseInt(obj.getString("stationRadius"))
                        )
                );
            }
            populateSpinner();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "ERROR : failed to handle response", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(context, "ERROR : Data parsing error\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //===============================================NETWORK CALL END=================================================

    //================================================Dev's defined methods================================
    private void populateSpinner() {

        source.add("Select a Source Station");
        dest.add("Select a Destination Station");

        for (RouteModel r : routeModels) {
            source.add(r.getStationName());
            //dest.add(r.getStationName());
        }

        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<String>(context, R.layout.custom_simple_spinner_item, source);
        destAdapter = new ArrayAdapter<String>(context, R.layout.custom_simple_spinner_item, dest);

        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnr_source.setAdapter(sourceAdapter);

        //initially destination does'nt have any items, after selecting source dest. will be generated
        spnr_destination.setAdapter(destAdapter);

        showDetailPanel(true);
    }

    private void showDetailPanel(boolean b) {
        if (b) {
            layout_details.setVisibility(View.VISIBLE);
            train_no.setEnabled(false);
        } else {
            layout_details.setVisibility(View.GONE);
            train_no.setText("");
            train_no.setEnabled(true);
        }
    }

    //================================================Dev's defined methods END================================

}
