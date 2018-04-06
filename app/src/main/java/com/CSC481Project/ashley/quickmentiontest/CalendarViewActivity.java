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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener {

    private CalendarView calendarView;
    private NavigationView menu;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle menuToggle;
    private ListView listview;
    private DatabaseHelper dbHelper;
    private static final String TAG = "CalendarViewActivity";
    private String selectedDate;
    private SimpleDateFormat dateFormat;
    private DecimalFormat digitFormatter;
    SimpleCursorAdapter adapter;

    private static final int VEHICLE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);
        setTitle("Calendar");

        //setup side menu and toggle button
        menu = (NavigationView) findViewById(R.id.navigationView);
        menu.setNavigationItemSelectedListener(this); //have app call onNavigationItemSelected() when menu option is used
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(menuToggle);
        menuToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup widgets for this activity
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new DateChangeListener());
        //calendarView is already set to current date on creation
        //calendarView.setDate(new Date().getTime()); //set view to current date
        listview = (ListView) findViewById(R.id.calendarListView);
        dbHelper = new DatabaseHelper(this);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        digitFormatter = new DecimalFormat("00");
        selectedDate = dateFormat.format(new Date(calendarView.getDate())); //get current date in mm/dd/yyyy
        //commenting out populateListView because it uses old dbhelper and not QMContent
        //populateListView();

        adapter = new SimpleCursorAdapter(this,
                R.layout.single_row_task,
                null,
                new String[] { QMContract.TaskEntry.KEY_NAME, QMContract.TaskEntry.KEY_START_DATE, QMContract.TaskEntry.KEY_START_TIME },
                new int[] { R.id.textViewTaskName, R.id.textViewTaskDate, R.id.textViewTaskTime }, 0);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CalendarViewActivity.this, CreateTaskActivity.class);

                Uri currentVehicleUri = ContentUris.withAppendedId(QMContract.TaskEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentVehicleUri);


                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data to ListView");

        Cursor data = dbHelper.getTasks(selectedDate);

        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()) {
            //only add the task to the listView if its start date matches the selected date on the calendarView
            listData.add(data.getString(1) + "\t\t\t" + data.getString(2) + "\t\t\t" + data.getString(3) + "\t\t\t" + data.getString(5));
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listview.setAdapter(adapter);
    }

    //listener is called whenever a new date is picked
    private class DateChangeListener implements CalendarView.OnDateChangeListener
    {
        DateChangeListener()
        {

        }

        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int day)
        {
            //formatter makes sure month has leading zero so strings can match
            selectedDate = digitFormatter.format(month + 1) + "/" + digitFormatter.format(day) + "/" + year;
            Log.d(TAG, selectedDate);
            //commenting out populateListView because it uses dbhelper and not QMContent
            //populateListView();
            getLoaderManager().restartLoader(VEHICLE_LOADER, null, CalendarViewActivity.this);
            
        }
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
                QMContract.TaskEntry.KEY_NOTES

        };

        Calendar c = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String selectDate = dateFormat.format(c.getTime());
        String selection = "(" + QMContract.TaskEntry.KEY_START_DATE + " = '" + selectedDate + "')";


        return new CursorLoader(this,   // Parent activity context
                QMContract.TaskEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                selection,                   // No selection clause
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

    //from menu interface
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menuToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    //from menu interface
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            case R.id.newTask:
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                return true;
            case R.id.allTasks:
                startActivity(new Intent(getApplicationContext(), DisplayTasksActivity.class));
                return true;
        }

        return false;
    }
}
