package com.jordanspell.gymrat.screenObj;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jordanspell.gymrat.R;
import com.jordanspell.gymrat.biz.PlateEnum;
import com.jordanspell.gymrat.biz.WorkoutExerciseEnum;
import com.jordanspell.gymrat.biz.WorkoutSetTypeEnum;
import com.jordanspell.gymrat.model.WorkoutExercise;
import com.jordanspell.gymrat.model.WorkoutSet;
import com.jordanspell.gymrat.util.Util;
import com.jordanspell.gymrat.view.NewWorkoutActivity;
import com.jordanspell.gymrat.view.UpdateWorkoutActivity;
import com.jordanspell.gymrat.view.WorkoutActivity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jorda_000 on 2/20/2015.
 */
public class WorkoutExerciseSection {

    private int workoutExerciseId;
    private String workoutExerciseName;
    private int workoutExerciseWeight;
    private String weightType;
    private int workoutSetTypeId;
    private LinearLayout parentLinearLayout;
    private LinearLayout exerciseRowLinearLayout;
    private TableLayout buttonTableLayout;
    private LinearLayout currentWeightCalcLayout;
    private TableRow buttonRow;

    private float exerciseRemoveSliderX = Float.NaN;
    private final int exerciseRemoveSliderDelta = 200;

    public WorkoutExerciseSection(int workoutId, WorkoutExercise workoutExercise, WorkoutActivity workoutActivity) {
        LinearLayout headerLinearLayout;

        this.workoutExerciseId = workoutExercise.getId();
        this.workoutExerciseName = workoutExercise.getWorkoutExerciseName();
        if(this.workoutExerciseName.equals(WorkoutExerciseEnum.DEADLIFT.getExerciseName())) {
            this.workoutSetTypeId = WorkoutSetTypeEnum.OneXFive.getId();
        }
        else {
            this.workoutSetTypeId = workoutActivity.getDb().getLastWorkoutSetTypeIDForWorkoutExercise(workoutExercise);
        }
        this.weightType = workoutActivity.getDb().getCurrentWeightType();

        if(workoutActivity instanceof NewWorkoutActivity) {
            NewWorkoutActivity newWorkoutActivity = (NewWorkoutActivity) workoutActivity;
            final int numOfReps = WorkoutSetTypeEnum.getEnumByID(this.workoutSetTypeId).getReps();
            final int numOfSets = WorkoutSetTypeEnum.getEnumByID(this.workoutSetTypeId).getSets();

            this.parentLinearLayout = (LinearLayout)newWorkoutActivity.findViewById(R.id.workoutLinearLayout);
            this.exerciseRowLinearLayout = new LinearLayout(newWorkoutActivity);
            this.exerciseRowLinearLayout.setOrientation(LinearLayout.VERTICAL);

            headerLinearLayout = new LinearLayout(newWorkoutActivity);
            headerLinearLayout.addView(this.createHeaderNameText(newWorkoutActivity.getActivity()));

            headerLinearLayout.addView(this.createHeaderSetTypeText(newWorkoutActivity.getActivity(), newWorkoutActivity.getDb().getAllWorkoutSetTypeNames()));

            this.workoutExerciseWeight = newWorkoutActivity.getDb().getNextWeightForWorkoutExercise(workoutExercise);
            headerLinearLayout.addView(this.createHeaderWeightText(newWorkoutActivity.getActivity()));

            if(!this.isStandardExercise(newWorkoutActivity, this.workoutExerciseId)) {
                this.createSwipeToRemoveAction(newWorkoutActivity);
            }

            this.buttonTableLayout = new TableLayout(newWorkoutActivity);
            this.buttonRow = new TableRow(newWorkoutActivity);
            this.buttonRow.setGravity(Gravity.CENTER);
            for(int j=0; j<numOfSets; j++) {
                this.buttonRow.addView(this.createExerciseRowButton(newWorkoutActivity.getActivity(), numOfReps));
            }
        }
        else {
            UpdateWorkoutActivity updateWorkoutActivity = (UpdateWorkoutActivity) workoutActivity;
            final int numOfReps = WorkoutSetTypeEnum.getEnumByID(this.workoutSetTypeId).getReps();
            List<WorkoutSet> workoutSetList = updateWorkoutActivity.getDb().getAllWorkoutSetsByWorkoutIDAndWorkoutExerciseID(workoutId, this.workoutExerciseId);

            this.parentLinearLayout = (LinearLayout)updateWorkoutActivity.findViewById(R.id.workoutLinearLayout);
            this.exerciseRowLinearLayout = new LinearLayout(updateWorkoutActivity);
            this.exerciseRowLinearLayout.setOrientation(LinearLayout.VERTICAL);

            this.workoutExerciseWeight = updateWorkoutActivity.getDb().getWeightByWorkoutIdAndWorkoutExerciseId(workoutId, this.workoutExerciseId);

            headerLinearLayout = new LinearLayout(updateWorkoutActivity);
            headerLinearLayout.addView(this.createHeaderNameText(updateWorkoutActivity.getActivity()));
            headerLinearLayout.addView(this.createHeaderSetTypeText(updateWorkoutActivity.getActivity(), updateWorkoutActivity.getDb().getAllWorkoutSetTypeNames()));
            headerLinearLayout.addView(this.createHeaderWeightText(updateWorkoutActivity.getActivity()));

            this.buttonTableLayout = new TableLayout(updateWorkoutActivity);
            this.buttonRow = new TableRow(updateWorkoutActivity);
            this.buttonRow.setGravity(Gravity.CENTER);
            for(int i=0; i<workoutSetList.size(); i++) {
                Button btn = this.createExerciseRowButton(updateWorkoutActivity.getActivity(), numOfReps);
                if(workoutSetList.get(i).getReps() == 0) {
                    btn.setText("");
                }
                else {
                    btn.setText(Integer.toString(workoutSetList.get(i).getReps()));
                }
                this.buttonRow.addView(btn);
            }
        }

        this.exerciseRowLinearLayout.addView(headerLinearLayout);
        this.buttonTableLayout.addView(this.buttonRow);
        this.buttonTableLayout.setShrinkAllColumns(true);
        this.buttonTableLayout.setStretchAllColumns(true);
        this.exerciseRowLinearLayout.addView(this.buttonTableLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(25,25,25,25);
        this.exerciseRowLinearLayout.setLayoutParams(layoutParams);
        this.exerciseRowLinearLayout.setBackgroundResource(R.drawable.exercise_row_style);

        this.parentLinearLayout.addView(this.exerciseRowLinearLayout);
    }

    public List<Button> getExerciseRowButtons() {
        List<Button> buttons = new LinkedList<>();
        for (int i = 0; i < this.buttonRow.getChildCount(); i++) {
            buttons.add((Button)this.buttonRow.getChildAt(i));
        }
        return buttons;
    }

    public int getWorkoutExerciseId() {
        return this.workoutExerciseId;
    }

    public String getWorkoutExerciseName() {
        return this.workoutExerciseName;
    }

    public int getWorkoutExerciseWeight() {
        return this.workoutExerciseWeight;
    }

    public int getWorkoutSetTypeId() {
        return workoutSetTypeId;
    }

    private View createHeaderNameText(ActionBarActivity activity) {
        TextView headerText = new TextView(activity);
        headerText.setText(workoutExerciseName);
        headerText.setTextColor(Color.BLACK);
        headerText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        return headerText;
    }

    private View createHeaderSetTypeText(final ActionBarActivity activity, final List<String> workoutSetTypeNameList) {
        final TextView headerSetTypeText = new TextView(activity);
        headerSetTypeText.setText(WorkoutSetTypeEnum.getEnumByID(this.workoutSetTypeId).getName());
        headerSetTypeText.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,40,0);
        headerSetTypeText.setLayoutParams(layoutParams);

        if(activity instanceof NewWorkoutActivity) {
            headerSetTypeText.setPaintFlags(headerSetTypeText.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            headerSetTypeText.setOnClickListener(new TextView.OnClickListener() {
                public void onClick(View v) {
                    final CharSequence[] workoutSetTypeListArray = workoutSetTypeNameList.toArray(new CharSequence[workoutSetTypeNameList.size()]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Set type for this exercise?");
                    builder.setItems(workoutSetTypeListArray, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int item) {
                            buttonTableLayout.removeView(buttonRow);
                            WorkoutSetTypeEnum workoutSetTypeEnum = WorkoutSetTypeEnum.getEnumByName(((AlertDialog)dialogInterface).getListView().getAdapter().getItem(item).toString());
                            int numOfReps = workoutSetTypeEnum.getReps();
                            int numOfSets = workoutSetTypeEnum.getSets();
                            buttonRow = new TableRow(activity);
                            buttonRow.setGravity(Gravity.CENTER);
                            for(int j=0; j<numOfSets; j++) {
                                buttonRow.addView(createExerciseRowButton(activity, numOfReps));
                            }
                            buttonTableLayout.addView(buttonRow);
                            headerSetTypeText.setText(workoutSetTypeEnum.getName());
                            workoutSetTypeId = workoutSetTypeEnum.getId();
                        }
                    });
                    builder.create().show();
                }
            });
        }

        return headerSetTypeText;
    }

