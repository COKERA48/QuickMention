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
    private static final int DB_VERSION = 13;

    /* table names */
    private static final String TABLE_TASK = "tasks";
    private static final String TABLE_CATEGORY = "categories";
    private static final String TABLE_TEMPLATE = "templates";

    /* column names */
    private static final String ID = "ID";
    private static final String NAME = "name";
    private static final String DATE = "date";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String REPEATS = "repeats";
    private static final String NOTES = "notes";
    private static final String TEMPLATE_CAT = "templateCategory";


    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* Create task table */
        String createTableTask = "CREATE TABLE " + TABLE_TASK + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT, " +
                DATE + " TEXT, " +
                START_TIME + " TEXT, " +
                END_TIME + " TEXT, " +
                REPEATS + " TEXT, " +
                NOTES + " TEXT)";
        db.execSQL(createTableTask);

        /* Create category table */
        String createTableCategory = "CREATE TABLE " + TABLE_CATEGORY + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT)";
        db.execSQL(createTableCategory);

        String createTableTemplate = "CREATE TABLE " + TABLE_TEMPLATE + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT, " +
                REPEATS + " TEXT, " +
                TEMPLATE_CAT + " INTEGER, " +
                "FOREIGN KEY (" + TEMPLATE_CAT + ") REFERENCES " + TABLE_CATEGORY + "(" + ID + "))";
        db.execSQL(createTableTemplate);

        /* Insert values into category table */
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + "(" + NAME + ") VALUES ('Home')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + "(" + NAME + ") VALUES ('Auto')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + "(" + NAME + ") VALUES ('Health')");



        /* Insert values into template table */
        db.execSQL("INSERT INTO " + TABLE_TEMPLATE + "(" + NAME + ", " + REPEATS + ", " + TEMPLATE_CAT + ") VALUES ('Do Laundry', 'Every Week', 1 )");
        db.execSQL("INSERT INTO " + TABLE_TEMPLATE + "(" + NAME + ", " + REPEATS + ", " + TEMPLATE_CAT + ") VALUES ('Mow Lawn', 'Every 2 Weeks', 1 )");
        db.execSQL("INSERT INTO " + TABLE_TEMPLATE + "(" + NAME + ", " + REPEATS + ", " + TEMPLATE_CAT + ") VALUES ('Wash Car', 'Every Week', 2 )");
        db.execSQL("INSERT INTO " + TABLE_TEMPLATE + "(" + NAME + ", " + REPEATS + ", " + TEMPLATE_CAT + ") VALUES ('Drink Water', 'Every Day', 3 )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATE);
        onCreate(db);

    }

    /* Inserts values into task table */
    boolean addTask(String name, String date, String startTime, String endTime, String repeats, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(DATE, date);
        contentValues.put(START_TIME, startTime);
        contentValues.put(END_TIME, endTime);
        contentValues.put(REPEATS, repeats);
        contentValues.put(NOTES, notes);

        Log.d(TAG, "addTask: Adding " + name + " to " + TABLE_TASK);

        /* Returns -1 if data did not get inserted correctly */
        long result = db.insert(TABLE_TASK, null, contentValues);
        return (result != -1);
    }

    /* Returns task cursor */
    Cursor getTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TASK;
        return db.rawQuery(query, null);

    }

    /* Returns category cursor */
    Cursor getCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CATEGORY;
        return db.rawQuery(query, null);
    }

    /* Returns all templates for specified category id */
    Cursor getTemplates(int catID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TEMPLATE + " WHERE " + TEMPLATE_CAT + " = " + catID;
        return db.rawQuery(query, null);
    }

    /* Returns category id matching category name */
    Cursor getCategoryID(String catName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID + " FROM " + TABLE_CATEGORY + " WHERE " + NAME + " = '" + catName + "'";
        return db.rawQuery(query, null);
    }

    /* Returns template repeat value matching template name */
    Cursor getTemplateRepeatValue(String tempName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + REPEATS + " FROM " + TABLE_TEMPLATE + " WHERE " + NAME + " = '" + tempName + "'";
        return db.rawQuery(query, null);
    }
}