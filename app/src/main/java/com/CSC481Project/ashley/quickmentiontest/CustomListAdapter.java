package com.CSC481Project.ashley.quickmentiontest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by davidortego on 3/6/18.
 */

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] catName;
    private final Integer[] imgId;

    public CustomListAdapter(Activity context, String[] catName, Integer[] imgId) {
        super(context, R.layout.single_row_category, catName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.catName=catName;
        this.imgId=imgId;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.single_row_category, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        txtTitle.setText(catName[position]);
        imageView.setImageResource(imgId[position]);
        return rowView;

    }

}
