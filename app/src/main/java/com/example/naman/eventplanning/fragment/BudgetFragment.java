package com.example.naman.eventplanning.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import com.example.naman.eventplanning.Budget;
import com.example.naman.eventplanning.EditBudget;
import com.example.naman.eventplanning.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mengdili on 3/10/17.
 */

public class BudgetFragment extends Fragment{



    ArrayList<String> TaskArray = new ArrayList<String>(
            Arrays.asList("Drivers","Snack Bringers","Table Setup"));
    int posEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_budget, null);
        initView(view);
        return view;
    }

    private void initView(View view) {


        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, TaskArray);

        ListView listView = (ListView) view.findViewById(R.id.taskList);
        listView.setAdapter(adapter);

        //Set selected item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posEdit = position;
                Intent editIntent = new Intent(getContext(), EditBudget.class);
                editIntent.putExtra("EventName", TaskArray.get(position));
                startActivityForResult(editIntent, 1);

            }
        });


    }

}
