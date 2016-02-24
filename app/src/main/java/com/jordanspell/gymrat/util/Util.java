package com.jordanspell.gymrat.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.jordanspell.gymrat.biz.PlateEnum;
import com.jordanspell.gymrat.dao.MySQLiteHelper;
import com.jordanspell.gymrat.model.Workout;
import com.jordanspell.gymrat.model.WorkoutExercise;
import com.jordanspell.gymrat.model.WorkoutSet;
import com.jordanspell.gymrat.model.WorkoutSetType;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by elfrank on 2/16/15.
 */
public class Util {

    public static Map calculatePlatesForLB(double totalWeight) {

        //These values should come from SharedPreferences -- to know what plates the user has avaible
        boolean fortyFivePounds = true;
        boolean thirtyFivePounds = false;
        boolean twentyFivePounds = true;
        boolean tenPounds = true;
        boolean fivePounds = true;
        boolean twoAndHalfPounds = true;

        int barWeight = 45;
        double weight;

        LinkedHashMap<PlateEnum, Integer> plates = new LinkedHashMap<>();

        double tempWeight = (totalWeight - barWeight) / 2;
        while (tempWeight > 0) {

            if (tempWeight >= 45) {

                if (fortyFivePounds) {
                    weight = tempWeight % 45;
                    int count = (int) ((tempWeight - weight) / 45.0);
                    tempWeight = weight;

                    plates.put(PlateEnum.FortyFiveLB, count);
                }
            }
            if (tempWeight >= 35) {

                if (thirtyFivePounds) {
                    weight = tempWeight % 35;
                    int count = (int) ((tempWeight - weight) / 35.0);
                    tempWeight = weight;

                    plates.put(PlateEnum.ThirtyFiveLB, count);
                }
            }
            if (tempWeight >= 25) {

                if (twentyFivePounds) {
                    weight = tempWeight % 25;
                    int count = (int) ((tempWeight - weight) / 25.0);
                    tempWeight = weight;

                    plates.put(PlateEnum.TwentyFiveLB, count);
                }
            }
            if (tempWeight >= 10) {
                if (tenPounds) {
                    weight = tempWeight % 10;
                    int count = (int) ((tempWeight - weight) / 10.0);
                    tempWeight = weight;

                    plates.put(PlateEnum.TenLB, count);
                }
            }
            if (tempWeight >= 5) {

                if (fivePounds) {
                    weight = tempWeight % 5;
                    int count = (int) ((tempWeight - weight) / 5.0);
                    tempWeight = weight;

                    plates.put(PlateEnum.FiveLB, count);
                }
            }
            if (tempWeight >= 2.5) {
                if (twoAndHalfPounds) {
                    weight = tempWeight % 2.5;
                    int count = (int) ((tempWeight - weight) / 2.5);
                    tempWeight = weight;

                    plates.put(PlateEnum.TwoAndHalfLB, count);
                }
            }

        }

        return plates;
    }

    public static Map calculatePlatesForKG(double totalWeight) {

        //These values should come from SharedPreferences -- to know what plates the user has avaible
        boolean twentyKilos = true;
        boolean tenKilos = false;
        boolean fiveKilos = true;
        boolean twoAndHalfKilos = true;
        boolean oneAndQuarterKilos = true;

        double barWeight = 20.0;
        double weight;

        LinkedHashMap<PlateEnum, Integer> plates = new LinkedHashMap<>();

        double tempWeight = (totalWeight - barWeight) / 2;
        while (tempWeight > 0) {

            if (tempWeight >= 20.0) {

                if (twentyKilos) {
                    weight = tempWeight % 20.0;
                    int count = (int) ((tempWeight - weight) / 20.0);
                    tempWeight = weight;

                    plates.put(PlateEnum.TwentyKG, count);
                }
            }
            if (tempWeight >= 10.0) {

                if (tenKilos) {
                    weight = tempWeight % 10.0;
                    int count = (int) ((tempWeight - weight) / 10.0);
                    tempWeight = weight;

                    plates.put(PlateEnum.TenKG, count);
                }
            }
            if (tempWeight >= 5.0) {

                if (fiveKilos) {
                    weight = tempWeight % 5.0;
                    int count = (int) ((tempWeight - weight) / 5.0);
                    tempWeight = weight;

                    plates.put(PlateEnum.FiveKG, count);
                }
            }
            if (tempWeight >= 2.5) {
                if (twoAndHalfKilos) {
                    weight = tempWeight % 2.5;
                    int count = (int) ((tempWeight - weight) / 2.5);
                    tempWeight = weight;

                    plates.put(PlateEnum.TwoAndHalfKG, count);
                }
            }
            if (tempWeight >= 1.25) {

                if (oneAndQuarterKilos) {
                    weight = tempWeight % 1.25;
                    int count = (int) ((tempWeight - weight) / 1.25);
                    tempWeight = weight;

                    plates.put(PlateEnum.OneAndQuarterKG, count);
                }
            }

        }

        return plates;
    }

    public static void exportData(ActionBarActivity activity) {
        MySQLiteHelper db = new MySQLiteHelper(activity);
        List<Workout> workoutList = db.getAllWorkouts();
        List<WorkoutSet> workoutSetList = db.getAllWorkoutSets();
        List<WorkoutExercise> workoutExerciseList = db.getAllWorkoutExercises();
        List<WorkoutSetType> workoutSetTypeList = db.getAllWorkoutSetTypes();
        try {
            File folder = new File(Environment.getExternalStorageDirectory(), "GymRat");
            if(!folder.exists()) {
                boolean success = folder.mkdirs();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyyHHmmss");
            File outFile = new File(folder, "GymRatExport_" + simpleDateFormat.format(new Date()) + ".csv");
            CSVWriter writer = new CSVWriter(new FileWriter(outFile), ',');
            writer.writeNext("WORKOUT_ID,WORKOUT_DATE,WORKOUT_ROTATION".split(","));
            for(int i=0; i<workoutList.size(); i++) {
                writer.writeNext(workoutList.get(i).getExportableData());
            }
            writer.writeNext("WORKOUT_SET_ID,WORKOUT_ID,WORKOUT_EXERCISE_ID,WORKOUT_SET_TYPE_ID,WEIGHT,REPS".split(","));
            for(int i=0; i<workoutSetList.size(); i++) {
                writer.writeNext(workoutSetList.get(i).getExportableData(db.getCurrentWeightType()));
            }
            writer.writeNext("WORKOUT_EXERCISE_ID,WORKOUT_EXERCISE_NAME,WORKOUT_ROTATION".split(","));
            for(int i=0; i<workoutExerciseList.size(); i++) {
                writer.writeNext(workoutExerciseList.get(i).getExportableData());
            }
            writer.writeNext("WORKOUT_SET_TYPE_ID,WORKOUT_SET_TYPE_NAME".split(","));
            for(int i=0; i<workoutSetTypeList.size(); i++) {
                writer.writeNext(workoutSetTypeList.get(i).getExportableData());
            }
            writer.close();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Workout data exported to device storage successfully to " + folder.toString())
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    });
            builder.create().show();
        }
        catch(Exception e) {
            Log.e("Failed to create CSV file of workout data: ", e.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Workout data failed to export.")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    });
            builder.create().show();
        }
    }
}
