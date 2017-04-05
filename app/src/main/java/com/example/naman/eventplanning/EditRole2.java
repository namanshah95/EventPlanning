package com.example.naman.eventplanning;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditRole2 extends AppCompatActivity implements
        OnClickListener {
    Button next;
    ListView listView;
    ArrayList<String> guestsPK;
    ArrayList<String> guests;
    ArrayAdapter<String> adapter;
    Button back;
    String Role, Needed_Role;
    String Event;
    String EventName;
    String myEmail, myName, myEntityPK;
    String temp, EntityName;
    ArrayList<String> candidates, candidatesPK;

    ArrayList<String>  selected;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_role2);

        findViewsById();

        Intent intent = getIntent();
        Event = intent.getStringExtra("Event");
        Role = intent.getStringExtra("Role");
        EventName = intent.getStringExtra("Money");
        myEmail = intent.getStringExtra("myEmail");
        myName = intent.getStringExtra("myName");
        myEntityPK = intent.getStringExtra("myEntityPK");
        Needed_Role = intent.getStringExtra("Needed_Role");
        candidates = new ArrayList<>();
        candidatesPK = new ArrayList<>();
        selected = new ArrayList<>();


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, new ArrayList<String>());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);


        getData();

        next.setOnClickListener(this);

        //backbutton
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(2, intent);
                finish();
//                startActivity(new Intent(EditRole2.this, EditRole.class));
            }
        });

        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Select People for the Role");
    }


    private void findViewsById() {
        listView = (ListView) findViewById(R.id.list);
        next = (Button) findViewById(R.id.next);
        back = (Button) findViewById(R.id.back);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  if the request code is same as what is passed  here it is 2
        if (requestCode == 3) {

        }
    }

    public void onClick(View v) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        ArrayList<String> selectedItemsPK = new ArrayList<>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                selectedItems.add(adapter.getItem(position));
                 selectedItemsPK.add(candidatesPK.get(position));
        }

        String[] outputStrArr = new String[selectedItems.size()];
        String[] outputStrArrPK = new String[selectedItems.size()];

        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i);
            outputStrArrPK[i] = selectedItemsPK.get(i);
            Log.d("outputPK", selectedItemsPK.get(i));
        }

        Intent intent = new Intent(getApplicationContext(),
                EditRole3.class);


        // Create a bundle object

        intent.putExtra("Event", Event);
        intent.putExtra("Role", Role);
        intent.putExtra("Needed_Role",Needed_Role);
        intent.putExtra("myEmail", myEmail);
        intent.putExtra("myName", myName);
        intent.putExtra("myEntityPK", myEntityPK);
        intent.putExtra("EventNme", EventName);
        intent.putExtra("selectedItems", outputStrArr);
        intent.putExtra("selectedItemsPK",outputStrArrPK);

        // Add the bundle to the intent.


        // start the ResultActivity
        startActivity(intent);
    }

    private void getData(){
        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/event/" + Event + "/guests/?role=-2";
        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage("Loading...");
        pDialog.show();


        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GuestInit", response.toString());
                        if (response != null) {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = response.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    candidatesPK.add(jsonObject.getString("entity"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                            findName();
                        }
                        pDialog.hide();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("InitReq", "Error: " + error.getMessage());
                pDialog.hide();
            }
        });


        AppController.getInstance(this).addToRequestQueue(req, tag_json_arry);

    }


    private void findName(){

        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/entities/";
        Log.d("findName", "The url is " + url);

        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GuestInit", response.toString());
                        if (response != null) {
                            Map<String,String> people = new HashMap();
                            for(int i = 0; i< response.length(); i ++){
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = response.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    people.put(jsonObject.getString("entity"), jsonObject.getString("Name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                            for (int i = 0; i < candidatesPK.size(); i++) {
                                candidates.add(people.get(candidatesPK.get(i)));
                                Log.d("Task", "candidates is "+ people.get(candidatesPK.get(i)));
                                adapter.add(people.get(candidatesPK.get(i)));
                                adapter.notifyDataSetChanged();

                            }
                            getSelected();


                        }
                        pDialog.hide();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("InitReq", "Error: " + error.getMessage());
                pDialog.hide();
            }
        });


        AppController.getInstance(this).addToRequestQueue(req, tag_json_arry);



    }

    private void getSelected(){
        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/event/" + Event + "/guests/?role=" + Needed_Role;
        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage("Loading...");
        pDialog.show();


        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("BudgetInit", response.toString());
                        if (response != null) {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = response.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                   selected.add(jsonObject.getString("entity"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                            for(int i = 0; i < selected.size();i++ )
                            {
                                for (int j = 0; j < candidatesPK.size(); j++){
                                    if(selected.get(i).equals(candidatesPK.get(j))){
                                        listView.setItemChecked(j,true);
                                        break;
                                    }
                                }

                            }

                        }
                        pDialog.hide();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("InitReq", "Error: " + error.getMessage());
                pDialog.hide();
            }
        });


        AppController.getInstance(this).addToRequestQueue(req, tag_json_arry);

    }





}