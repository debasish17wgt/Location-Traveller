package com.wgt.locationtraveller.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import com.wgt.locationtraveller.R;
import com.wgt.locationtraveller.database.AppDatabase;
import com.wgt.locationtraveller.dialog.AddTrainDialogUtil;
import com.wgt.locationtraveller.model.TrainInfoModel;
import com.wgt.locationtraveller.preference.TrainPref;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, AddTrainDialogUtil.TrainAddedListener {
    private RadioGroup rg_time, rg_train;
    private RadioButton rd_five_sec, rd_thirty_sec, rd_two_min, rd_five_min, rd_fifteen_min, rd_thirty_min;
    //rd_train_one, rd_train_two, rd_train_three, rd_train_four, rd_train_five, rd_train_six;
    private TextView tv_train_name;
    private View v;
    private String train_no;
    private int rbtn_train_no;

    private FloatingActionButton fab;
    private boolean isVisible;

    private AppDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        v = view;
        initUIComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg_train.getChildCount() == 6) {
                    Toast.makeText(getContext(), "You have reached maximim number of train's limit", Toast.LENGTH_SHORT).show();
                    return;
                }
                AddTrainDialogUtil trainDialogUtil = new AddTrainDialogUtil(getContext(), SettingsFragment.this);
                trainDialogUtil.showDialog();
            }
        });

        rg_train.setOnCheckedChangeListener(this);
        database = AppDatabase.getDatabase(getContext());
        updateUI(getContext());
    }

    private void updateUI(Context context) {
        /*TrainInfoHandler trainInfoHandler = new TrainInfoHandler(getContext());
        TrainInfoModel trainInfoModel  = trainInfoHandler.getTrainDetails();
        if (trainInfoModel != null) {
            tv_train_name.setText(trainInfoModel.getTrainName());
            rg_train.check(trainInfoModel.getRbtn_train());
            //((RadioButton)(v.findViewById(trainInfoModel.getRbtn_train()))).setChecked(true);
        }*/

        populateTrainRadioButtons(context);


    }

    private void populateTrainRadioButtons(Context context) {
        rg_train.removeAllViews();
        List<TrainInfoModel> trains = database.trainInfoDao().getAllTrainInfo();
        int checkedTrain = new TrainPref(context).getTrainNumber();
        if (trains != null && trains.size() >= 1) {
            for (final TrainInfoModel train : trains) {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setId(train.getTrainNo());
                radioButton.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                radioButton.setText("" + train.getTrainNo());
                radioButton.setTextColor(getResources().getColor(android.R.color.white));
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                if (checkedTrain == train.getTrainNo()) {
                    radioButton.setChecked(true);
                }
                //uncomment to enable delete feature
                /*radioButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteTrain(train.getTrainNo());
                        return false;
                    }
                });*/
                rg_train.addView(radioButton);
            }
        }
    }

    private void deleteTrain(final int trainNo) {
        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
        deleteDialog.setTitle("Delete train " + trainNo)
                .setMessage("Are you sure to delete ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete train
                        database.trainInfoDao().deleteTrain(trainNo);
                        populateTrainRadioButtons(getContext());
                    }
                })
                .setNegativeButton("Calcel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

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

        /*rd_train_one = view.findViewById(R.id.rd_train_one);
        rd_train_two = view.findViewById(R.id.rd_train_two);
        rd_train_three = view.findViewById(R.id.rd_train_three);
        rd_train_four = view.findViewById(R.id.rd_train_four);
        rd_train_five = view.findViewById(R.id.rd_train_five);
        rd_train_six = view.findViewById(R.id.rd_train_six);*/

        tv_train_name = view.findViewById(R.id.tv_train_name);

        fab = view.findViewById(R.id.fab);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (!isVisible) return;
        new TrainPref(getContext()).saveTrainNumber(checkedId);

    }

    @Override
    public void onTrainAdded() {
        populateTrainRadioButtons(getContext());
    }
}
