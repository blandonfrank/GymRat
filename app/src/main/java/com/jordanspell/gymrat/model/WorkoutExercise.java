package com.jordanspell.gymrat.model;

import android.nfc.tech.IsoDep;

/**
 * Created by jorda_000 on 2/13/2015.
 */
public class WorkoutExercise {

    private int id;
    private String workoutExerciseName;
    private String rotation;

    public int isPrimary() {
        return isPrimary;
    }

    public void setPrimary(int isPrimary) {
        this.isPrimary = isPrimary;
    }

    private int isPrimary;

    public WorkoutExercise() {

    }

    public WorkoutExercise(String workoutExerciseName, String rotation) {
        super();
        this.workoutExerciseName = workoutExerciseName;
        this.rotation = rotation;
    }

    public WorkoutExercise(String workoutExerciseName, String rotation, int isPrimary) {
        super();
        this.workoutExerciseName = workoutExerciseName;
        this.rotation = rotation;
        this.isPrimary = isPrimary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkoutExerciseName() {
        return workoutExerciseName;
    }

    public void setWorkoutExerciseName(String workoutExerciseName) {
        this.workoutExerciseName = workoutExerciseName;
    }

    public String getRotation() {
        return rotation;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public String[] getExportableData() {
        return (Integer.toString(this.id) + "," + this.workoutExerciseName + "," + this.rotation).split(",");
    }
}
