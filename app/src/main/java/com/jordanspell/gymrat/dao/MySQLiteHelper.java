package com.jordanspell.gymrat.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jordanspell.gymrat.biz.WorkoutExerciseEnum;
import com.jordanspell.gymrat.biz.WorkoutSetTypeEnum;
import com.jordanspell.gymrat.model.Workout;
import com.jordanspell.gymrat.model.WorkoutExercise;
import com.jordanspell.gymrat.model.WorkoutSet;
import com.jordanspell.gymrat.model.WorkoutSetType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jorda_000 on 2/13/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 139;
    private static final String DATABASE_NAME = "GYM_RAT_DB";

    private static final String TABLE_WORKOUT = "WORKOUT";
    private static final String WORKOUT_ID = "WORKOUT_ID";
    private static final String WORKOUT_DATE = "WORKOUT_DATE";
    private static final String WORKOUT_ROTATION = "WORKOUT_ROTATION";
    private static final String[] WORKOUT_COLUMNS = {WORKOUT_ID,WORKOUT_DATE,WORKOUT_ROTATION};

    private static final String TABLE_WORKOUT_EXERCISE = "WORKOUT_EXERCISE";
    private static final String WORKOUT_EXERCISE_ID = "WORKOUT_EXERCISE_ID";
    private static final String WORKOUT_EXERCISE_NAME = "WORKOUT_EXERCISE_NAME";
    private static final String[] WORKOUT_EXERCISE_COLUMNS = {WORKOUT_EXERCISE_ID,WORKOUT_EXERCISE_NAME,WORKOUT_ROTATION};

    private static final String TABLE_WORKOUT_SET_TYPE = "WORKOUT_SET_TYPE";
    private static final String WORKOUT_SET_TYPE_ID = "WORKOUT_SET_TYPE_ID";
    private static final String WORKOUT_SET_TYPE_NAME = "WORKOUT_SET_TYPE_NAME";
    private static final String[] WORKOUT_SET_TYPE_COLUMNS = {WORKOUT_SET_TYPE_ID,WORKOUT_SET_TYPE_NAME};

    private static final String TABLE_WORKOUT_SET = "WORKOUT_SET";
    private static final String WORKOUT_SET_ID = "WORKOUT_SET_ID";
    private static final String WEIGHT = "WEIGHT";
    private static final String REPS = "REPS";
    private static final String[] WORKOUT_SET_COLUMNS = {WORKOUT_SET_ID,WORKOUT_ID,WORKOUT_EXERCISE_ID,WORKOUT_SET_TYPE_ID,WEIGHT,REPS};

    private static final String TABLE_CONFIG = "CONFIG";
    private static final String PARM_NAME = "PARM_NAME";
    private static final String PARM_VALUE = "PARM_VALUE";
    private static final String[] CONFIG_COLUMNS = {PARM_NAME,PARM_VALUE};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE WORKOUT (" +
                "WORKOUT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "WORKOUT_DATE DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime')), "+
                "WORKOUT_ROTATION TEXT)");

        db.execSQL("CREATE TABLE WORKOUT_EXERCISE (" +
                "WORKOUT_EXERCISE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WORKOUT_EXERCISE_NAME TEXT, "+
                "WORKOUT_ROTATION TEXT," +
                "WORKOUT_PRIMARY INTEGER DEFAULT 0)");

        db.execSQL("INSERT INTO WORKOUT_EXERCISE VALUES (?, 'Squats','', 1)");
        db.execSQL("INSERT INTO WORKOUT_EXERCISE VALUES (?, 'Bench Press','A', 1)");
        db.execSQL("INSERT INTO WORKOUT_EXERCISE VALUES (?, 'Barbell Row','A', 1)");
        db.execSQL("INSERT INTO WORKOUT_EXERCISE VALUES (?, 'Overhead Press','B',1)");
        db.execSQL("INSERT INTO WORKOUT_EXERCISE VALUES (?, 'Deadlift','B', 1)");

        db.execSQL("CREATE TABLE WORKOUT_SET_TYPE (" +
                "WORKOUT_SET_TYPE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WORKOUT_SET_TYPE_NAME TEXT)");

        db.execSQL("INSERT INTO WORKOUT_SET_TYPE VALUES (?, '5x5')");
        db.execSQL("INSERT INTO WORKOUT_SET_TYPE VALUES (?, '3x8')");
        db.execSQL("INSERT INTO WORKOUT_SET_TYPE VALUES (?, '3x5')");
        db.execSQL("INSERT INTO WORKOUT_SET_TYPE VALUES (?, '3x3')");
        db.execSQL("INSERT INTO WORKOUT_SET_TYPE VALUES (?, '1x5')");
        db.execSQL("INSERT INTO WORKOUT_SET_TYPE VALUES (?, '1x3')");

        db.execSQL("CREATE TABLE WORKOUT_SET (" +
                "WORKOUT_SET_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "WORKOUT_ID INTEGER, "+
                "WORKOUT_EXERCISE_ID INTEGER, "+
                "WORKOUT_SET_TYPE_ID INTEGER, "+
                "WEIGHT INTEGER, "+
                "REPS INTEGER, "+
                "FOREIGN KEY(WORKOUT_ID) REFERENCES WORKOUT(WORKOUT_ID), "+
                "FOREIGN KEY(WORKOUT_EXERCISE_ID) REFERENCES WORKOUT_EXERCISE(WORKOUT_EXERCISE_ID), "+
                "FOREIGN KEY(WORKOUT_SET_TYPE_ID) REFERENCES WORKOUT_SET_TYPE(WORKOUT_SET_TYPE_ID))");

        db.execSQL("CREATE INDEX WORKOUT_EXERCISE_ID_IDX ON WORKOUT_SET(WORKOUT_EXERCISE_ID)");

        db.execSQL("CREATE TABLE CONFIG (" +
                "PARM_NAME TEXT, " +
                "PARM_VALUE TEXT)");

        db.execSQL("INSERT INTO CONFIG VALUES ('Rotation', 'A')");
        db.execSQL("INSERT INTO CONFIG VALUES ('WeightType', 'LB')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL("DROP TABLE IF EXISTS WORKOUT");
        db.execSQL("DROP TABLE IF EXISTS WORKOUT_EXERCISE");
        db.execSQL("DROP TABLE IF EXISTS WORKOUT_SET_TYPE");
        db.execSQL("DROP TABLE IF EXISTS WORKOUT_SET");
        db.execSQL("DROP TABLE IF EXISTS CONFIG");

        this.onCreate(db);*/
    }

    public long addWorkout(Workout workout) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WORKOUT_ROTATION, workout.getRotation());
        if(workout.getDate() != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            values.put(WORKOUT_DATE, simpleDateFormat.format(workout.getDate()));
        }
        long id = db.insert(TABLE_WORKOUT, null, values);

        db.close();

        return id;
    }

    public void addWorkoutExercise(WorkoutExercise workoutExercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WORKOUT_EXERCISE_NAME, workoutExercise.getWorkoutExerciseName());
        values.put(WORKOUT_ROTATION, workoutExercise.getRotation());

        db.insert(TABLE_WORKOUT_EXERCISE, null, values);

        db.close();
    }

    public void addWorkoutSetType(WorkoutSetType workoutSetType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WORKOUT_SET_TYPE_NAME, workoutSetType.getWorkoutSetTypeName());

        db.insert(TABLE_WORKOUT_SET_TYPE, null, values);

        db.close();
    }

    public void addWorkoutSet(WorkoutSet workoutSet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WORKOUT_ID, workoutSet.getWorkoutID());
        values.put(WORKOUT_EXERCISE_ID, workoutSet.getWorkoutExerciseID());
        values.put(WORKOUT_SET_TYPE_ID, workoutSet.getWorkoutSetTypeID());
        values.put(WEIGHT, workoutSet.getWeight());
        values.put(REPS, workoutSet.getReps());

        db.insert(TABLE_WORKOUT_SET, null, values);

        db.close();
    }

    public Workout getWorkout(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_WORKOUT,
                        WORKOUT_COLUMNS,
                        " WORKOUT_ID = ?",
                        new String[] { String.valueOf(id) },
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        Workout workout = new Workout();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            workout.setId(Integer.parseInt(cursor.getString(0)));
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                workout.setDate(sdf.parse(cursor.getString(1)));
            }
            catch(Exception e) {
                Log.e("Failed to parse date while getting workout for id " + id, e.toString());
            }
            workout.setRotation(cursor.getString(2));
            cursor.close();
        }

        return workout;
    }

    public Workout getWorkoutByDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String queryDate = "";
        try {
            queryDate = sdf.format(date);
        }
        catch(Exception e) {
            Log.e("Failed to parse date while getting workout by date " + date.toString(), e.toString());
        }

        String query = "SELECT  * FROM " + TABLE_WORKOUT + " WHERE strftime('%Y-%m-%d', WORKOUT_DATE) = '" + queryDate + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Workout workout = new Workout();

        if (cursor.moveToFirst()) {
            workout.setId(Integer.parseInt(cursor.getString(0)));
            try {
                workout.setDate(sdf.parse(cursor.getString(1)));
            }
            catch(Exception e) {
                Log.e("Failed to parse date while getting workout for date " + date.toString(), e.toString());
            }
            workout.setRotation(cursor.getString(2));
            cursor.close();
        }

        return workout;
    }

    public WorkoutExercise getWorkoutExercise(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_WORKOUT_EXERCISE,
                        WORKOUT_EXERCISE_COLUMNS,
                        " WORKOUT_EXERCISE_ID = ?",
                        new String[] { String.valueOf(id) },
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        WorkoutExercise workoutExercise = new WorkoutExercise();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            workoutExercise.setId(Integer.parseInt(cursor.getString(0)));
            workoutExercise.setWorkoutExerciseName(cursor.getString(1));
            workoutExercise.setRotation(cursor.getString(2));
            workoutExercise.setPrimary(Integer.parseInt(cursor.getString(3)));
            cursor.close();
        }

        return workoutExercise;
    }

    public WorkoutExercise getWorkoutExerciseByName(String workoutExerciseName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_WORKOUT_EXERCISE,
                        WORKOUT_EXERCISE_COLUMNS,
                        " lower(WORKOUT_EXERCISE_NAME) = lower(?)",
                        new String[] { workoutExerciseName },
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        WorkoutExercise workoutExercise = new WorkoutExercise();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            workoutExercise.setId(Integer.parseInt(cursor.getString(0)));
            workoutExercise.setWorkoutExerciseName(cursor.getString(1));
            workoutExercise.setRotation(cursor.getString(2));
            cursor.close();
        }

        return workoutExercise;
    }

    public WorkoutSetType getWorkoutSetType(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_WORKOUT_SET_TYPE,
                        WORKOUT_SET_TYPE_COLUMNS,
                        " WORKOUT_SET_TYPE_ID = ?",
                        new String[] { String.valueOf(id) },
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        WorkoutSetType workoutSetType = new WorkoutSetType();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            workoutSetType.setId(Integer.parseInt(cursor.getString(0)));
            workoutSetType.setWorkoutSetTypeName(cursor.getString(1));
            cursor.close();
        }

        return workoutSetType;
    }

    public WorkoutSet getWorkoutSet(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_WORKOUT_SET,
                        WORKOUT_SET_COLUMNS,
                        " WORKOUT_SET_ID = ?",
                        new String[] { String.valueOf(id) },
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        WorkoutSet workoutSet = new WorkoutSet();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            workoutSet.setId(Integer.parseInt(cursor.getString(0)));
            workoutSet.setWorkoutID(Integer.parseInt(cursor.getString(1)));
            workoutSet.setWorkoutExerciseID(Integer.parseInt(cursor.getString(2)));
            workoutSet.setWorkoutSetTypeID(Integer.parseInt(cursor.getString(3)));
            workoutSet.setWeight(Integer.parseInt(cursor.getString(4)));
            workoutSet.setReps(Integer.parseInt(cursor.getString(5)));
            cursor.close();
        }

        return workoutSet;
    }

    public List<Workout> getAllWorkouts() {
        List<Workout> workoutList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_WORKOUT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Workout workout;
        if (cursor.moveToFirst()) {
            do {
                workout = new Workout();
                workout.setId(Integer.parseInt(cursor.getString(0)));
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    workout.setDate(sdf.parse(cursor.getString(1)));
                }
                catch(Exception e) {
                    Log.e("Failed to parse date while getting all workouts. id " + cursor.getString(0), e.toString());
                }
                workout.setRotation(cursor.getString(2));

                workoutList.add(workout);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutList;
    }

    public List<WorkoutExercise> getAllWorkoutExercises() {
        List<WorkoutExercise> workoutExerciseList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_WORKOUT_EXERCISE + " ORDER BY WORKOUT_ROTATION ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        WorkoutExercise workoutExercise;
        if (cursor.moveToFirst()) {
            do {
                workoutExercise = new WorkoutExercise();
                workoutExercise.setId(Integer.parseInt(cursor.getString(0)));
                workoutExercise.setWorkoutExerciseName(cursor.getString(1));
                workoutExercise.setRotation(cursor.getString(2));
                workoutExercise.setPrimary(Integer.parseInt(cursor.getString(3)));
                workoutExerciseList.add(workoutExercise);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutExerciseList;
    }

    public List<WorkoutExercise> getAllPrimaryWorkoutExercises() {
        List<WorkoutExercise> workoutExerciseList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_WORKOUT_EXERCISE + " WHERE WORKOUT_ROTATION IN ('','A','B') AND WORKOUT_PRIMARY = 1 ORDER BY WORKOUT_ROTATION ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        WorkoutExercise workoutExercise;
        if (cursor.moveToFirst()) {
            do {
                workoutExercise = new WorkoutExercise();
                workoutExercise.setId(Integer.parseInt(cursor.getString(0)));
                workoutExercise.setWorkoutExerciseName(cursor.getString(1));
                workoutExercise.setRotation(cursor.getString(2));
                workoutExercise.setPrimary(Integer.parseInt(cursor.getString(3)));

                workoutExerciseList.add(workoutExercise);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutExerciseList;
    }

    public List<WorkoutExercise> getAllWorkoutExercisesByRotation(String rotation) {
        List<WorkoutExercise> workoutExerciseList = new LinkedList<>();
        List<WorkoutExercise> allWorkoutExerciseList = this.getAllWorkoutExercises();

        for(int i=0; i<allWorkoutExerciseList.size(); i++) {
            if(allWorkoutExerciseList.get(i).getRotation().equals(rotation) || (allWorkoutExerciseList.get(i).getRotation().equals("") && !rotation.equals("C"))) {
                workoutExerciseList.add(allWorkoutExerciseList.get(i));
            }
        }

        return workoutExerciseList;
    }

    public List<WorkoutExercise> getAllWorkoutExercisesByWorkoutID(int workoutID) {
        String query = "SELECT * FROM " + TABLE_WORKOUT_EXERCISE + " WHERE WORKOUT_EXERCISE_ID IN (SELECT DISTINCT(WORKOUT_EXERCISE_ID) FROM " + TABLE_WORKOUT_SET + " WHERE WORKOUT_ID = " + workoutID + ")";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        List<WorkoutExercise> workoutExerciseList = new LinkedList<>();

        WorkoutExercise workoutExercise;
        if (cursor.moveToFirst()) {
            do {
                workoutExercise = new WorkoutExercise();
                workoutExercise.setId(Integer.parseInt(cursor.getString(0)));
                workoutExercise.setWorkoutExerciseName(cursor.getString(1));
                workoutExercise.setRotation(cursor.getString(2));
                workoutExercise.setPrimary(Integer.parseInt(cursor.getString(3)));
                workoutExerciseList.add(workoutExercise);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutExerciseList;
    }

    public List<WorkoutExercise> getAllStandardWorkoutExercisesByWorkoutID(int workoutID) {
        String query = "SELECT * FROM " + TABLE_WORKOUT_EXERCISE + " WHERE WORKOUT_EXERCISE_ID IN (SELECT DISTINCT(WORKOUT_EXERCISE_ID) FROM " + TABLE_WORKOUT_SET + " WHERE WORKOUT_ID = " + workoutID + ") " +
                        "AND WORKOUT_ROTATION IN ('','A', 'B')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        List<WorkoutExercise> workoutExerciseList = new LinkedList<>();

        WorkoutExercise workoutExercise;
        if (cursor.moveToFirst()) {
            do {
                workoutExercise = new WorkoutExercise();
                workoutExercise.setId(Integer.parseInt(cursor.getString(0)));
                workoutExercise.setWorkoutExerciseName(cursor.getString(1));
                workoutExercise.setRotation(cursor.getString(2));
                workoutExercise.setPrimary(Integer.parseInt(cursor.getString(3)));
                workoutExerciseList.add(workoutExercise);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutExerciseList;
    }

    public List<WorkoutSetType> getAllWorkoutSetTypes() {
        List<WorkoutSetType> workoutSetTypeList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_WORKOUT_SET_TYPE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        WorkoutSetType workoutSetType;
        if (cursor.moveToFirst()) {
            do {
                workoutSetType = new WorkoutSetType();
                workoutSetType.setId(Integer.parseInt(cursor.getString(0)));
                workoutSetType.setWorkoutSetTypeName(cursor.getString(1));

                workoutSetTypeList.add(workoutSetType);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutSetTypeList;
    }

    public List<String> getAllWorkoutSetTypeNames() {
        List<String> workoutSetTypeNameList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_WORKOUT_SET_TYPE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                workoutSetTypeNameList.add(cursor.getString(1));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutSetTypeNameList;
    }

    public List<WorkoutExercise> getAllStandardExercisesAlreadyPerformed(){
        List<WorkoutExercise> workoutExerciseList = new LinkedList<>();

        String query = "SELECT DISTINCT(WORKOUT_EXERCISE_NAME)FROM " +  TABLE_WORKOUT_EXERCISE + " WE " +
                " JOIN " +
                 TABLE_WORKOUT_SET + " WS " +
                "ON WS.WORKOUT_EXERCISE_ID = WE.WORKOUT_EXERCISE_ID WHERE WORKOUT_ROTATION IN ('','A','B')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        WorkoutExercise workoutExercise;
        if(cursor.moveToFirst()){
            do {
               workoutExercise = new WorkoutExercise();
               workoutExercise.setWorkoutExerciseName(cursor.getString(0));
               workoutExerciseList.add(workoutExercise);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return workoutExerciseList;
    }

    public List<WorkoutSet> getAllWorkoutSets() {
        List<WorkoutSet> workoutSetList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_WORKOUT_SET;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        WorkoutSet workoutSet;
        if (cursor.moveToFirst()) {
            do {
                workoutSet = new WorkoutSet();
                workoutSet.setId(Integer.parseInt(cursor.getString(0)));
                workoutSet.setWorkoutID(Integer.parseInt(cursor.getString(1)));
                workoutSet.setWorkoutExerciseID(Integer.parseInt(cursor.getString(2)));
                workoutSet.setWorkoutSetTypeID(Integer.parseInt(cursor.getString(3)));
                workoutSet.setWeight(Integer.parseInt(cursor.getString(4)));
                workoutSet.setReps(Integer.parseInt(cursor.getString(5)));

                workoutSetList.add(workoutSet);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutSetList;
    }

    public List<WorkoutSet> getAllWorkoutSetsByWorkoutIDAndWorkoutExerciseID(int workoutID, int workoutExerciseID) {
        List<WorkoutSet> workoutSetList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_WORKOUT_SET + " WHERE WORKOUT_ID = " + workoutID + " AND WORKOUT_EXERCISE_ID = " + workoutExerciseID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        WorkoutSet workoutSet;
        if (cursor.moveToFirst()) {
            do {
                workoutSet = new WorkoutSet();
                workoutSet.setId(Integer.parseInt(cursor.getString(0)));
                workoutSet.setWorkoutID(Integer.parseInt(cursor.getString(1)));
                workoutSet.setWorkoutExerciseID(Integer.parseInt(cursor.getString(2)));
                workoutSet.setWorkoutSetTypeID(Integer.parseInt(cursor.getString(3)));
                workoutSet.setWeight(Integer.parseInt(cursor.getString(4)));
                workoutSet.setReps(Integer.parseInt(cursor.getString(5)));

                workoutSetList.add(workoutSet);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutSetList;
    }

    public List<WorkoutSet> getAllWorkoutSetsByWorkoutID(int workoutID) {
        List<WorkoutSet> workoutSetList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_WORKOUT_SET + " WHERE WORKOUT_ID = " + workoutID + " ORDER BY WORKOUT_EXERCISE_ID ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        WorkoutSet workoutSet;
        if (cursor.moveToFirst()) {
            do {
                workoutSet = new WorkoutSet();
                workoutSet.setId(Integer.parseInt(cursor.getString(0)));
                workoutSet.setWorkoutID(Integer.parseInt(cursor.getString(1)));
                workoutSet.setWorkoutExerciseID(Integer.parseInt(cursor.getString(2)));
                workoutSet.setWorkoutSetTypeID(Integer.parseInt(cursor.getString(3)));
                workoutSet.setWeight(Integer.parseInt(cursor.getString(4)));
                workoutSet.setReps(Integer.parseInt(cursor.getString(5)));

                workoutSetList.add(workoutSet);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return workoutSetList;
    }

    public int updateWorkout(Workout workout) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WORKOUT_ROTATION, workout.getRotation());

        int i = db.update(TABLE_WORKOUT,
                values,
                WORKOUT_ID+" = ?",
                new String[] { String.valueOf(workout.getId()) });

        db.close();

        return i;
    }

    public int updateWorkoutExercise(WorkoutExercise workoutExercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WORKOUT_EXERCISE_NAME, workoutExercise.getWorkoutExerciseName());

        int i = db.update(TABLE_WORKOUT_EXERCISE,
                values,
                WORKOUT_EXERCISE_ID+" = ?",
                new String[] { String.valueOf(workoutExercise.getId()) });

        db.close();

        return i;
    }

    public int updateWorkoutSetType(WorkoutSetType workoutSetType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WORKOUT_SET_TYPE_NAME, workoutSetType.getWorkoutSetTypeName());

        int i = db.update(TABLE_WORKOUT_SET_TYPE,
                values,
                WORKOUT_SET_TYPE_ID+" = ?",
                new String[] { String.valueOf(workoutSetType.getId()) });

        db.close();

        return i;
    }

    public int updateWorkoutSet(WorkoutSet workoutSet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WEIGHT, workoutSet.getWeight());
        values.put(REPS, workoutSet.getReps());

        int i = db.update(TABLE_WORKOUT_SET,
                values,
                WORKOUT_SET_ID+" = ?",
                new String[] { String.valueOf(workoutSet.getId()) });

        db.close();

        return i;
    }

    public int updateConfigValue(String parmName, String parmValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PARM_VALUE, parmValue);

        int i = db.update(TABLE_CONFIG,
                values,
                PARM_NAME+" = ?",
                new String[] { parmName });

        db.close();

        return i;
    }

    public void deleteWorkout(Workout workout) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_WORKOUT,
                WORKOUT_ID+" = ?",
                new String[] { String.valueOf(workout.getId()) });

        db.close();
    }

    public void deleteWorkoutExercise(WorkoutExercise workoutExercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_WORKOUT_EXERCISE,
                WORKOUT_EXERCISE_ID+" = ?",
                new String[] { String.valueOf(workoutExercise.getId()) });

        db.close();
    }

    public void deleteWorkoutExerciseByName(WorkoutExercise workoutExercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_WORKOUT_EXERCISE,
                WORKOUT_EXERCISE_NAME+" = ?",
                new String[] { String.valueOf(workoutExercise.getWorkoutExerciseName()) });

        db.close();
    }

    public void deleteWorkoutSetType(WorkoutSetType workoutSetType) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_WORKOUT_SET_TYPE,
                WORKOUT_SET_TYPE_ID+" = ?",
                new String[] { String.valueOf(workoutSetType.getId()) });

        db.close();
    }

    public void deleteWorkoutSet(WorkoutSet workoutSet) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_WORKOUT_SET,
                WORKOUT_SET_ID+" = ?",
                new String[] { String.valueOf(workoutSet.getId()) });

        db.close();
    }

    public void deleteWorkoutSetByWorkoutExerciseID(WorkoutExercise workoutExercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_WORKOUT_SET,
                WORKOUT_EXERCISE_ID+" = ?",
                new String[] { String.valueOf(workoutExercise.getId()) });

        db.close();
    }

    public String getCurrentWorkoutRotation() {
        String query = "SELECT PARM_VALUE FROM CONFIG WHERE PARM_NAME = 'Rotation'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String currentWorkoutRotation = "A";

        if (cursor.moveToFirst()) {
            currentWorkoutRotation = cursor.getString(0);
            cursor.close();
        }

        return currentWorkoutRotation;
    }

    public String getNextWorkoutRotation() {
        String query = "SELECT PARM_VALUE FROM CONFIG WHERE PARM_NAME = 'Rotation'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String nextWorkoutRotation = "A";

        if (cursor.moveToFirst()) {
            nextWorkoutRotation = cursor.getString(0);
            cursor.close();
        }

        if(nextWorkoutRotation.equals("A")) {
            nextWorkoutRotation = "B";
        }
        else {
            nextWorkoutRotation = "A";
        }

        return nextWorkoutRotation;
    }

    public String getCurrentWeightType() {
        String query = "SELECT PARM_VALUE FROM CONFIG WHERE PARM_NAME = 'WeightType'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String currentWeightType = "LB";

        if (cursor.moveToFirst()) {
            currentWeightType = cursor.getString(0);
            cursor.close();
        }

        return currentWeightType;
    }

    public String getNextWeightType() {
        String query = "SELECT PARM_VALUE FROM CONFIG WHERE PARM_NAME = 'WeightType'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String nextWeightType = "LB";

        if (cursor.moveToFirst()) {
            nextWeightType = cursor.getString(0);
            cursor.close();
        }

        if(nextWeightType.equals("LB")) {
            nextWeightType = "KG";
        }
        else {
            nextWeightType = "LB";
        }

        return nextWeightType;
    }

    public int getLastWorkoutSetTypeIDForWorkoutExercise(WorkoutExercise workoutExercise) {
        String query = "SELECT DISTINCT(WORKOUT_SET_TYPE_ID) FROM " + TABLE_WORKOUT_SET + " WHERE WORKOUT_ID = (SELECT MAX(WORKOUT_ID) FROM " + TABLE_WORKOUT_SET +
                " WHERE WORKOUT_EXERCISE_ID = " + workoutExercise.getId() + ") AND WORKOUT_EXERCISE_ID = " + workoutExercise.getId();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int currentSetType = 1;

        if (cursor.moveToFirst()) {
            currentSetType = Integer.parseInt(cursor.getString(0));
            cursor.close();
        }

        return currentSetType;
    }

    public int getWeightByWorkoutIdAndWorkoutExerciseId(int workoutId, int workoutExerciseId) {
        String query = "SELECT DISTINCT(WEIGHT) FROM " + TABLE_WORKOUT_SET + " WHERE WORKOUT_ID = " + workoutId + " AND WORKOUT_EXERCISE_ID = " + workoutExerciseId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int currentWeight = 45;

        if (cursor.moveToFirst()) {
            currentWeight = Integer.parseInt(cursor.getString(0));
            cursor.close();
        }

        return currentWeight;
    }

    public int getNextWeightForWorkoutExercise(WorkoutExercise exercise) {
        String query = "SELECT * FROM " + TABLE_WORKOUT_SET + " WHERE WORKOUT_ID = (SELECT MAX(WORKOUT_ID) FROM " + TABLE_WORKOUT_SET + " WHERE WORKOUT_EXERCISE_ID = " + exercise.getId() +
                ") AND WORKOUT_EXERCISE_ID = " + exercise.getId();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int currentWeight = 45;
        boolean movingUp = false;

        if (cursor != null && cursor.getCount() == WorkoutSetTypeEnum.getEnumByID(this.getLastWorkoutSetTypeIDForWorkoutExercise(exercise)).getSets()) {
            if (cursor.moveToFirst()) {
                currentWeight = Integer.parseInt(cursor.getString(4));
                do {
                    if(Integer.parseInt(cursor.getString(5)) == WorkoutSetTypeEnum.getEnumByID(this.getLastWorkoutSetTypeIDForWorkoutExercise(exercise)).getReps()) {
                        movingUp = true;
                    }
                    else {
                        movingUp = false;
                        break;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        if(movingUp) {
            if(exercise.getWorkoutExerciseName().equalsIgnoreCase(WorkoutExerciseEnum.DEADLIFT.getName())) {
                return currentWeight + 10;
            }
            else {
                return currentWeight + 5;
            }
        }
        else {
            return currentWeight;
        }
    }
}

