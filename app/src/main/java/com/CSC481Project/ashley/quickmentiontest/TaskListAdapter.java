package com.CSC481Project.ashley.quickmentiontest;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ashley Coker on 3/6/2018.
 */

public class TaskListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> taskName;
    private final ArrayList<String> taskDay;
    private final ArrayList<String> taskTime;

    TaskListAdapter(Activity context, ArrayList<String> taskName, ArrayList<String> taskDay, ArrayList<String> taskTime) {
        super(context, R.layout.single_row_task, taskName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.taskName=taskName;
        this.taskDay=taskDay;
        this.taskTime=taskTime;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.single_row_task, null,true);

        TextView txtName = (TextView) rowView.findViewById(R.id.textViewTaskName);
        TextView txtDay = (TextView) rowView.findViewById(R.id.textViewTaskDate);
        TextView txtTime = (TextView) rowView.findViewById(R.id.textViewTaskTime);


        txtName.setText(taskName.get(position));
        txtDay.setText(taskDay.get(position));
        txtTime.setText(taskTime.get(position));
        return rowView;

    }

}
