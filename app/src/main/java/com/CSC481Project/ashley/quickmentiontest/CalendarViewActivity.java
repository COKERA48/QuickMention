package com.CSC481Project.ashley.quickmentiontest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

public class CalendarViewActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView textbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new DateChangeListener());
        //calendarView is already set to current date on creation
        //calendarView.setDate(new Date().getTime()); //set view to current date

        textbox = (TextView) findViewById(R.id.calendarViewTextbox);
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
            textbox.setText(month + "/" + day + "/" + year);
        }
    }
}
