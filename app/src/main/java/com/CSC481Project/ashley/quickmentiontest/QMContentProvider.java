package com.CSC481Project.ashley.quickmentiontest;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Ashley on 4/2/2018.
 */

public class QMContentProvider extends ContentProvider {
    public static final String LOG_TAG = QMContentProvider.class.getSimpleName();

    private static final int TASK = 100;
    private static final int TASK_ID = 101;

    private static final int CATEGORY = 200;
    private static final int CATEGORY_ID = 201;

    private static final int TEMPLATE = 300;
    private static final int TEMPLATE_ID = 301;



    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(QMContract.CONTENT_AUTHORITY, QMContract.PATH_VEHICLE1, TASK);
        sUriMatcher.addURI(QMContract.CONTENT_AUTHORITY, QMContract.PATH_VEHICLE1 + "/#", TASK_ID);

        sUriMatcher.addURI(QMContract.CONTENT_AUTHORITY, QMContract.PATH_VEHICLE2, CATEGORY);
        sUriMatcher.addURI(QMContract.CONTENT_AUTHORITY, QMContract.PATH_VEHICLE2 + "/#", CATEGORY_ID);

        sUriMatcher.addURI(QMContract.CONTENT_AUTHORITY, QMContract.PATH_VEHICLE3, TEMPLATE);
        sUriMatcher.addURI(QMContract.CONTENT_AUTHORITY, QMContract.PATH_VEHICLE3 + "/#", TEMPLATE_ID);

    }

    private DatabaseHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK:
                cursor = database.query(QMContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TASK_ID:
                selection = QMContract.TaskEntry._ID1 + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(QMContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CATEGORY:
                cursor = database.query(QMContract.CategoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CATEGORY_ID:
                selection = QMContract.CategoryEntry._ID2 + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(QMContract.CategoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TEMPLATE:
                cursor = database.query(QMContract.TemplateEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TEMPLATE_ID:
                selection = QMContract.TemplateEntry._ID3 + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(QMContract.TemplateEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK:
                return QMContract.TaskEntry.CONTENT_LIST_TYPE;
            case TASK_ID:
                return QMContract.TaskEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return QMContract.CategoryEntry.CONTENT_LIST_TYPE;
            case CATEGORY_ID:
                return QMContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case TEMPLATE:
                return QMContract.TemplateEntry.CONTENT_LIST_TYPE;
            case TEMPLATE_ID:
                return QMContract.TemplateEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK:
                return insertTask(uri, contentValues);
            case TEMPLATE:
                return insertTemplate(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertTemplate(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(QMContract.TemplateEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertTask(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(QMContract.TaskEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

   


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK:
                rowsDeleted = database.delete(QMContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                selection = QMContract.TaskEntry._ID1 + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(QMContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TEMPLATE:
                rowsDeleted = database.delete(QMContract.TemplateEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TEMPLATE_ID:
                selection = QMContract.TemplateEntry._ID3 + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(QMContract.TemplateEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK:
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TASK_ID:
                selection = QMContract.TaskEntry._ID1 + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TEMPLATE_ID:
                selection = QMContract.TemplateEntry._ID3 + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTemplate(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateTemplate(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(QMContract.TemplateEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(QMContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
