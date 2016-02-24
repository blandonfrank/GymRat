package com.jordanspell.gymrat.model;

/**
 * Created by jorda_000 on 2/13/2015.
 */
public class WorkoutSet {

    private int id;
    private int workoutID;
    private int workoutExerciseID;
    private int workoutSetTypeID;
    private int weight;
    private int reps;

    public WorkoutSet() {

    }

    public WorkoutSet(int workoutID, int workoutExerciseID, int workoutSetTypeID, int weight, int reps) {
        super();
        this.workoutID = workoutID;
        this.workoutExerciseID = workoutExerciseID;
        this.workoutSetTypeID = workoutSetTypeID;
        this.weight = weight;
        this.reps = reps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkoutID() {
        return workoutID;
    }

    public void setWorkoutID(int workoutID) {
        this.workoutID = workoutID;
    }

    public int getWorkoutExerciseID() {
        return workoutExerciseID;
    }

    public void setWorkoutExerciseID(int workoutExerciseID) {
        this.workoutExerciseID = workoutExerciseID;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getWorkoutSetTypeID() {
        return workoutSetTypeID;
    }

    public void setWorkoutSetTypeID(int workoutSetTypeID) {
        this.workoutSetTypeID = workoutSetTypeID;
    }

    public String[] getExportableData(String currentWeightType) {
        return (Integer.toString(this.id) + "," + Integer.toString(this.workoutID) + "," + Integer.toString(this.workoutExerciseID) + "," + Integer.toString(this.workoutSetTypeID) +
                "," + getWeightTypeDisplay(currentWeightType, this.weight) + "," + Integer.toString(this.reps)).split(",");
    }

    private String getWeightTypeDisplay(String currentWeightType, double weight) {
        if(currentWeightType.equals("LB")) {
            return Double.toString(weight);
        }
        else {
            double kgWeight = weight / 2.25;
            if(kgWeight % 2.5 == 0) {
                return Double.toString(kgWeight);
            }
            else {
                double mod = kgWeight % 2.5;
                return Double.toString(kgWeight + (2.5 - mod));
            }
        }
    }
}
