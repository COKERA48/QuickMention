package com.CSC481Project.ashley.quickmentiontest;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private EditText editTextTaskName, editTextNotes;
    private TextView tvStartDate, tvStartTime, tvEndDate, tvEndTime;
    private DatePickerDialog.OnDateSetListener StartDateSetListener, EndDateSetListener;
    private TimePickerDialog.OnTimeSetListener StartTimeSetListener, EndTimeSetListener;
    Calendar calStart, calEnd;
    private Button buttonSaveTask, buttonDeleteTask;
    private static final String TAG = "TaskActivity";
    Spinner spinner;
    DateFormat dateFormat, timeFormat;
    String sourceClass = "";


    private Uri mCurrentReminderUri;

    private static final int EXISTING_VEHICLE_LOADER = 0;

    // Values for orientation change
    private static final String KEY_NAME = "title_key";
    private static final String KEY_START_TIME = "start_time_key";
    private static final String KEY_START_DATE = "start_date_key";
    private static final String KEY_END_TIME = "end_time_key";
    private static final String KEY_END_DATE = "end_date_key";
    private static final String KEY_REPEATS = "repeats_key";
    private static final String KEY_NOTES = "notes_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        editTextTaskName = (EditText) findViewById(R.id.editTextTaskName);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        editTextNotes = (EditText) findViewById(R.id.editTextNotes);
        buttonSaveTask = (Button) findViewById(R.id.buttonSaveTask);
        buttonDeleteTask = (Button) findViewById(R.id.buttonDeleteTask);
        spinner = (Spinner) findViewById(R.id.spinner);
        Intent intent = getIntent();
        mCurrentReminderUri = intent.getData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sourceClass = bundle.getString("ClassName");
        }

        if (sourceClass.equals("TemplateActivity")) {
            Log.d(TAG, "test");
            setTitle("New Task");

            getLoaderManager().initLoader(1, null, this);

            buttonDeleteTask.setVisibility(View.GONE);
        } else {

            setTitle("Edit Task");
            buttonSaveTask.setText("Update Task");

            getLoaderManager().initLoader(EXISTING_VEHICLE_LOADER, null, this);
        }

        dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        timeFormat = new SimpleDateFormat( "hh:mm a", Locale.US);

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
        buttonDeleteTask.setOnClickListener(this);

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedName = savedInstanceState.getString(KEY_NAME);
            editTextTaskName.setText(savedName);

            String savedStartDate = savedInstanceState.getString(KEY_START_DATE);
            tvStartDate.setText(savedStartDate);

            String savedStartTime = savedInstanceState.getString(KEY_START_TIME);
            tvStartTime.setText(savedStartTime);

            String savedEndDate = savedInstanceState.getString(KEY_END_DATE);
            tvEndDate.setText(savedEndDate);

            String savedEndTime = savedInstanceState.getString(KEY_END_TIME);
            tvEndTime.setText(savedEndTime);

            String savedRepeats = savedInstanceState.getString(KEY_REPEATS);
            setRepeatsValue(savedRepeats);

            String savedNotes = savedInstanceState.getString(KEY_NOTES);
            editTextNotes.setText(savedNotes);

        }

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

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_NAME, editTextTaskName.getText());
        outState.putCharSequence(KEY_START_TIME, tvStartTime.getText());
        outState.putCharSequence(KEY_START_DATE, tvStartDate.getText());
        outState.putCharSequence(KEY_END_TIME, tvEndTime.getText());
        outState.putCharSequence(KEY_END_DATE, tvEndDate.getText());
        outState.putCharSequence(KEY_REPEATS, spinner.getSelectedItem().toString());
        outState.putCharSequence(KEY_NOTES, editTextNotes.getText());
    }

    // Gets string array of spinner options. Compares the options to the repeat value of
    // template. Sets spinner selected value to that option.
    private void setRepeatsValue(String repeats) {
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
        String startDate = dateFormat.format(calStart.getTime());
        String startTime = timeFormat.format(calStart.getTime());
        String endDate = dateFormat.format(calEnd.getTime());
        String endTime = timeFormat.format(calEnd.getTime());
        String repeats = String.valueOf(spinner.getSelectedItem());
        String notes = editTextNotes.getText().toString();

        ContentValues values = new ContentValues();

        values.put(QMContract.TaskEntry.KEY_NAME, name);
        values.put(QMContract.TaskEntry.KEY_START_DATE, startDate);
        values.put(QMContract.TaskEntry.KEY_START_TIME, startTime);
        values.put(QMContract.TaskEntry.KEY_END_DATE, endDate);
        values.put(QMContract.TaskEntry.KEY_END_TIME, endTime);
        values.put(QMContract.TaskEntry.KEY_REPEATS, repeats);
        values.put(QMContract.TaskEntry.KEY_NOTES, notes);

        if (sourceClass.equals("TemplateActivity")) {
            // This is a NEW reminder, so insert a new reminder into the provider,
            // returning the content URI for the new reminder.
            Uri newUri = getContentResolver().insert(QMContract.TaskEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Something went wrong.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Task Saved!",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                setAlarm();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentReminderUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Something went wrong.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Task Updated!",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        }


    }

    private void setAlarm() {
        long interval = 0;
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        intent.putExtra("taskName", editTextTaskName.getText().toString());
        final int alarmID = (int) System.currentTimeMillis();
        intent.putExtra("alarmID", alarmID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);



        if (alarmManager != null) {
            if (spinner.getSelectedItem().toString().equals("Never")) {
                if (Build.VERSION.SDK_INT >= 19) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calStart.getTimeInMillis(), pendingIntent);
                }
                else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calStart.getTimeInMillis(), pendingIntent);
                }
            }
            else {

                switch (spinner.getSelectedItem().toString()) {
                    case "Every Day":
                        interval = 86400000;
                        break;
                    case "Every Other Day":
                        interval = 172800000;
                        break;
                    case "Every Week":
                        interval = 604800000;
                        break;
                    case "Every 2 Weeks":
                        interval = 1209600000;
                        break;
                    case "Every Month":
                        interval = 2629746000L;
                        break;
                    case "Every Year":
                        interval = 31556952000L;
                        break;
                }



                if (Build.VERSION.SDK_INT >= 19) {
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calStart.getTimeInMillis(), interval, pendingIntent);
                }
                else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calStart.getTimeInMillis(), interval, pendingIntent);
                }

            }




            Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "setAlarm: time" + calStart.getTime().toString() + " " + interval);

        }
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
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, StartTimeSetListener, calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), false);
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
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, EndTimeSetListener, calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE), false);
                if (tpdEnd.getWindow() != null)
                    tpdEnd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tpdEnd.show();
                break;
            case R.id.buttonSaveTask:
                saveTask();
                break;
            case R.id.buttonDeleteTask:
                deleteTask();
                break;

        }


    }

    private void deleteTask() {

        // Only perform the delete if this is an existing reminder.
        if (!sourceClass.equals("TemplateActivity")) {
            // Call the ContentResolver to delete the reminder at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentreminderUri
            // content URI already identifies the reminder that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentReminderUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error Deleting Task.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Task Deleted!",
                        Toast.LENGTH_SHORT).show();
            }
        }

        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = null;
        switch (i){
            case EXISTING_VEHICLE_LOADER:
                String[] projection = {
                        QMContract.TaskEntry._ID1,
                        QMContract.TaskEntry.KEY_NAME,
                        QMContract.TaskEntry.KEY_START_DATE,
                        QMContract.TaskEntry.KEY_START_TIME,
                        QMContract.TaskEntry.KEY_END_DATE,
                        QMContract.TaskEntry.KEY_END_TIME,
                        QMContract.TaskEntry.KEY_REPEATS,
                        QMContract.TaskEntry.KEY_NOTES,
                };

                // This loader will execute the ContentProvider's query method on a background thread
                loader = new CursorLoader(this,   // Parent activity context
                        mCurrentReminderUri,         // Query the content URI for the current reminder
                        projection,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
                break;
            case 1:
                String[] projection2 = {
                        QMContract.TemplateEntry._ID3,
                        QMContract.TemplateEntry.KEY_NAME,
                        QMContract.TemplateEntry.KEY_REPEATS,
                        QMContract.TemplateEntry.KEY_TEMP_CAT
                };
                loader = new CursorLoader(this,   // Parent activity context
                        mCurrentReminderUri,         // Query the content URI for the current reminder
                        projection2,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
                break;
            default:
                return null;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        switch (loader.getId()) {
            case 0:
                // Proceed with moving to the first row of the cursor and reading data from it
                // (This should be the only row in the cursor)
                if (cursor.moveToFirst()) {
                    int nameColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_NAME);
                    int startDateColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_START_DATE);
                    int startTimeColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_START_TIME);
                    int endDateColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_END_DATE);
                    int endTimeColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_END_TIME);
                    int repeatsColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_REPEATS);
                    int notesColumnIndex = cursor.getColumnIndex(QMContract.TaskEntry.KEY_NOTES);

                    // Extract out the value from the Cursor for the given column index
                    String name = cursor.getString(nameColumnIndex);
                    String startDate = cursor.getString(startDateColumnIndex);
                    String startTime = cursor.getString(startTimeColumnIndex);
                    String endDate = cursor.getString(endDateColumnIndex);
                    String endTime = cursor.getString(endTimeColumnIndex);
                    String repeats = cursor.getString(repeatsColumnIndex);
                    String notes = cursor.getString(notesColumnIndex);

                    // Update the views on the screen with the values from the database
                    editTextTaskName.setText(name);
                    tvStartDate.setText(startDate);
                    tvStartTime.setText(startTime);
                    tvEndDate.setText(endDate);
                    tvEndTime.setText(endTime);
                    setRepeatsValue(repeats);
                    editTextNotes.setText(notes);
                }
                break;
            case 1:
                if (cursor.moveToFirst()){

                    int nameColumnIndex = cursor.getColumnIndex(QMContract.TemplateEntry.KEY_NAME);
                    int repeatsColumnIndex = cursor.getColumnIndex(QMContract.TemplateEntry.KEY_REPEATS);

                    String name = cursor.getString(nameColumnIndex);
                    String repeats = cursor.getString(repeatsColumnIndex);

                    editTextTaskName.setText(name);
                    setRepeatsValue(repeats);
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
