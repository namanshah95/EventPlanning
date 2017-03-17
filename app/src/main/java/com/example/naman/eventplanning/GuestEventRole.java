package com.example.naman.eventplanning;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class GuestEventRole extends AppCompatActivity {
    ListView lv;
    //ArrayList<String> Event = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String EventName, EventNameEdit;
    String judge,judgeEdit;
    int posEdit;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    ArrayList<String> Event = new ArrayList<String>(
            Arrays.asList("Drivers","Snack Bringers","Table Setup"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_event_role);

        //set navigation bar
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_event_role);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Task List");

        lv = (ListView)findViewById(R.id.evenList);


        //ADAPPTER
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Event);
        lv.setAdapter(adapter);



        //Set selected item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                view.setSelected(true);
//                lv.setItemChecked(1,true);
//                Object listItem = lv.getItemAtPosition(position);
                posEdit = position;
                Intent editIntent = new Intent(GuestEventRole.this, GuestEditRole.class );
                editIntent.putExtra("EventName", Event.get(position));
                GuestEventRole.this.startActivityForResult(editIntent,1);

            }
        });



        //swipe to delete
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    Event.remove(position);
                                    adapter.notifyDataSetChanged();

                                }

                            }
                        });
        lv.setOnTouchListener(touchListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                EventName = data.getStringExtra("name");
                judge = data.getStringExtra("judge");
                EventNameEdit = data.getStringExtra("nameEdit");
                System.out.println("");
                System.out.println(EventNameEdit);
                judgeEdit = data.getStringExtra("judgeEdit");
//                System.out.println("");
//                System.out.println(judge);
                if(judge != null && judge.equals("yes")){
//                    System.out.println("");
//                    System.out.println("It's correct");

                    add();
                    judge = "";
                }
                if(judgeEdit != null && judgeEdit.equals("yesEdit")){
                    update();
                    judgeEdit = "";
                }


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }



        }
    }//onActivityResult

    private void add(){
        if(!EventName.isEmpty() && EventName.length()> 0){
            //Add
            adapter.add(EventName);

            //Refresh
            adapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(),"Added " + EventName, Toast.LENGTH_SHORT).show();

            EventName = "";

        }
        else{
            Toast.makeText(getApplicationContext(), "!! Nothing to Add", Toast.LENGTH_SHORT).show();
        }

    }
    private void delete(){
        int pos= lv.getCheckedItemPosition();
        if (pos > -1)
        {
            //remove
            adapter.remove(Event.get(pos));

            //refresh
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Deleted ", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "!! Nothing to Delete", Toast.LENGTH_SHORT).show();
        }

    }

    private void update(){


        if(!EventNameEdit.isEmpty() && EventNameEdit.length()> 0){

            adapter.remove(Event.get(posEdit));
            adapter.insert(EventNameEdit, posEdit);
            adapter.notifyDataSetChanged();


            Toast.makeText(getApplicationContext(),"Edited Event", Toast.LENGTH_SHORT).show();

            EventNameEdit = "";

        }
        else{
            Toast.makeText(getApplicationContext(), "!! EventName cannot be null", Toast.LENGTH_SHORT).show();
        }
    }



}



