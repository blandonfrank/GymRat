package com.jordanspell.gymrat.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jordanspell.gymrat.R;
import com.jordanspell.gymrat.dao.MySQLiteHelper;
import com.jordanspell.gymrat.model.Workout;
import com.jordanspell.gymrat.model.WorkoutExercise;
import com.jordanspell.gymrat.model.WorkoutSet;
import com.jordanspell.gymrat.screenObj.WorkoutExerciseSection;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NewWorkoutActivity extends ActionBarActivity implements WorkoutActivity {

    private MySQLiteHelper db;
    private LinearLayout linearLayout;
    private LinearLayout bottomButtonLayout;
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

        this.workoutRotationExerciseList = this.db.getAllWorkoutExercisesByRotation(this.db.getCurrentWorkoutRotation());
        this.displayedWorkoutExerciseSections = new LinkedList<>();

        for(int i=0; i< workoutRotationExerciseList.size(); i++) {
            this.createExerciseRow(workoutRotationExerciseList.get(i));
        }

        this.createBottomButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_workout, menu);
        for(int i=0; i<menu.size(); i++) {
            if(menu.getItem(i).getItemId() == R.id.action_change_rotation) {
                if(this.db.getCurrentWorkoutRotation().equals("A")) {
                    menu.getItem(i).setIcon(getResources().getDrawable(R.drawable.rotation_a_icon));
                }
                else {
                    menu.getItem(i).setIcon(getResources().getDrawable(R.drawable.rotation_b_icon));
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
        int id = item.getItemId();

        if(id == android.R.id.home) {
            this.confirmQuit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.confirmQuit();
    }

    private void createExerciseRow(WorkoutExercise workoutExercise) {
        this.displayedWorkoutExerciseSections.add(new WorkoutExerciseSection(-1, workoutExercise, this));
    }

    /**
     * Iterates through all displayed exercise rows and inserts records in Workout and WorkoutSet tables. Also flips the current rotation flag.
     */
    private void saveWorkout() {
        String workoutExerciseName;
        int workoutWeight;

        List<WorkoutSet> workoutSetList = new LinkedList<>();

        for (int i = 0; i < displayedWorkoutExerciseSections.size(); i++) {
            workoutExerciseName = displayedWorkoutExerciseSections.get(i).getWorkoutExerciseName();
            workoutWeight = displayedWorkoutExerciseSections.get(i).getWorkoutExerciseWeight();

            // Create one workout set table entry for each button in the row
            for (int j = 0; j < displayedWorkoutExerciseSections.get(i).getExerciseRowButtons().size(); j++) {
                WorkoutSet workoutSet = new WorkoutSet();
                workoutSet.setWorkoutExerciseID(this.db.getWorkoutExerciseByName(workoutExerciseName).getId());
                workoutSet.setWorkoutSetTypeID(displayedWorkoutExerciseSections.get(i).getWorkoutSetTypeId());
                workoutSet.setWeight(workoutWeight);
                if(displayedWorkoutExerciseSections.get(i).getExerciseRowButtons().get(j).getText().toString().equals("")) {
                    workoutSet.setReps(0);
                }
                else {
                    workoutSet.setReps(Integer.parseInt(displayedWorkoutExerciseSections.get(i).getExerciseRowButtons().get(j).getText().toString()));
                }
                workoutSetList.add(workoutSet);
            }
        }

        Workout workout = new Workout(this.db.getCurrentWorkoutRotation());

        if(this.getIntent().getLongExtra("Date", -1) != -1) {
            Date date = new Date();
            date.setTime(this.getIntent().getLongExtra("Date", -1));
            workout.setDate(date);
        }

        int workoutID = (int) this.db.addWorkout(workout);

        for(int j=0; j<workoutSetList.size(); j++) {
            this.db.addWorkoutSet(new WorkoutSet(workoutID, workoutSetList.get(j).getWorkoutExerciseID(), workoutSetList.get(j).getWorkoutSetTypeID(),
                    workoutSetList.get(j).getWeight(), workoutSetList.get(j).getReps()));
        }

        this.db.updateConfigValue("Rotation", this.db.getNextWorkoutRotation());

        this.finish();
    }

    /**
     * Creates a dialog box that allows the user to add extra exercises to their workout
     */
    public void addExercise(final MenuItem item) {
        final List<WorkoutExercise> extraWorkoutExerciseList = this.db.getAllWorkoutExercisesByRotation("C");

        List<String> extraWorkoutExerciseNames = new LinkedList<>();

        Iterator<WorkoutExercise> i = extraWorkoutExerciseList.iterator();

        // Remove workouts from the list that already exist on the screen. (ie. user adds curls then hit "Add Exercise" again, curls won't show in the dialog)
        while (i.hasNext()) {
            boolean alreadyExists = false;
            WorkoutExercise workoutExercise = i.next();
            for(int j=0; j<this.displayedWorkoutExerciseSections.size(); j++) {
                if(workoutExercise.getId() == this.displayedWorkoutExerciseSections.get(j).getWorkoutExerciseId()) {
                    alreadyExists = true;
                }
            }
            if(!alreadyExists) {
                extraWorkoutExerciseNames.add(workoutExercise.getWorkoutExerciseName());
            }
            else {
                i.remove();
            }
        }

        final CharSequence[] extraWorkoutExerciseNameArray = extraWorkoutExerciseNames.toArray(new CharSequence[extraWorkoutExerciseNames.size()]);
        final boolean[] extraWorkoutExerciseNameArrayStates = new boolean[extraWorkoutExerciseNames.size()];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add what exercise(s)?");
        builder.setMultiChoiceItems(extraWorkoutExerciseNameArray, extraWorkoutExerciseNameArrayStates, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialogInterface, int item, boolean state) {
            }
        });
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SparseBooleanArray checkBoxArray = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                boolean addedNewExercise = false;
                // When the user clicks Okay in the dialog box, iterate through the exercise list to find the name that matches the checked name and add exercise to screen
                for (int j = 0; j < extraWorkoutExerciseList.size(); j++) {
                    if (checkBoxArray.get(j)) {
                        if (((AlertDialog) dialog).getListView().getAdapter().getItem(j).toString().equals(extraWorkoutExerciseList.get(j).getWorkoutExerciseName())) {
                            createExerciseRow(extraWorkoutExerciseList.get(j));
                            addedNewExercise = true;
                        }
                    }
                }
                if (addedNewExercise) {
                    linearLayout.removeView(bottomButtonLayout);
                    createBottomButtons();
                }
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("+/-", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Create/Delete Exercise(s)");
                for(int i=0; i<extraWorkoutExerciseNameArrayStates.length; i++) {
                    extraWorkoutExerciseNameArrayStates[i] = false;
                }
                builder.setMultiChoiceItems(extraWorkoutExerciseNameArray, extraWorkoutExerciseNameArrayStates, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialogInterface, int item, boolean state) {
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        addExercise(item);
                    }
                });
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Name of new exercise?");
                        final EditText newExerciseText = new EditText(getActivity());
                        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                WorkoutExercise workoutExercise = db.getWorkoutExerciseByName(newExerciseText.getText().toString().trim());
                                if (workoutExercise.getWorkoutExerciseName() == null) {
                                    db.addWorkoutExercise(new WorkoutExercise(newExerciseText.getText().toString().trim(), "C"));
                                }
                                dialog.dismiss();
                                addExercise(item);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                addExercise(item);
                            }
                        });
                        builder.setView(newExerciseText);
                        final AlertDialog newExerciseDialog = builder.create();
                        newExerciseText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    newExerciseDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                }
                            }
                        });
                        newExerciseDialog.show();
                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SparseBooleanArray checkBoxArray = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                        for (int i = 0; i < extraWorkoutExerciseList.size(); i++) {
                            if (checkBoxArray.get(i)) {
                                if (((AlertDialog) dialog).getListView().getAdapter().getItem(i).toString().equals(extraWorkoutExerciseList.get(i).getWorkoutExerciseName())) {
                                    db.deleteWorkoutSetByWorkoutExerciseID(extraWorkoutExerciseList.get(i));
                                    db.deleteWorkoutExerciseByName(extraWorkoutExerciseList.get(i));
                                }
                            }
                        }
                        dialog.dismiss();
                        addExercise(item);
                    }
                });
                builder.create().show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void changeRotation(MenuItem item) {
        this.db.updateConfigValue("Rotation", this.db.getNextWorkoutRotation());

        Intent intent;

        if(this.getIntent().getLongExtra("Date", -1) != -1) {
            Date date = new Date();
            date.setTime(this.getIntent().getLongExtra("Date", -1));
            intent = new Intent(this.getActivity(), NewWorkoutActivity.class);
            intent.putExtra("Date", date.getTime());
        }
        else {
            intent = new Intent(this.getActivity(), NewWorkoutActivity.class);
        }

        this.startActivity(intent);
        this.finish();
    }

    private void createBottomButtons() {
        this.bottomButtonLayout = new LinearLayout(this);
        Button saveWorkoutButton = new Button(this);
        saveWorkoutButton.setText("Save Workout");
        saveWorkoutButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                saveWorkout();
            }
        });
        saveWorkoutButton.setBackgroundResource(R.drawable.action_button_style);
        LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(150,25,150,25);
        saveWorkoutButton.setLayoutParams(layoutParams);
        this.bottomButtonLayout.addView(saveWorkoutButton);

        this.linearLayout.addView(this.bottomButtonLayout);
    }

    private void confirmQuit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit exercise?");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