    private LinearLayout createHeaderPlateCalculator(final ActionBarActivity activity, Map<PlateEnum, Integer> plates) {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView headerPlateTextView = new TextView(activity);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        headerParams.setMargins(75,25,25,25);
        headerPlateTextView.setLayoutParams(headerParams);
        headerPlateTextView.setTextColor(Color.BLACK);
        headerPlateTextView.setText("Plates for each side:");
        linearLayout.addView(headerPlateTextView);

        Iterator entries = plates.entrySet().iterator();
        while (entries.hasNext()) {

            Map.Entry thisEntry = (Map.Entry) entries.next();
            Object key = thisEntry.getKey();
            Object value = thisEntry.getValue();

            headerPlateTextView = new TextView(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(125,25,25,25);
            headerPlateTextView.setLayoutParams(params);
            headerPlateTextView.setTextColor(Color.BLACK);
            headerPlateTextView.setText(PlateEnum.getEnumByName((PlateEnum) key).getDisplayName() + " x " + value.toString());
            linearLayout.addView(headerPlateTextView);
        }
        return linearLayout;
    }

    private View createHeaderWeightText(final ActionBarActivity activity) {
        final TextView headerText = new TextView(activity);
        headerText.setText(this.getWeightTypeDisplay() + this.weightType);
        headerText.setPaintFlags(headerText.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        headerText.setTextColor(Color.BLACK);
        headerText.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Weight for this exercise?");
                final LinearLayout headerWeightTextLinearLayout = new LinearLayout(activity);
                headerWeightTextLinearLayout.setOrientation(LinearLayout.VERTICAL);

                final EditText weightText = new EditText(activity);
                weightText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                weightText.setText(getWeightTypeDisplay());
                weightText.setSelection(weightText.getText().toString().length());
                weightText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            if(weightType.equals("LB")) {
                                if(!weightText.getText().toString().equals("") && (Integer.parseInt(weightText.getText().toString()) % 5 == 0)) {
                                    headerWeightTextLinearLayout.removeView(currentWeightCalcLayout);
                                    currentWeightCalcLayout = createHeaderPlateCalculator(activity, Util.calculatePlatesForLB(Double.parseDouble(weightText.getText().toString())));
                                    headerWeightTextLinearLayout.addView(currentWeightCalcLayout);
                                }
                            }
                            else {
                                if(!weightText.getText().toString().equals("") && (Double.parseDouble(weightText.getText().toString()) % 2.5 == 0)) {
                                    headerWeightTextLinearLayout.removeView(currentWeightCalcLayout);
                                    currentWeightCalcLayout = createHeaderPlateCalculator(activity, Util.calculatePlatesForKG(Double.parseDouble(weightText.getText().toString())));
                                    headerWeightTextLinearLayout.addView(currentWeightCalcLayout);
                                }
                            }
                        }
                        catch(Exception e) {
                            headerText.setText(getWeightTypeDisplay() + weightType);
                            Log.e("User input of weight is too large ", e.toString());
                        }
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            if(weightType.equals("LB")) {
                                workoutExerciseWeight = Integer.parseInt(weightText.getText().toString());
                                headerText.setText(weightText.getText().toString() + weightType);
                            }
                            else {
                                double pounds = Math.floor(Double.parseDouble(weightText.getText().toString()) * 2.25);
                                workoutExerciseWeight = (int) pounds;
                                headerText.setText(Double.parseDouble(weightText.getText().toString()) + weightType);
                            }
                        }
                        catch(Exception e) {
                            headerText.setText(getWeightTypeDisplay() + weightType);
                            Log.e("User input of weight is too large ", e.toString());
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                headerWeightTextLinearLayout.addView(weightText);

                if(weightType.equals("LB")) {
                    if(!weightText.getText().toString().equals("") && (Integer.parseInt(weightText.getText().toString()) % 5 == 0)) {
                        currentWeightCalcLayout = createHeaderPlateCalculator(activity, Util.calculatePlatesForLB(Double.parseDouble(weightText.getText().toString())));
                        headerWeightTextLinearLayout.addView(currentWeightCalcLayout);
                    }
                }
                else {
                    if(!weightText.getText().toString().equals("") && (Double.parseDouble(weightText.getText().toString()) % 2.5 == 0)) {
                        headerWeightTextLinearLayout.removeView(currentWeightCalcLayout);
                        currentWeightCalcLayout = createHeaderPlateCalculator(activity, Util.calculatePlatesForKG(Double.parseDouble(weightText.getText().toString())));
                        headerWeightTextLinearLayout.addView(currentWeightCalcLayout);
                    }
                }

                builder.setView(headerWeightTextLinearLayout);
                final AlertDialog dialog = builder.create();
                weightText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
                dialog.show();
            }
        });
        headerText.setGravity(Gravity.END);
        return headerText;
    }

    //If exercise is not part of the standard workout set, allow it to be removed by adding red remove option
    private boolean isStandardExercise(WorkoutActivity workoutActivity, int workoutExerciseId) {
        List<WorkoutExercise> workoutExerciseList = workoutActivity.getWorkoutRotationExerciseList();

        boolean isStandardExercise = false;
        for(int i=0; i<workoutExerciseList.size(); i++) {
            if(workoutExerciseList.get(i).getId() == workoutExerciseId) {
                isStandardExercise = true;
            }
        }

        return isStandardExercise;
    }

    private void createSwipeToRemoveAction(final WorkoutActivity workoutActivity) {
        this.exerciseRowLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        exerciseRemoveSliderX = event.getX();
                        return true;

                    case MotionEvent.ACTION_UP:
                        if ((event.getX() - exerciseRemoveSliderX < -exerciseRemoveSliderDelta) || (event.getX() - exerciseRemoveSliderX > exerciseRemoveSliderDelta))
                        {
                            parentLinearLayout.removeView(exerciseRowLinearLayout);
                            Iterator<WorkoutExerciseSection> i = workoutActivity.getDisplayedWorkoutExerciseSections().iterator();

                            while (i.hasNext()) {
                                WorkoutExerciseSection workoutExerciseSection = i.next();
                                if(workoutExerciseSection.getWorkoutExerciseId() == workoutExerciseId) {
                                    i.remove();
                                }
                            }
                            return true;
                        }
                    default: return false;
                }
            }
        });
    }

    private Button createExerciseRowButton(ActionBarActivity activity, final int numOfReps) {
        final Button btn = new Button(activity);
        btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (btn.getText() == null || btn.getText().toString().equals("") || btn.getText().toString().equals("0")) {
                    btn.setText(Integer.toString(numOfReps));
                } else {
                    if (btn.getText().toString().equals("1")) {
                        btn.setText("");
                    } else {
                        btn.setText(Integer.toString(Integer.parseInt(btn.getText().toString()) - 1));
                    }
                }
            }
        });
        return btn;
    }

    private String getWeightTypeDisplay() {
        if(this.weightType.equals("LB")) {
            return Integer.toString(this.workoutExerciseWeight);
        }
        else {
            double kgWeight = this.workoutExerciseWeight / 2.25;
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
