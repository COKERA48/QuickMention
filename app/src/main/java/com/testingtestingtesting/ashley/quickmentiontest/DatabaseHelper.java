package com.testingtestingtesting.ashley.quickmentiontest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ashley on 1/21/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "QuickMentionDB.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE USERS ( " +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "EMAIL TEXT NOT NULL, " +
                "NAME TEXT )");
        db.execSQL("CREATE TABLE TASKS ( " +
                "ID         INT PRIMARY KEY AUTOINCREMENT   NOT NULL," +
                "TITLE      TEXT        NOT NULL, " +
                "ALLDAY     BOOLEAN     NOT NULL, " +
                "STARTS     DATETIME, " +
                "ENDS       DATETIME, " +
                "REPEATS    INT, " +
                "USERID     INT FOREIGN KEY)");
        db.execSQL("CREATE TABLE CATEGORIES (" +
                "ID     INT PRIMARY KEY AUTOINCREMENT    NOT NULL," +
                "NAME   TEXT    NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USERS");
        db.execSQL("DROP TABLE IF EXISTS TASKS");
        onCreate(db);
    }
}
