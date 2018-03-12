package com.CSC481Project.ashley.quickmentiontest;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextTaskName, editTextNotes;
    private TextView tvStartDate, tvStartTime, tvEndDate, tvEndTime;
    private DatePickerDialog.OnDateSetListener StartDateSetListener, EndDateSetListener;
    private TimePickerDialog.OnTimeSetListener StartTimeSetListener, EndTimeSetListener;
    private Calendar calStart, calEnd;
    private DatabaseHelper dbHelper;
    private int hourStart, minuteStart, hourEnd, minuteEnd;
    private Button buttonSaveTask;
    private String formattedStartDate, formattedStartTime, formattedEndDate, formattedEndTime, repeats;
    private static final String TAG = "CreateTaskActivity";
    private Spinner spinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        setTitle("New Task");

        editTextTaskName = (EditText) findViewById(R.id.editTextTaskName);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        editTextNotes = (EditText) findViewById(R.id.editTextNotes);
        buttonSaveTask = (Button) findViewById(R.id.buttonSaveTask);
        spinner = (Spinner) findViewById(R.id.spinner);
        dbHelper = new DatabaseHelper(this);

        /*  */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null ) {
            String tempName = bundle.getString("templateName");
            repeats = bundle.getString("templateRepeats");
            editTextTaskName.setText(tempName);

            setRepeatsValue();

        }

        // Create calendar for start date and set initial display of start date and time.
        calStart = Calendar.getInstance();
        formattedStartDate = formatDate(calStart);
        tvStartDate.setText(formattedStartDate);
        hourStart = calStart.get(Calendar.HOUR_OF_DAY);
        minuteStart = calStart.get(Calendar.MINUTE);
        formattedStartTime = formatTime(hourStart, minuteStart, calStart);
        tvStartTime.setText(formattedStartTime);

        // Create calendar for end date. Task end time is set to one hour after start time.
        calEnd = Calendar.getInstance();
        formattedEndDate = formattedStartDate;
        tvEndDate.setText(formattedEndDate);
        hourEnd = hourStart + 1;
        minuteEnd = minuteStart;
        formattedEndTime = formatTime(hourEnd, minuteEnd, calEnd);
        tvEndTime.setText(formattedEndTime);



        tvStartDate.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        buttonSaveTask.setOnClickListener(this);

        // Listener for start date DatePicker. Gets date picked and displays to start date textview.
        // Resets end date to the same as start date.
        StartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                calStart.set(Calendar.YEAR, y);
                calStart.set(Calendar.MONTH, m);
                calStart.set(Calendar.DAY_OF_MONTH, d);
                formattedStartDate = formatDate(calStart);
                tvStartDate.setText(formattedStartDate);
                calEnd.set(Calendar.YEAR, y);
                calEnd.set(Calendar.MONTH, m);
                calEnd.set(Calendar.DAY_OF_MONTH, d);
                formattedEndDate = formattedStartDate;
                tvEndDate.setText(formattedEndDate);
                checkTimes();

            }
        };
        // Listener for start time TimePicker. Gets time picked and displays to start time textView.
        // Resets the end time to an hour after the start time.
        StartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hourStart   = h;
                minuteStart = m;
                formattedStartTime = formatTime(hourStart, minuteStart, calStart);
                tvStartTime.setText(formattedStartTime);
                hourEnd = hourStart + 1;
                minuteEnd = minuteStart;
                formattedEndTime = formatTime(hourEnd, minuteEnd, calEnd);
                tvEndTime.setText(formattedEndTime);
                checkTimes();
            }
        };
        // Listener for end date DatePicker. Gets date picked and displays to end date textview.
        EndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                calEnd.set(Calendar.YEAR, y);
                calEnd.set(Calendar.MONTH, m);
                calEnd.set(Calendar.DAY_OF_MONTH, d);
                formattedEndDate = formatDate(calEnd);
                tvEndDate.setText(formattedEndDate);
                checkTimes();
            }
        };
        // Listener for end time TimePicker. Gets time picked and displays to end time textView.
        EndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hourEnd = h;
                minuteEnd = m;
                formattedEndTime = formatTime(hourEnd, minuteEnd, calEnd);
                tvEndTime.setText(formattedEndTime);
                checkTimes();
            }
        };

    }

    // Gets string array of spinner options. Compares the options to the repeat value of
    // template. Sets spinner selected value to that option.
    private void setRepeatsValue() {
        String[] repeatStrings = this.getResources().getStringArray(R.array.repeat_options);
        for (int i = 0; i < repeatStrings.length; i++)
        {
            if (repeatStrings[i].equals(repeats)) {
                spinner.setSelection(i);
            }
        }
    }

    // Formats date of calendar object to "MM/dd/yyyy" string
    public String formatDate(Calendar calendar)
    {
        DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return simpleDateFormat.format(calendar.getTime());
    }

    // Formats time from military to civilian and determine AM or PM. Formats time to "hh:mm" string with am or pm.
    public String formatTime(int hours, int minutes, Calendar calendar) {
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
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        DateFormat simpleDateFormat = new SimpleDateFormat( "hh:mm", Locale.US);
        String formattedTime = simpleDateFormat.format(calendar.getTime());
        return formattedTime + " " + am_pm;

    }


    public void checkTimes() {
        if (calStart.compareTo(calEnd) > 0) {
            Log.d(TAG, "TaskActivity: checkTimes: Start date/time is after End date/time");
            buttonSaveTask.setEnabled(false);
        }
        else buttonSaveTask.setEnabled(true);
    }


    public void saveTask() {
        String name = editTextTaskName.getText().toString();
        String notes = editTextNotes.getText().toString();
        String repeats = String.valueOf(spinner.getSelectedItem());
        boolean insertData = dbHelper.addTask(name, formattedStartDate, formattedStartTime, formattedEndDate, formattedEndTime, repeats, notes);
        if(insertData) {
            setAlarm(calStart.getTimeInMillis());
            Toast.makeText(this, "Data added successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateTaskActivity.this, DisplayTasksActivity.class);
            startActivity(intent);
        }

        else Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

    }

    private void setAlarm(long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        if(alarmManager != null)
            alarmManager.setRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.tvStartDate:
                DatePickerDialog dpdStart = new DatePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, StartDateSetListener, calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH));
                if (dpdStart.getWindow() != null)
                    dpdStart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpdStart.show();

                break;
            case R.id.tvStartTime:
                TimePickerDialog tpdStart = new TimePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, StartTimeSetListener, hourStart, minuteStart, false);
                if(tpdStart.getWindow() != null)
                    tpdStart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tpdStart.show();
                break;
            case R.id.tvEndDate:
                DatePickerDialog dpdEnd = new DatePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, EndDateSetListener, calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH), calEnd.get(Calendar.DAY_OF_MONTH));
                if (dpdEnd.getWindow() != null)
                    dpdEnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpdEnd.show();
                break;
            case R.id.tvEndTime:
                TimePickerDialog tpdEnd = new TimePickerDialog(CreateTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, EndTimeSetListener, hourEnd, minuteEnd, false);
                if (tpdEnd.getWindow() != null)
                    tpdEnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tpdEnd.show();
                break;
            case R.id.buttonSaveTask:
                saveTask();
                break;

        }


    }
}
