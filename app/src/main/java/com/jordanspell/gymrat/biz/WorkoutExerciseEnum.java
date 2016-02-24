package com.jordanspell.gymrat.biz;

/**
 * Created by elfrank on 3/3/15.
 */
public enum WorkoutExerciseEnum {
    SQUAT("Squats"),
    BENCH_PRESS("Bench Press"),
    BARBELL_ROW("Barbell Row"),
    OVERHEAD_PRESS("Overhead Press"),
    DEADLIFT("Deadlift");

    private String exerciseName = "";

    private WorkoutExerciseEnum(String exerciseName){
        this.exerciseName = exerciseName;
    }

    public String getExerciseName() {
        return exerciseName;
    }


    public String getName() {
        return exerciseName;
    }

    public static WorkoutExerciseEnum getByName(String name){
        for(WorkoutExerciseEnum workoutExerciseEnum : values()){
            if(workoutExerciseEnum.getName().equals(name)){
                return workoutExerciseEnum;
            }
        }

        throw new IllegalArgumentException(name + " is not a valid PropName");
    }
}

