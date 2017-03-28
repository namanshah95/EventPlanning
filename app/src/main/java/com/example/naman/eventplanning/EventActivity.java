package com.example.naman.eventplanning;

/**
 * Created by mengdili on 3/25/17.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class EventActivity extends AppCompatActivity {
    ListView lv;
    Button addBtn;
    ArrayList<String> Event;
    ArrayAdapter<String> adapter;
    String EventName, EventNameEdit;
    String judge,judgeEdit;
    String EventPK;
    ArrayList<String> EventPKAll;
    int posEdit;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavMenu;

    String myEmail, myName, myEntityPK,event_entity_role;

    private DatabaseReference mDatabase;


    String Entity = "1";



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myName = dataSnapshot.getValue().toString();
                Log.d("Guest", "Name is " + myName);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("Email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myEmail = dataSnapshot.getValue().toString();
                getPK();
                Log.d("User", "Email is " + myEmail);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



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
        Event = new ArrayList<String>();
        EventPKAll = new ArrayList<String>();


        //ADAPPTER
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Event){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);


                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 300;
                view.setLayoutParams(params);

                return view;
            }
        };
        lv.setAdapter(adapter);



        //Set selected item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                posEdit = position;
                Intent editIntent = new Intent(EventActivity.this, MainActivity.class );
                editIntent.putExtra("EventName", Event.get(position));
                editIntent.putExtra("Event", EventPKAll.get(position));
                editIntent.putExtra("Email",myEmail);
                editIntent.putExtra("Name", myName);
                editIntent.putExtra("Entity",myEntityPK);
                Log.d("transfer","Event is "+ EventPKAll.get(position) );
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
            addEvent();
            adapter.add(EventName);

            //Refresh
            adapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(),"Added " + EventName, Toast.LENGTH_SHORT).show();

//            EventName = "";

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

    private void getData(){

        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/entity/" + myEntityPK + "/events/";

        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage("Loading...");
        pDialog.show();



        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("InitReq", response.toString());
                        if (response != null) {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = response.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String name = null;
                                String temp = null;
                                try {
                                    name = jsonObject.getString("event_name");
                                    temp = jsonObject.getString("event");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (name != null) {
                                    EventPKAll.add(temp);
                                    adapter.add(name);
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }

                        pDialog.hide();}

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("InitReq", "Error: " + error.getMessage());
            }
        });


        AppController.getInstance(this).addToRequestQueue(req, tag_json_arry);
    }

    private void addEvent(){
        String tag_json_obj = "json_obj_req";

        String url = "http://planmything.tech/api/events/";
        Map<String, String> params = new HashMap();
        Log.d("PostReq", "EventName is " + EventName);
        params.put("name", EventName);
        params.put("start_time", "2017-03-06 10:14:11.799291");
        params.put("end_time", "2017-03-06 10:14:11.799291");

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PostReq", response.toString());
                        try {
                            EventPK = response.getString("event");
                            EventPKAll.add(EventPK);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        addOwner();


                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PostReq", "Error: " + error.networkResponse.statusCode);

            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };


        AppController.getInstance(this).addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    private void addOwner(){

        String tag_json_obj = "json_obj_req";

        String url = "http://planmything.tech/api/event/" + EventPK + "/guests/";
        Map<String, String> params = new HashMap();

        params.put("entity", myEntityPK);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PostReq", response.toString());
                        try {
                            event_entity_role = response.getString("event_entity_role");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PostReq", "Error: " + error.networkResponse.statusCode);

            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

        };

// Adding request to request queue
        AppController.getInstance(this).addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private void getPK(){
        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/entities/?Email=" + myEmail;
        Log.d("GetPK", "The url is " + url);


        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GetPk", response.toString());
                        try {
                            myEntityPK = response.getJSONObject(0).getString("entity");
                            Log.d("GetPk", "Entity is "+ myEntityPK);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getData();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("InitReq", "Error: " + error.getMessage());

            }
        });

// Adding request to request queue
        AppController.getInstance(this).addToRequestQueue(req, tag_json_arry);
    }







}