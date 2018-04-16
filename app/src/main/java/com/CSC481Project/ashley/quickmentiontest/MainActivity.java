package com.CSC481Project.ashley.quickmentiontest;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    private ActionBarDrawerToggle toggle;
    SimpleCursorAdapter adapter;

    private static final int VEHICLE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        Toolbar toolbar = findViewById(R.id.toolbarMainActivity);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigation = findViewById(R.id.navigationView);
        navigation.setNavigationItemSelectedListener(this);
        TextView textViewNoTasks = findViewById(R.id.textViewNoTasks);

        ListView listViewUpcomingTasks = findViewById(R.id.listViewUpcomingTasks);
        listViewUpcomingTasks.setEmptyView(textViewNoTasks);

        adapter = new SimpleCursorAdapter(this,
                R.layout.single_row_task,
                null,
                new String[] { QMContract.TaskEntry.KEY_NAME, QMContract.TaskEntry.KEY_START_DATE, QMContract.TaskEntry.KEY_START_TIME },
                new int[] { R.id.textViewTaskName, R.id.textViewTaskDate, R.id.textViewTaskTime }, 0);

        listViewUpcomingTasks.setAdapter(adapter);

        listViewUpcomingTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);

                Uri currentVehicleUri = ContentUris.withAppendedId(QMContract.TaskEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentVehicleUri);


                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                QMContract.TaskEntry._ID1,
                QMContract.TaskEntry.KEY_NAME,
                QMContract.TaskEntry.KEY_START_DATE,
                QMContract.TaskEntry.KEY_START_TIME,
                QMContract.TaskEntry.KEY_END_DATE,
                QMContract.TaskEntry.KEY_END_TIME,
                QMContract.TaskEntry.KEY_REPEATS,
                QMContract.TaskEntry.KEY_NOTES,
                QMContract.TaskEntry.KEY_ALARM_ID,
                QMContract.TaskEntry.KEY_TIMESTAMP

        };

        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String selectDate = dateFormat.format(c.getTime());
        String selection = "(" + QMContract.TaskEntry.KEY_START_DATE + " = '" + selectDate + "')";


        return new CursorLoader(this,   // Parent activity context
                QMContract.TaskEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                selection,                   // No selection clause
                null,                   // No selection arguments
                QMContract.TaskEntry.KEY_TIMESTAMP);                  // Default sort order

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newTask:
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                return true;
            case R.id.calendar:
                startActivity(new Intent(getApplicationContext(), CalendarViewActivity.class));
                return true;
            case R.id.allTasks:
                startActivity(new Intent(getApplicationContext(), DisplayTasksActivity.class));
                return true;
        }

        return true;
    }
}
