package com.jordanspell.gymrat.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.jordanspell.gymrat.R;
import com.jordanspell.gymrat.biz.WorkoutExerciseEnum;
import com.jordanspell.gymrat.dao.MySQLiteHelper;
import com.jordanspell.gymrat.graphs.LineGraphFragment;
import com.jordanspell.gymrat.model.WorkoutExercise;
import com.jordanspell.gymrat.util.Util;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elfrank on 2/23/15.
 */
public class ProgressionActivity extends ActionBarActivity implements OnSeekBarChangeListener {

    PageIndicator mIndicator;
    private MySQLiteHelper db;
    private int numOfFragments;
    List<WorkoutExercise> listOfExercisesPerformed;
    Fragment[] arrayOfGraphFragments;
    final int allDataGraphPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progression);
        getWindow().setWindowAnimations(0);

        db = new MySQLiteHelper(this);
        getNumOfFragmentsNeeded();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        PageAdapter a = new PageAdapter(getSupportFragmentManager());
        pager.setAdapter(a);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_progression, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    private void getNumOfFragmentsNeeded(){

        listOfExercisesPerformed = db.getAllStandardExercisesAlreadyPerformed();
        arrayOfGraphFragments = new Fragment[WorkoutExerciseEnum.values().length + 1];

        ArrayList<String> exercisePerformedNames = new ArrayList<>();

        for(WorkoutExercise exercisesPerformed : listOfExercisesPerformed){
            exercisePerformedNames.add(exercisesPerformed.getWorkoutExerciseName());
        }
        int pos = 0;
        Bundle bundle = new Bundle();
        Fragment f = new LineGraphFragment();
        f.setArguments(bundle);
        arrayOfGraphFragments[allDataGraphPosition] = f;
        bundle.putInt("pos", pos);

        int index =1;
        for(WorkoutExerciseEnum workoutExerciseEnum : WorkoutExerciseEnum.values()){
            pos++;

            if(exercisePerformedNames.contains(workoutExerciseEnum.getExerciseName())){
                bundle = new Bundle();
                bundle.putInt("pos", pos);
                f = new LineGraphFragment();
                f.setArguments(bundle);
                arrayOfGraphFragments[index++] = f;
            }
        }
        numOfFragments = listOfExercisesPerformed.size() + 1;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            return arrayOfGraphFragments[pos];

        }

        @Override
        public int getCount() {
            return numOfFragments ;
        }
    }

    public void exportData(MenuItem item) {
        Util.exportData(this);
    }
}
