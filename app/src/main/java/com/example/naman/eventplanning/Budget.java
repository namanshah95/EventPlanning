package com.example.naman.eventplanning;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.Arrays;
import java.util.ArrayList;

public class Budget extends AppCompatActivity {

    ArrayList<String> TaskArray = new ArrayList<String>(
            Arrays.asList("Drivers","Snack Bringers","Table Setup"));
    int posEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);


        //set actionbar title
        getSupportActionBar().setTitle("BUDGET MANAGER");
        getSupportActionBar().setSubtitle("Task List");

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, TaskArray);

        ListView listView = (ListView) findViewById(R.id.taskList);
        listView.setAdapter(adapter);

        //Set selected item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posEdit = position;
                Intent editIntent = new Intent(Budget.this, EditBudget.class );
                editIntent.putExtra("EventName", TaskArray.get(position));
                Budget.this.startActivityForResult(editIntent,1);

            }
        });


    }


}
