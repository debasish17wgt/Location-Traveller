package com.wgt.locationtraveller.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wgt.locationtraveller.R;
import com.wgt.locationtraveller.file.TrainInfoHandler;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements Response.ErrorListener, Response.Listener<String> {
    private RadioGroup rg_time, rg_train;
    private RadioButton rd_five_sec, rd_thirty_sec, rd_two_min, rd_five_min, rd_fifteen_min, rd_thirty_min,
            rd_train_one, rd_train_two, rd_train_three, rd_train_four, rd_train_five, rd_train_six;
    private TextView tv_train_name;
    private View v;
    private String train_no;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        v = view;
        initUIComponents(view);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Getting train details");
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        return view;
    }

    private void initUIComponents(final View view) {
        rg_time = view.findViewById(R.id.rg_time_imterval);
        rg_train = view.findViewById(R.id.rg_train_no);

        rd_five_sec = view.findViewById(R.id.rd_five_sec);
        rd_thirty_sec = view.findViewById(R.id.rd_thirty_sec);
        rd_two_min = view.findViewById(R.id.rd_two_min);
        rd_five_min = view.findViewById(R.id.rd_five_min);
        rd_fifteen_min = view.findViewById(R.id.rd_fifteen_min);
        rd_thirty_min = view.findViewById(R.id.rd_thirty_min);

        rd_train_one = view.findViewById(R.id.rd_train_one);
        rd_train_two = view.findViewById(R.id.rd_train_two);
        rd_train_three = view.findViewById(R.id.rd_train_three);
        rd_train_four = view.findViewById(R.id.rd_train_four);
        rd_train_five = view.findViewById(R.id.rd_train_five);
        rd_train_six = view.findViewById(R.id.rd_train_six);

        tv_train_name = view.findViewById(R.id.tv_train_name);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            rg_train.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //Toast.makeText(getContext(), "RADIO : "+group.getCheckedRadioButtonId()+":"+checkedId, Toast.LENGTH_SHORT).show();
                    //network call to get train details
                    if (v == null) {
                        Toast.makeText(getContext(), "ERROR : cant get selected train number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    train_no = ((RadioButton) v.findViewById(checkedId)).getText().toString();
                    doNetworkJob(train_no);
                }
            });
        }
    }

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
        VolleySingleton.getInstance(getContext()).addToRequestQueue(trainDetailsRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(getContext(), "ERROR : cant fetch train details.\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "ERROR : " + message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "ERROR : parsing error\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleResponse(JSONObject response) {
        TrainInfoModel trainInfoModel = null;
        List<RouteModel> routeModels = new ArrayList<>();
        String trainName = "";
        String message = "";
        try {
            trainName = response.getString("trainName");
            message = response.getString("message");
            JSONArray routeArr = new JSONArray(message);
            for (int i = 0; i < routeArr.length(); i++) {
                JSONObject obj = routeArr.getJSONObject(i);
                routeModels.add(new RouteModel(obj.getString("stationName"), obj.getString("arrival"), obj.getString("distTravelled")));
            }

            trainInfoModel = new TrainInfoModel(trainName, train_no, routeModels);
            boolean b = new TrainInfoHandler(getContext()).saveTrainDetails(trainInfoModel);
            if (b) {
                Toast.makeText(getContext(), "Train Details Saved locally", Toast.LENGTH_SHORT).show();
                tv_train_name.setText(trainName);
            } else {
                Toast.makeText(getContext(), "Train Details Failed to Saved locally", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "ERROR : failed to handle response", Toast.LENGTH_SHORT).show();
        }
    }
}
