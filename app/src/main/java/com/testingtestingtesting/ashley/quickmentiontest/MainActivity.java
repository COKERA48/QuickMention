package com.testingtestingtesting.ashley.quickmentiontest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNewTask = (Button) findViewById(R.id.buttonNewTask);
        buttonNewTask.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
