package com.CSC481Project.ashley.quickmentiontest;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listViewUpcomingTasks;
    private Button buttonNewTask;
    private Button buttonSignOut;
    private static final String TAG = "MainActivity";
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        listViewUpcomingTasks = (ListView) findViewById(R.id.listViewUpcomingTasks);

        // == Not working for me for some reason ==
        // listViewUpcomingTasks.setLayoutParams(new LinearLayout.LayoutParams
        //                                      (LinearLayout.LayoutParams.FILL_PARENT, 4));

        buttonNewTask = (Button) findViewById(R.id.buttonNewTask);
        buttonNewTask.setOnClickListener(this);

        dbHelper = new DatabaseHelper(this);
        populateListView();

        /*
        Toolbar is being used for sign out. If we get rid of user logins for good then we can
        get rid of the toolbar. OR we could add other things to it instead.
         */
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    // Creates toolbar menu
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    // Listener for toolbar
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SignIn.class));
                break;

        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNewTask:
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                break;
        }


    }

    // Displays tasks to listView
    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data to ListView");

        Cursor data = dbHelper.getTasks();

        ArrayList<String> taskNames = new ArrayList<>();
        ArrayList<String> taskDates = new ArrayList<>();
        ArrayList<String> taskTimes = new ArrayList<>();
        while(data.moveToNext()) {
            taskNames.add(data.getString(1));
            taskDates.add(data.getString(2));
            taskTimes.add(data.getString(3));
        }


        TaskListAdapter adapter = new TaskListAdapter(this, taskNames, taskDates, taskTimes);
        listViewUpcomingTasks.setAdapter(adapter);
    }
}
