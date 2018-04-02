package com.CSC481Project.ashley.quickmentiontest;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class DisplayTasksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "DisplayTasksActivity";
    SimpleCursorAdapter adapter;
    private static final int VEHICLE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tasks);

        ListView listView = (ListView) findViewById(R.id.listView);

        adapter = new SimpleCursorAdapter(this,
                R.layout.single_row_task,
                null,
                new String[] { QMContract.TaskEntry.KEY_NAME, QMContract.TaskEntry.KEY_START_DATE, QMContract.TaskEntry.KEY_START_TIME },
                new int[] { R.id.textViewTaskName, R.id.textViewTaskDate, R.id.textViewTaskTime }, 0);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(DisplayTasksActivity.this, CreateTaskActivity.class);

                Uri currentVehicleUri = ContentUris.withAppendedId(QMContract.TaskEntry.CONTENT_URI, id);

                intent.setData(currentVehicleUri);

                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                QMContract.TaskEntry._ID,
                QMContract.TaskEntry.KEY_NAME,
                QMContract.TaskEntry.KEY_START_DATE,
                QMContract.TaskEntry.KEY_START_TIME,
                QMContract.TaskEntry.KEY_END_DATE,
                QMContract.TaskEntry.KEY_END_TIME,
                QMContract.TaskEntry.KEY_REPEATS,
                QMContract.TaskEntry.KEY_NOTES

        };

        return new CursorLoader(this,   // Parent activity context
                QMContract.TaskEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
