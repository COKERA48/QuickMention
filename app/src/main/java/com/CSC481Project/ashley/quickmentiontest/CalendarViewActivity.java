package com.CSC481Project.ashley.quickmentiontest;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalendarViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        }
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
