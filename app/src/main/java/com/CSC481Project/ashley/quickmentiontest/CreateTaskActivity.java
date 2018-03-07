package com.CSC481Project.ashley.quickmentiontest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextTaskName, editTextNotes;
    private TextView textViewDateInput, textViewStartTimeInput, textViewEndTimeInput;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener2;
    Calendar c, c2;
    DatabaseHelper dbHelper;
    Spinner spinner;
    private int year, month, day, hourStart, minuteStart, hourEnd, minuteEnd;
    private Button buttonSaveTask;
    String date, timeStart, timeEnd, formattedDate, repeats;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        setTitle("New Task");

        editTextTaskName = (EditText) findViewById(R.id.editTextTaskName);
        textViewDateInput = (TextView) findViewById(R.id.textViewDateInput);
        textViewStartTimeInput = (TextView) findViewById(R.id.textViewStartTimeInput);
        textViewEndTimeInput = (TextView) findViewById(R.id.textViewEndTimeInput);
        editTextNotes = (EditText) findViewById(R.id.editTextNotes);
        buttonSaveTask = (Button) findViewById(R.id.buttonSaveTask);
        spinner = (Spinner) findViewById(R.id.spinner);
        dbHelper = new DatabaseHelper(this);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null ) {
            /* Gets selected template name and displays */
            String templateName = bundle.getString("templateName");
            editTextTaskName.setText(templateName);

            // Gets repeat value of template and sets spinner to that value
            repeats = bundle.getString("templateRepeats");
            String[] repeatStrings = this.getResources().getStringArray(R.array.repeat_options);
            for (int i = 0; i < repeatStrings.length; i++)
            {
                if (repeatStrings[i].equals(repeats)) {
                    spinner.setSelection(i);
                }
            }
        }


        /* Creates calendar with current date and time */
        c = Calendar.getInstance();
        updateDate();
        /* Sets start time to current time */
        hourStart = c.get(Calendar.HOUR_OF_DAY);
        minuteStart = c.get(Calendar.MINUTE);
        updateTimeStart(hourStart, minuteStart);

        /* Creates second calendar with current date and time */
        c2 = Calendar.getInstance();
        /* Sets end time to an hour after start time */
        hourEnd = hourStart + 1;
        minuteEnd = minuteStart;
        updateTimeEnd(hourEnd, minuteEnd);

        textViewDateInput.setOnClickListener(this);
        textViewStartTimeInput.setOnClickListener(this);
        textViewEndTimeInput.setOnClickListener(this);
        buttonSaveTask.setOnClickListener(this);

        /* Sets calendar date when date is selected */
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                c.set(Calendar.YEAR, y);
                c.set(Calendar.MONTH, m);
                c.set(Calendar.DAY_OF_MONTH, d);
                updateDate();
            }
        };

        /* Sets start time when time is selected */
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hourStart   = h;
                minuteStart = m;
                updateTimeStart(hourStart,minuteStart);
            }
        };

        /* Sets end time when time is selected */
        mTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hourEnd = h;
                minuteEnd = m;
                updateTimeEnd(hourEnd, minuteEnd);
            }
        };
    }

    /* Formats the date of the calendar object and displays as string */
    public void updateDate()
    {
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");
        formattedDate = simpleDateFormat.format(c.getTime());
        textViewDateInput.setText(formattedDate);
    }

    /* Formats start time and displays as string */
    public void updateTimeStart(int hours, int minute)
    {
        /* Determines whether the selected time is AM or PM */
        String am_pm;
        if (hours > 12) {
            hours -= 12;
            am_pm = "PM";
        } else if (hours == 0) {
            hours += 12;
            am_pm = "AM";
        } else if (hours == 12)
            am_pm = "PM";
        else
            am_pm = "AM";
        /* Sets calendar to selected time and formats string */
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minute);
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat( "hh:mm", Locale.US);
        timeStart = simpleDateFormat.format(c.getTime()) + " " + am_pm;

        /* Displays string */
        textViewStartTimeInput.setText(timeStart);

        /* Ensures that the start time is before end time */
        checkTimes();
    }

    /* Formats end time and displays as string */
    public void updateTimeEnd (int hours, int minute)
    {
        /* Determines whether the selected time is AM or PM */
        String am_pm = "";
        if (hours > 12) {
            hours -= 12;
            am_pm = "PM";
        } else if (hours == 0) {
            hours += 12;
            am_pm = "AM";
        } else if (hours == 12)
            am_pm = "PM";
        else
            am_pm = "AM";
        /* Sets second calendar to selected time and formats string */
        c2.set(Calendar.HOUR_OF_DAY, hours);
        c2.set(Calendar.MINUTE, minute);
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat( "hh:mm", Locale.US);
        timeEnd = simpleDateFormat.format(c2.getTime()) + " " + am_pm; //format your time
        /* Displays string */
        textViewEndTimeInput.setText(timeEnd);
        /* Ensures that the end time is after start time */
        checkTimes();


    }

    /* Checks whether the start time is before the end time. If not the save button is disabled. */
    public void checkTimes() {
        if((hourStart > hourEnd ) || (hourStart == hourEnd && minuteStart > minuteEnd))
            buttonSaveTask.setEnabled(false);
        else buttonSaveTask.setEnabled(true);
    }

    /* Saves task to database */
    public void saveTask() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String name = editTextTaskName.getText().toString();
        String notes = editTextNotes.getText().toString();
        String repeats = String.valueOf(spinner.getSelectedItem());
        boolean insertData = dbHelper.addTask(name, formattedDate, timeStart, timeEnd, repeats, notes);
        /* Task is inserted successfully */
        if(insertData) {
            Toast.makeText(this, "Data added successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateTaskActivity.this, DisplayTasksActivity.class);
            startActivity(intent);
        }
        /* Task failed to be inserted */
        else Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            /* When user clicks to input date, a date picker appears */
            case R.id.textViewDateInput:
                DatePickerDialog dpd = new DatePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpd.show();
                break;
            /* When user clicks to input start time, a time picker appears */
            case R.id.textViewStartTimeInput:
                TimePickerDialog tpd = new TimePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mTimeSetListener, hourStart, minuteStart, false);
                tpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tpd.show();
                break;
            /* When user clicks to input end time, a time picker appears */
            case R.id.textViewEndTimeInput:
                TimePickerDialog tpd2 = new TimePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mTimeSetListener2, hourEnd, minuteEnd, false);
                tpd2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tpd2.show();
                break;
            /* User clicks save task button */
            case R.id.buttonSaveTask:
                saveTask();

                break;

        }


    }
}