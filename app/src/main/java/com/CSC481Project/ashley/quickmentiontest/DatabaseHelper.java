package com.CSC481Project.ashley.quickmentiontest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ashley on 2/21/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "quick_mention_db";
    private static final int DB_VERSION = 16;

    /* table names */
    private static final String TABLE_TASK = "tasks";

    /* column names */
    private static final String START_DATE = "startDate";


    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /* Create task table */
        String createTableTask = "CREATE TABLE " + QMContract.TaskEntry.TABLE_NAME + " (" +
                QMContract.TaskEntry._ID1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QMContract.TaskEntry.KEY_NAME + " TEXT, " +
                QMContract.TaskEntry.KEY_START_DATE + " TEXT, " +
                QMContract.TaskEntry.KEY_START_TIME + " TEXT, " +
                QMContract.TaskEntry.KEY_END_DATE + " TEXT, " +
                QMContract.TaskEntry.KEY_END_TIME + " TEXT, " +
                QMContract.TaskEntry.KEY_REPEATS + " TEXT, " +
                QMContract.TaskEntry.KEY_NOTES + " TEXT, " +
                QMContract.TaskEntry.KEY_TIMESTAMP + " INTEGER)";
        db.execSQL(createTableTask);

        String createTableCategory = "CREATE TABLE " + QMContract.CategoryEntry.TABLE_NAME + " (" +
                QMContract.CategoryEntry._ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QMContract.CategoryEntry.KEY_NAME + " TEXT, " +
                QMContract.CategoryEntry.KEY_ICON + " INTEGER)";
        db.execSQL(createTableCategory);


        String createTableTemplate = "CREATE TABLE " + QMContract.TemplateEntry.TABLE_NAME + " (" +
                QMContract.TemplateEntry._ID3 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QMContract.TemplateEntry.KEY_NAME + " TEXT, " +
                QMContract.TemplateEntry.KEY_REPEATS + " TEXT, " +
                QMContract.TemplateEntry.KEY_TEMP_CAT + " INTEGER, " +
                "FOREIGN KEY (" + QMContract.TemplateEntry.KEY_TEMP_CAT + ") REFERENCES " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry._ID + "))";
        db.execSQL(createTableTemplate);

        /* Insert values into category table */
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ") VALUES ('Home', " + R.drawable.ic_home_black_24dp + ")");
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ") VALUES ('Auto', " + R.drawable.ic_directions_car_black_24dp + ")");
        db.execSQL("INSERT INTO " + QMContract.CategoryEntry.TABLE_NAME + "(" + QMContract.CategoryEntry.KEY_NAME + ", " + QMContract.CategoryEntry.KEY_ICON + ") VALUES ('Health', " + R.drawable.ic_local_hospital_black_24dp + ")");



        /* Insert values into template table */
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ") VALUES ('Do Laundry', 'Every Week', 1 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ") VALUES ('Cut Grass', 'Every 2 Weeks', 1 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ") VALUES ('Wash Car', 'Every Week', 2 )");
        db.execSQL("INSERT INTO " + QMContract.TemplateEntry.TABLE_NAME + "(" + QMContract.TemplateEntry.KEY_NAME + ", " +
                QMContract.TemplateEntry.KEY_REPEATS + ", " + QMContract.TemplateEntry.KEY_TEMP_CAT + ") VALUES ('Drink Water', 'Every Day', 3 )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + QMContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QMContract.CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QMContract.TemplateEntry.TABLE_NAME);
        onCreate(db);


    }



    /* Return tasks with a start_date that matches dateString
     * dateString must be formatted as MM/dd/yyyy
      * including leading zero on the month */
    Cursor getTasks(String dateString){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TASK + " WHERE " + START_DATE + " = '" +
                dateString + "'";
        return db.rawQuery(query, null);


    }

}