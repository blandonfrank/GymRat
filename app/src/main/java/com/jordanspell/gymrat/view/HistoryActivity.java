package com.jordanspell.gymrat.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jordanspell.gymrat.R;
import com.jordanspell.gymrat.dao.MySQLiteHelper;
import com.jordanspell.gymrat.model.Workout;
import com.jordanspell.gymrat.model.WorkoutSet;
import com.jordanspell.gymrat.util.Util;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends ActionBarActivity {

    private MySQLiteHelper db;
    private Map<Date,Workout> workoutByDateMap = new HashMap<>();
    CaldroidFragment caldroidFragment;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getWindow().setWindowAnimations(0);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.db = new MySQLiteHelper(this);

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        List<Workout> workoutList = this.db.getAllWorkouts();


        for(int i=0; i<workoutList.size(); i++) {
            workoutByDateMap.put(workoutList.get(i).getDate(),workoutList.get(i));
            caldroidFragment.setBackgroundResourceForDate(R.color.lightblue, workoutList.get(i).getDate());
        }


        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.historyCalendar, caldroidFragment);
        t.commit();

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                Workout workout = workoutByDateMap.get(date);
                if(workout!=null){
                    Intent intent = new Intent(HistoryActivity.this, UpdateWorkoutActivity.class);
                    intent.putExtra("Date", workout.getDate().getTime());
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(HistoryActivity.this, NewWorkoutActivity.class);
                    intent.putExtra("Date", date.getTime());
                    startActivity(intent);
                }

            }


            @Override
            public void onLongClickDate(final Date date, final View view){
                final Workout workout = workoutByDateMap.get(date);
                if(workout !=null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                    builder.setTitle("Do you want to delete this workout?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(WorkoutSet workoutSet: db.getAllWorkoutSetsByWorkoutID(workout.getId()))
                                db.deleteWorkoutSet(workoutSet);
                            db.deleteWorkout(workout);
                            workoutByDateMap.remove(date);

                            caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_white, date); // super hacky but it makes the cal to refresh
                            caldroidFragment.refreshView();


                        }

                });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
            }

        }};

        caldroidFragment.setCaldroidListener(listener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Workout> workoutList = this.db.getAllWorkouts();
        workoutByDateMap = new HashMap<>();

        for(int i=0; i<workoutList.size(); i++) {
            workoutByDateMap.put(workoutList.get(i).getDate(),workoutList.get(i));
            caldroidFragment.setBackgroundResourceForDate(R.color.lightblue, workoutList.get(i).getDate()); // super hacky but it makes the cal to refresh
        }
        caldroidFragment.refreshView();
    }

    public void exportData(MenuItem item) {
        Util.exportData(this);
    }
}
