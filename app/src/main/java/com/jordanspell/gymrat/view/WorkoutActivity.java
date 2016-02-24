package com.jordanspell.gymrat.view;

import com.jordanspell.gymrat.dao.MySQLiteHelper;
import com.jordanspell.gymrat.model.WorkoutExercise;
import com.jordanspell.gymrat.screenObj.WorkoutExerciseSection;

import java.util.List;

/**
 * Created by jorda_000 on 2/23/2015.
 */
public interface WorkoutActivity {

    public MySQLiteHelper getDb();

    public List<WorkoutExercise> getWorkoutRotationExerciseList();

    public List<WorkoutExerciseSection> getDisplayedWorkoutExerciseSections();
}
