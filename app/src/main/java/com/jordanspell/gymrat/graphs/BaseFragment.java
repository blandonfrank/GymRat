package com.jordanspell.gymrat.graphs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.jordanspell.gymrat.biz.WorkoutExerciseEnum;
import com.jordanspell.gymrat.dao.MySQLiteHelper;
import com.jordanspell.gymrat.model.Workout;
import com.jordanspell.gymrat.model.WorkoutExercise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by elfrank on 3/3/15.
 */
public class BaseFragment extends Fragment {
    private MySQLiteHelper db;

    ArrayList<Entry> squatEntries;
    ArrayList<Entry> deadLiftEntries;
    ArrayList<Entry> overHeadPressEntries;
    ArrayList<Entry> benchPressEntries;
    ArrayList<Entry> bentOverRowsEntries;
    LineData allExercisesLineData;

    Map<WorkoutExerciseEnum, LineData> exerciseTypeLineDataMap;
    List<Workout> workouts;

    public BaseFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }



    protected LineData getLineData(int pos) {

        LineData lineData = null;
        db = new MySQLiteHelper(getActivity());
        getAllData();


        switch (pos){
            case 0:
                lineData = allExercisesLineData;
                break;
            case 1:
                lineData = exerciseTypeLineDataMap.get(WorkoutExerciseEnum.SQUAT);
                break;
            case 2:
                lineData = exerciseTypeLineDataMap.get(WorkoutExerciseEnum.BENCH_PRESS);
                break;
            case 3:
                lineData = exerciseTypeLineDataMap.get(WorkoutExerciseEnum.BARBELL_ROW);
                break;
            case 4:
                lineData = exerciseTypeLineDataMap.get(WorkoutExerciseEnum.OVERHEAD_PRESS);
                break;
            case 5:
                lineData = exerciseTypeLineDataMap.get(WorkoutExerciseEnum.DEADLIFT);
                break;

        }
        lineData.setValueFormatter(getWholeNumberValueFormatter());

        return lineData;
    }



    private LineData getAllData(){
        squatEntries = new ArrayList<>();
        deadLiftEntries = new ArrayList<>();
        overHeadPressEntries = new ArrayList<>();
        benchPressEntries = new ArrayList<>();
        bentOverRowsEntries = new ArrayList<>();

        exerciseTypeLineDataMap = new HashMap<>();

        workouts = db.getAllWorkouts();

        SimpleDateFormat dt = new SimpleDateFormat("MM/dd");


        //Sortout worksouts by Date
        Collections.sort(workouts, new Comparator<Workout>() {
            @Override
            public int compare(Workout lhs, Workout rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });

        int numOfWorkOuts = workouts.size();

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < numOfWorkOuts; i++) {
            xVals.add(dt.format(workouts.get(i).getDate()));
        }


        ArrayList<LineDataSet> dataSets = new ArrayList<>();

        List<WorkoutExercise> listOfWorkoutExercises = db.getAllStandardWorkoutExercises();

        String currentWeightType = db.getCurrentWeightType();

        for(int i=0; i < numOfWorkOuts; i++){

            int workoutID = workouts.get(i).getId();
            List<WorkoutExercise> workoutExercises = db.getAllStandardWorkoutExercisesByWorkoutID(workoutID);

            for(WorkoutExercise workoutExercise : workoutExercises){
                if(currentWeightType.equals("LB")) {
                    int weight = db.getWeightByWorkoutIdAndWorkoutExerciseId(workoutID, workoutExercise.getId());
                    switch (WorkoutExerciseEnum.getByName(workoutExercise.getWorkoutExerciseName())) {
                        case SQUAT:
                            squatEntries.add(new Entry(weight, i));
                            break;
                        case BENCH_PRESS:
                            benchPressEntries.add(new Entry(weight,i));
                            break;
                        case BARBELL_ROW:
                            bentOverRowsEntries.add(new Entry(weight,i));
                            break;
                        case OVERHEAD_PRESS:
                            overHeadPressEntries.add(new Entry(weight,i));
                            break;
                        case DEADLIFT:
                            deadLiftEntries.add(new Entry(weight,i));
                            break;
                    }
                }
                else {
                    double weight = db.getWeightByWorkoutIdAndWorkoutExerciseId(workoutID, workoutExercise.getId()) / 2.25;
                    if(weight % 2.5 != 0) {
                        double mod = weight % 2.5;
                        weight = weight + (2.5 - mod);
                    }
                    switch (WorkoutExerciseEnum.getByName(workoutExercise.getWorkoutExerciseName())) {
                        case SQUAT:
                            squatEntries.add(new Entry((float) weight, i));
                            break;
                        case BENCH_PRESS:
                            benchPressEntries.add(new Entry((float) weight,i));
                            break;
                        case BARBELL_ROW:
                            bentOverRowsEntries.add(new Entry((float) weight,i));
                            break;
                        case OVERHEAD_PRESS:
                            overHeadPressEntries.add(new Entry((float) weight,i));
                            break;
                        case DEADLIFT:
                            deadLiftEntries.add(new Entry((float) weight,i));
                            break;
                    }
                }
            }

        }


        //Create the line data sets in the Y axis for each exercise
        for(int i=0; i < listOfWorkoutExercises.size(); i++){
            switch (WorkoutExerciseEnum.getByName(listOfWorkoutExercises.get(i).getWorkoutExerciseName())){
                case SQUAT:
                    if(squatEntries.size() > 0) {
                        LineDataSet squatLineDataSet = new LineDataSet(squatEntries, "Squats");
                        squatLineDataSet.setColor(ColorTemplate.getHoloBlue());
                        squatLineDataSet.setCircleColor(ColorTemplate.getHoloBlue());
                        squatLineDataSet.setLineWidth(2f);
                        squatLineDataSet.setCircleSize(4f);
                        squatLineDataSet.setFillAlpha(65);
                        squatLineDataSet.setFillColor(ColorTemplate.getHoloBlue());
                        squatLineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
                        dataSets.add(squatLineDataSet);
                        exerciseTypeLineDataMap.put(WorkoutExerciseEnum.SQUAT, new LineData(xVals, squatLineDataSet));
                    }
                    break;
                case BENCH_PRESS:
                    if(benchPressEntries.size() > 0){
                        LineDataSet benchPressLineDataSet = new LineDataSet(benchPressEntries, "Bench Press");
                        benchPressLineDataSet.setColor(Color.GREEN);
                        benchPressLineDataSet.setCircleColor(Color.GREEN);
                        benchPressLineDataSet.setLineWidth(2f);
                        benchPressLineDataSet.setCircleSize(4f);
                        benchPressLineDataSet.setFillAlpha(65);
                        benchPressLineDataSet.setFillColor(Color.GREEN);
                        benchPressLineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
                        dataSets.add(benchPressLineDataSet);
                        exerciseTypeLineDataMap.put(WorkoutExerciseEnum.BENCH_PRESS, new LineData(xVals, benchPressLineDataSet));
                    }
                    break;
                case BARBELL_ROW:
                    if(benchPressEntries.size() > 0){
                        LineDataSet bentOverRowsLineDataSet = new LineDataSet(bentOverRowsEntries, "Barbell Row");
                        bentOverRowsLineDataSet.setColor(Color.YELLOW);
                        bentOverRowsLineDataSet.setCircleColor(Color.YELLOW);
                        bentOverRowsLineDataSet.setLineWidth(2f);
                        bentOverRowsLineDataSet.setCircleSize(4f);
                        bentOverRowsLineDataSet.setFillAlpha(65);
                        bentOverRowsLineDataSet.setFillColor(Color.YELLOW);
                        bentOverRowsLineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
                        dataSets.add(bentOverRowsLineDataSet);
                        exerciseTypeLineDataMap.put(WorkoutExerciseEnum.BARBELL_ROW, new LineData(xVals, bentOverRowsLineDataSet));
                    }
                    break;
                case OVERHEAD_PRESS:
                    if(overHeadPressEntries.size() > 0){
                        LineDataSet overHeadPressLineDataSet = new LineDataSet(overHeadPressEntries, "Overhead Press");
                        overHeadPressLineDataSet.setColor(Color.MAGENTA);
                        overHeadPressLineDataSet.setCircleColor(Color.MAGENTA);
                        overHeadPressLineDataSet.setLineWidth(2f);
                        overHeadPressLineDataSet.setCircleSize(4f);
                        overHeadPressLineDataSet.setFillAlpha(65);
                        overHeadPressLineDataSet.setFillColor(Color.MAGENTA);
                        overHeadPressLineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
                        dataSets.add(overHeadPressLineDataSet);
                        exerciseTypeLineDataMap.put(WorkoutExerciseEnum.OVERHEAD_PRESS, new LineData(xVals, overHeadPressLineDataSet));
                    }
                    break;
                case DEADLIFT:
                    if(deadLiftEntries.size() > 0){
                        LineDataSet deadLiftLineDataSet = new LineDataSet(deadLiftEntries, "Deadlift");
                        deadLiftLineDataSet.setColor(Color.RED);
                        deadLiftLineDataSet.setCircleColor(Color.RED);
                        deadLiftLineDataSet.setLineWidth(2f);
                        deadLiftLineDataSet.setCircleSize(4f);
                        deadLiftLineDataSet.setFillAlpha(65);
                        deadLiftLineDataSet.setFillColor(Color.RED);
                        deadLiftLineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
                        dataSets.add(deadLiftLineDataSet);
                        exerciseTypeLineDataMap.put(WorkoutExerciseEnum.DEADLIFT, new LineData(xVals, deadLiftLineDataSet));
                    }
                    break;
            }
        }


        // create a data object with the datasets
        allExercisesLineData = new LineData(xVals,dataSets);
        return allExercisesLineData;
    }


    protected ValueFormatter getWholeNumberValueFormatter(){
        return new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(db.getCurrentWeightType().equals("LB")) {
                    return (int)value + "";
                }
                else {
                    return value + "";
                }
            }
        };
    }
}
