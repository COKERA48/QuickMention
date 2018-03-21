package com.CSC481Project.ashley.quickmentiontest;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
    DateFormat dateFormat, timeFormat;



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

        dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        timeFormat = new SimpleDateFormat( "hh:mm a", Locale.US);

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
        calStart.set(Calendar.SECOND, 0);
        tvStartDate.setText(dateFormat.format(calStart.getTime()));
        tvStartTime.setText(timeFormat.format(calStart.getTime()));

        // Create calendar for end date. Task end time is set to one hour after start time.
        calEnd = Calendar.getInstance();
        calEnd.set(Calendar.SECOND, 0);
        calEnd.add(Calendar.HOUR_OF_DAY, 1);
        tvEndDate.setText(dateFormat.format(calEnd.getTime()));
        tvEndTime.setText(timeFormat.format(calEnd.getTime()));



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
                calStart.set(y, m, d);
                tvStartDate.setText(dateFormat.format(calStart.getTime()));
                calEnd.set(y, m, d);
                tvEndDate.setText(dateFormat.format(calEnd.getTime()));
                checkTimes();

            }
        };
        // Listener for start time TimePicker. Gets time picked and displays to start time textView.
        // Resets the end time to an hour after the start time.
        StartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                calStart.set(Calendar.HOUR_OF_DAY, h);
                calStart.set(Calendar.MINUTE, m);
                calEnd.set(Calendar.HOUR_OF_DAY, h+1);
                calEnd.set(Calendar.MINUTE, m);
                tvStartTime.setText(timeFormat.format(calStart.getTime()));
                tvEndTime.setText(timeFormat.format(calEnd.getTime()));
                checkTimes();
            }
        };
        // Listener for end date DatePicker. Gets date picked and displays to end date textview.
        EndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                calEnd.set(y, m, d);
                tvEndDate.setText(dateFormat.format(calEnd.getTime()));
                checkTimes();
            }
        };
        // Listener for end time TimePicker. Gets time picked and displays to end time textView.
        EndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                calEnd.set(Calendar.HOUR_OF_DAY, h);
                calEnd.set(Calendar.MINUTE, m);
                tvEndTime.setText(timeFormat.format(calEnd.getTime()));
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
        boolean insertData = dbHelper.addTask(name, dateFormat.format(calStart.getTime()), timeFormat.format(calStart.getTime()), dateFormat.format(calEnd.getTime()), timeFormat.format(calEnd.getTime()), repeats, notes);
        if(insertData) {
            Toast.makeText(this, "Task Saved!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            setAlarm();
        }

        else Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

    }

    private void setAlarm() {
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        intent.putExtra("taskName", editTextTaskName.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calStart.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calStart.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }

            Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "setAlarm: " + calStart.getTime().toString());

        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.tvStartDate:
                DatePickerDialog dpdStart = new DatePickerDialog(this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, StartDateSetListener, calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH));
                if (dpdStart.getWindow() != null)
                    dpdStart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpdStart.show();

                break;
            case R.id.tvStartTime:
                TimePickerDialog tpdStart = new TimePickerDialog(this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, StartTimeSetListener, calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), false);
                if(tpdStart.getWindow() != null)
                    tpdStart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tpdStart.show();
                break;
            case R.id.tvEndDate:
                DatePickerDialog dpdEnd = new DatePickerDialog(this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, EndDateSetListener, calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH), calEnd.get(Calendar.DAY_OF_MONTH));
                if (dpdEnd.getWindow() != null)
                    dpdEnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpdEnd.show();
                break;
            case R.id.tvEndTime:
                TimePickerDialog tpdEnd = new TimePickerDialog(this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, EndTimeSetListener, calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE), false);
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
