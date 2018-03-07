package com.CSC481Project.ashley.quickmentiontest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by davidortego on 3/6/18.
 */

public class CategoryListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> catName;
    private final Integer[] imgId;

    CategoryListAdapter(Activity context, ArrayList<String> catName, Integer[] imgId) {
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

        txtTitle.setText(catName.get(position));
        imageView.setImageResource(imgId[position]);
        return rowView;

    }

}
