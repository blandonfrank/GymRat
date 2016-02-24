package com.jordanspell.gymrat.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jordanspell.gymrat.R;
import com.jordanspell.gymrat.dao.MySQLiteHelper;
import com.jordanspell.gymrat.model.Workout;
import com.jordanspell.gymrat.model.WorkoutSet;

import java.util.Date;
import java.util.List;

public class HomeActivity extends ActionBarActivity {

    private MySQLiteHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setWindowAnimations(0);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.db = new MySQLiteHelper(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        for(int i=0; i<menu.size(); i++) {
            if(menu.getItem(i).getItemId() == R.id.action_change_weight_type) {
                if(this.db.getCurrentWeightType().equals("LB")) {
                    menu.getItem(i).setIcon(getResources().getDrawable(R.drawable.weight_type_lb_icon));
                }
                else {
                    menu.getItem(i).setIcon(getResources().getDrawable(R.drawable.weight_type_kg_icon));
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public void newWorkout(View view) {
        final Workout workout = this.db.getWorkoutByDate(new Date());
        if(workout.getDate() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Replace today's other exercise?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.deleteWorkout(workout);
                    List<WorkoutSet> workoutSetList = db.getAllWorkoutSetsByWorkoutID(workout.getId());
                    for(int i=0; i<workoutSetList.size(); i++) {
                        db.deleteWorkoutSet(workoutSetList.get(i));
                    }
                    db.updateConfigValue("Rotation", db.getNextWorkoutRotation());
                    Intent intent = new Intent(getActivity(), NewWorkoutActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }
        else {
            Intent intent = new Intent(getActivity(), NewWorkoutActivity.class);
            this.startActivity(intent);
        }
    }

    public void viewHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        this.startActivity(intent);
    }

    public void viewProgression(View view) {
        final List<Workout> workoutList = this.db.getAllWorkouts();
        if(workoutList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Let's hit the weights first champ.")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    });
            builder.create().show();
        }
        else {
            Intent intent = new Intent(this, ProgressionActivity.class);
            this.startActivity(intent);
        }
    }

    public void changeWeightType(MenuItem item) {
        this.db.updateConfigValue("WeightType", this.db.getNextWeightType());
        Intent intent = new Intent(this.getActivity(), HomeActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private HomeActivity getActivity() {
        return this;
    }
}
