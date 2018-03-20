package com.CSC481Project.ashley.quickmentiontest;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarViewActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView listview;
    private TextView textbox;
    private DatabaseHelper dbHelper;
    private static final String TAG = "CalendarViewActivity";
    private String selectedDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new DateChangeListener());
        //calendarView is already set to current date on creation
        //calendarView.setDate(new Date().getTime()); //set view to current date

        listview = (ListView) findViewById(R.id.calendarListView);
        textbox = (TextView) findViewById(R.id.calendarViewTextbox);
        dbHelper = new DatabaseHelper(this);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        populateListView();

    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data to ListView");

        Cursor data = dbHelper.getTasks();

        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()) {
            textbox.setText(selectedDate + " " + data.getString(2));
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

            selectedDate = (month+1) + "/" + day + "/" + year;
            populateListView();
        }
    }
}
