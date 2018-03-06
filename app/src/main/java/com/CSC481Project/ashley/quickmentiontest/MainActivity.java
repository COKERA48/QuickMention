package com.CSC481Project.ashley.quickmentiontest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listViewUpcomingTasks;
    private Button buttonNewTask;
    private Button buttonSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewUpcomingTasks = (ListView) findViewById(R.id.listViewUpcomingTasks);
        listViewUpcomingTasks.setLayoutParams(new LinearLayout.LayoutParams
                                             (LinearLayout.LayoutParams.FILL_PARENT, 4));

        buttonNewTask = (Button) findViewById(R.id.buttonNewTask);
        buttonNewTask.setOnClickListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

    }

    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

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
}
