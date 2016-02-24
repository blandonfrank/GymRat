package com.jordanspell.gymrat.model;

/**
 * Created by jorda_000 on 2/13/2015.
 */
public class WorkoutSetType {

    private int id;
    private String workoutSetTypeName;

    public WorkoutSetType() {

    }

    public WorkoutSetType(String workoutSetTypeName) {
        super();
        this.workoutSetTypeName = workoutSetTypeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkoutSetTypeName() {
        return workoutSetTypeName;
    }

    public void setWorkoutSetTypeName(String workoutSetTypeName) {
        this.workoutSetTypeName = workoutSetTypeName;
    }

    public String[] getExportableData() {
        return (Integer.toString(id) + "," + workoutSetTypeName).split(",");
    }
}
