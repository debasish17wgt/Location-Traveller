package com.wgt.locationtraveller.file;

import android.content.Context;

import com.wgt.locationtraveller.model.TrainInfoModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by debasish on 16-02-2018.
 */

public class TrainInfoHandler {
    private static final long serialVersionUID = 1L;
    private final String TRAIN_INFO_FILE = "train_details";

    private Context context;

    public TrainInfoHandler(Context context) {
        this.context = context;
    }

    public TrainInfoModel getTrainDetails() {
        TrainInfoModel trainInfoModel = null;
        try {
            FileInputStream fin = context.openFileInput(TRAIN_INFO_FILE);
            ObjectInputStream oin = new ObjectInputStream(fin);
            trainInfoModel = (TrainInfoModel) oin.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return trainInfoModel;
    }

    public boolean saveTrainDetails(TrainInfoModel trainInfoModel) {
        if (trainInfoModel == null || trainInfoModel.getRouteList().size() < 1) {
            return false;
        }
        try {
            FileOutputStream fout = context.openFileOutput(TRAIN_INFO_FILE, context.MODE_PRIVATE);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(trainInfoModel);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
