package com.CSC481Project.ashley.quickmentiontest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ashley on 2/21/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "quick_mention_db";
<<<<<<< HEAD
    private static final int DB_VERSION = 28;
=======
    private static final int DB_VERSION = 24;
>>>>>>> parent of 7f3531c... working on UI



    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /* Create task table */
        String createTableTask = "CREATE TABLE " + QMContract.TaskEntry.TABLE_NAME + " (" +
                QMContract.TaskEntry._ID1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QMContract.TaskEntry.KEY_NAME + " TEXT, " +
                QMContract.TaskEntry.KEY_DATE + " TEXT, " +
                QMContract.TaskEntry.KEY_TIME + " TEXT, " +
                QMContract.TaskEntry.KEY_REPEATS + " TEXT, " +
                QMContract.TaskEntry.KEY_NOTES + " TEXT, " +
                QMContract.TaskEntry.KEY_ALARM_ID + " INTEGER, " +
                QMContract.TaskEntry.KEY_TIMESTAMP + " INTEGER)";
        db.execSQL(createTableTask);

        String createTableCategory = "CREATE TABLE " + QMContract.CategoryEntry.TABLE_NAME + " (" +
                QMContract.CategoryEntry._ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QMContract.CategoryEntry.KEY_NAME + " TEXT, " +
                QMContract.CategoryEntry.KEY_ICON + " INTEGER, " +
                QMContract.CategoryEntry.KEY_USAGE + " INTEGER)";
        db.execSQL(createTableCategory);


        String createTableTemplate = "CREATE TABLE " + QMContract.TemplateEntry.TABLE_NAME + " (" +
                QMContract.TemplateEntry._ID3 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QMContract.TemplateEntry.KEY_NAME + " TEXT, " +
                QMContract.TemplateEntry.KEY_REPEATS + " TEXT, " +
                QMContract.TemplateEntry.KEY_TEMP_CAT + " INTEGER, " +
                QMContract.TemplateEntry.KEY_CREATED_BY_USER + " INTEGER, " +
                QMContract.TemplateEntry.KEY_USAGE + " INTEGER, " +
                "FOREIGN KEY (" + QMContract.TemplateEntry.KEY_TEMP_CAT + ") REFERENCES " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry._ID + "))";
        db.execSQL(createTableTemplate);

        /* Insert values into category table */
<<<<<<< HEAD
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ", " + QMContract.CategoryEntry.KEY_USAGE + ") VALUES ('Home', " + R.drawable.ic_home + ", 0)");
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ", " + QMContract.CategoryEntry.KEY_USAGE + ") VALUES ('Auto', " + R.drawable.ic_auto + ", 0)");
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ", " + QMContract.CategoryEntry.KEY_USAGE + ") VALUES ('Medical', " + R.drawable.ic_health + ", 0)");
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ", " + QMContract.CategoryEntry.KEY_USAGE + ") VALUES ('Work', " + R.drawable.ic_work + ", 0)");
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ", " + QMContract.CategoryEntry.KEY_USAGE + ") VALUES ('School', " + R.drawable.ic_school + ", 0)");
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ", " + QMContract.CategoryEntry.KEY_USAGE + ") VALUES ('Fitness', " + R.drawable.ic_fitness + ", 0)");
=======
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ", " + QMContract.CategoryEntry.KEY_USAGE + ") VALUES ('Home', " + R.drawable.ic_home_black_24dp + ", 0)");
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ", " + QMContract.CategoryEntry.KEY_USAGE + ") VALUES ('Auto', " + R.drawable.ic_directions_car_black_24dp + ", 0)");
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ", " + QMContract.CategoryEntry.KEY_USAGE + ") VALUES ('Health', " + R.drawable.ic_local_hospital_black_24dp + ", 0)");

>>>>>>> parent of 7f3531c... working on UI


        /* Insert values into template table */
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ", " + QMContract.TemplateEntry.KEY_CREATED_BY_USER + ", " + QMContract.TemplateEntry.KEY_USAGE + ") VALUES ('Do Laundry', 'Every Week', 1, 0, 0 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ", " + QMContract.TemplateEntry.KEY_CREATED_BY_USER + ", " + QMContract.TemplateEntry.KEY_USAGE + ") VALUES ('Mow Lawn', 'Every 2 Weeks', 1, 0, 0 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ", " + QMContract.TemplateEntry.KEY_CREATED_BY_USER + ", " + QMContract.TemplateEntry.KEY_USAGE + ") VALUES ('Clean Gutters', 'Every Year', 1, 0, 0 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ", " + QMContract.TemplateEntry.KEY_CREATED_BY_USER + ", " + QMContract.TemplateEntry.KEY_USAGE + ") VALUES ('Wash Car', 'Every Week', 2, 0, 0 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ", " + QMContract.TemplateEntry.KEY_CREATED_BY_USER + ", " + QMContract.TemplateEntry.KEY_USAGE + ") VALUES ('Drink Water', 'Every Day', 3, 0, 0 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ", " + QMContract.TemplateEntry.KEY_CREATED_BY_USER + ", " + QMContract.TemplateEntry.KEY_USAGE + ") VALUES ('Do Homework', 'Every Day', 5, 0, 0 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ", " + QMContract.TemplateEntry.KEY_CREATED_BY_USER + ", " + QMContract.TemplateEntry.KEY_USAGE + ") VALUES ('Finish Reports', 'Every Week', 4, 0, 0 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ", " + QMContract.TemplateEntry.KEY_CREATED_BY_USER + ", " + QMContract.TemplateEntry.KEY_USAGE + ") VALUES ('Go Running', 'Every Day', 6, 0, 0 )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + QMContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QMContract.CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QMContract.TemplateEntry.TABLE_NAME);
        onCreate(db);


    }




}