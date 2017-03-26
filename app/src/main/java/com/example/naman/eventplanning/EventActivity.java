package com.example.naman.eventplanning;

/**
 * Created by mengdili on 3/25/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;


public class EventActivity extends AppCompatActivity {
    ListView lv;
    Button addBtn;
    ArrayList<String> Event = new ArrayList<String>(
            Arrays.asList("Birthday","Small Party","Happy Party"));
    ArrayAdapter<String> adapter;
    String EventName, EventNameEdit;
    String judge,judgeEdit;
    int posEdit;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavMenu;



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        /*mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(EventActivity.this, LoginActivity.class));
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(EventActivity.this, LoginActivity.class));
                }
            }
        };*/




        setContentView(R.layout.addeventactivity);



        mNavMenu = (NavigationView) findViewById(R.id.nav_menu);
        mNavMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                System.out.println("Nav Item Selected");
                if (item.getItemId() == R.id.SignOut) {
                    mAuth.signOut();
                }
                return true;
            }
        });





        //set navigation bar

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_event_role);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Event List");

        lv = (ListView)findViewById(R.id.evenList1);
        addBtn = (Button) findViewById(R.id.btnAdd1);


        //ADAPPTER
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Event);
        lv.setAdapter(adapter);



        //Set selected item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                posEdit = position;
                Intent editIntent = new Intent(EventActivity.this, MainActivity.class );
                editIntent.putExtra("EventName", Event.get(position));
                EventActivity.this.startActivityForResult(editIntent,1);

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




        // Handle events
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(EventActivity.this, AddEventActivity.class );
                EventActivity.this.startActivityForResult(addIntent,1);
            }
        });

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
                if(judge != null && judge.equals("yes")){

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
    }
    //onActivityResult

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

