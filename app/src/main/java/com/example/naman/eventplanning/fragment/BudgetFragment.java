package com.example.naman.eventplanning.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.naman.eventplanning.AppController;
import com.example.naman.eventplanning.Budget;
import com.example.naman.eventplanning.EditBudget;
import com.example.naman.eventplanning.R;
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

/**
 * Created by mengdili on 3/10/17.
 */

public class BudgetFragment extends Fragment{

    String Event = "1";
    ListView listView;
    ArrayAdapter adapter;
    String RoleName;
    String PK;
    String Money;
    String myEmail, myName;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;


    //    ArrayList<String> TaskArray = new ArrayList<String>(
//            Arrays.asList("Drivers","Snack Bringers","Table Setup"));
    ArrayList<String> TaskArray;
    int posEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_budget, null);
        initView(view);
        return view;
    }

    private void initView(View view) {

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
                Log.d("User", "Name is " + myEmail);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


//
//        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_list_item_1, TaskArray);
        TaskArray = new ArrayList<String>();

        listView = (ListView) view.findViewById(R.id.taskList);
        getData();
//        listView.setAdapter(adapter);

        //Set selected item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posEdit = position;
                getPK(position);
//                Intent editIntent = new Intent(getContext(), EditBudget.class);
//                editIntent.putExtra("Event", Event);
//                editIntent.putExtra("RoleName", TaskArray.get(position));
//                startActivityForResult(editIntent, 1);

            }
        });


    }
    private void getData(){
        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/event/" + Event + "/roles/";
        final ProgressDialog pDialog = new ProgressDialog(getContext());

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
                                try {
                                    name = jsonObject.getString("needed_role_name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (name != null) {
                                    TaskArray.add(name);
                                }

                            }
                        }
                        pDialog.hide();
                        adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1, TaskArray);
                        listView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("InitReq", "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

// Adding request to request queue
        AppController.getInstance(getContext()).addToRequestQueue(req, tag_json_arry);
    }

    private void getPK(int position){

        RoleName =  TaskArray.get(position);
        String tag_json_arry = "json_array_req";
        Log.d("RoleName", "RoleName is " + RoleName);

        String url = "http://planmything.tech/api/event/" + Event +"/roles/";

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GetPk", response.toString());
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = response.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String name = null;
                            try {
                                name = jsonObject.getString("needed_role_name");
                                Log.d("RoleName", "name is "+ name);
                                Log.d("RoleName", "RoleName is " + RoleName);
                                if (name.equals(RoleName)){

                                    Log.d("RoleName","works here" );
                                    PK = jsonObject.getString("event_needed_role");
                                    Money = jsonObject.getString("estimated_budget");
                                    Log.d("GetPk","PK is "+ PK );
                                    break;


                                }
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }


                        }
                        Intent editIntent = new Intent(getContext(), EditBudget.class);
                        editIntent.putExtra("Event", Event);
                        editIntent.putExtra("Role", PK);
                        editIntent.putExtra("Money",Money);

                        startActivityForResult(editIntent, 1);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("getPK","Gett Error Response code: " + error.networkResponse.statusCode );

            }
        });

// Adding request to request queue
        AppController.getInstance(getContext()).addToRequestQueue(req, tag_json_arry);

    }



}
