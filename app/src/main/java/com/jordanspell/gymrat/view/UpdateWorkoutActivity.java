package com.jordanspell.gymrat.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jordanspell.gymrat.R;
import com.jordanspell.gymrat.dao.MySQLiteHelper;
import com.jordanspell.gymrat.model.Workout;
import com.jordanspell.gymrat.model.WorkoutExercise;
import com.jordanspell.gymrat.model.WorkoutSet;
import com.jordanspell.gymrat.screenObj.WorkoutExerciseSection;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UpdateWorkoutActivity extends ActionBarActivity implements WorkoutActivity {

    private MySQLiteHelper db;
    private Workout workout;
    private LinearLayout linearLayout;
    private List<WorkoutExercise> workoutRotationExerciseList;
    private List<WorkoutExerciseSection> displayedWorkoutExerciseSections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        getWindow().setWindowAnimations(0);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.db = new MySQLiteHelper(this);

        this.linearLayout = (LinearLayout)findViewById(R.id.workoutLinearLayout);

        Date date = new Date();
        date.setTime(this.getIntent().getLongExtra("Date", -1));
        this.workout = this.db.getWorkoutByDate(date);

        this.workoutRotationExerciseList = this.db.getAllWorkoutExercisesByRotation(this.workout.getRotation());
        this.displayedWorkoutExerciseSections = new LinkedList<>();

        List<WorkoutExercise> workoutExerciseList = this.db.getAllWorkoutExercisesByWorkoutID(this.workout.getId());

        for(int i=0; i<workoutExerciseList.size(); i++) {
            this.createExerciseRow(this.workout.getId(), workoutExerciseList.get(i));
        }

        this.createBottomButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    /**
     * Iterates through all displayed exercise rows and updates records in WorkoutSet table.
     */
    private void updateWorkout() {
        List<WorkoutSet> workoutSetList = this.db.getAllWorkoutSetsByWorkoutID(this.workout.getId());
        WorkoutSet workoutSet;

        for (int i=0; i< displayedWorkoutExerciseSections.size(); i++) {
            int setCount = 0;
            for(int j=0; j<workoutSetList.size(); j++) {
                if(displayedWorkoutExerciseSections.get(i).getWorkoutExerciseId() == workoutSetList.get(j).getWorkoutExerciseID()) {
                    workoutSet = new WorkoutSet();
                    workoutSet.setId(workoutSetList.get(j).getId());
                    workoutSet.setWorkoutExerciseID(displayedWorkoutExerciseSections.get(i).getWorkoutExerciseId());
                    workoutSet.setWeight(displayedWorkoutExerciseSections.get(i).getWorkoutExerciseWeight());
                    if(displayedWorkoutExerciseSections.get(i).getExerciseRowButtons().get(setCount).getText().toString().equals("")) {
                        workoutSet.setReps(0);
                    }
                    else {
                        workoutSet.setReps(Integer.parseInt(displayedWorkoutExerciseSections.get(i).getExerciseRowButtons().get(setCount).getText().toString()));
                    }
                    this.db.updateWorkoutSet(workoutSet);
                    setCount++;
                }
            }
        }

        this.finish();
    }

    private void createExerciseRow(int workoutId, WorkoutExercise workoutExercise) {
        this.displayedWorkoutExerciseSections.add(new WorkoutExerciseSection(workoutId, workoutExercise, this));
    }

    private void createBottomButtons() {
        LinearLayout bottomButtonLayout = new LinearLayout(this);
        Button updateWorkoutButton = new Button(this);
        updateWorkoutButton.setText("Update Workout");
        updateWorkoutButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                updateWorkout();
            }
        });
        updateWorkoutButton.setBackgroundResource(R.drawable.action_button_style);
        LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(150,25,150,25);
        updateWorkoutButton.setLayoutParams(layoutParams);
        bottomButtonLayout.addView(updateWorkoutButton);

        this.linearLayout.addView(bottomButtonLayout);
    }

    public MySQLiteHelper getDb() {
        return this.db;
    }

    public List<WorkoutExercise> getWorkoutRotationExerciseList() {
        return this.workoutRotationExerciseList;
    }

    public List<WorkoutExerciseSection> getDisplayedWorkoutExerciseSections() {
        return this.displayedWorkoutExerciseSections;
    }

    public ActionBarActivity getActivity() {
        return this;
    }
}
