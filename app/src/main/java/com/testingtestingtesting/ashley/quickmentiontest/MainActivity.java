package com.testingtestingtesting.ashley.quickmentiontest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonNewTask;
    private Button buttonSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNewTask = (Button) findViewById(R.id.buttonNewTask);
        buttonNewTask.setOnClickListener(this);
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);
        buttonSignOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == buttonSignOut){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }

    }
}
