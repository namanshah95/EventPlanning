package com.example.naman.eventplanning;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
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

        String[] sports = getResources().getStringArray(R.array.people_array);
        guests = new ArrayList<String>();
        guestsPK = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, guests);
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
                 selectedItemsPK.add(guestsPK.get(position));
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

    private void getData() {
        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/event/" + Event + "/guests/";
        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage("Loading...");
        pDialog.show();


        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("InitReq", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = response.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                temp = jsonObject.getString("entity");

                                findName();
                            } catch (JSONException e) {
                                e.printStackTrace();
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

// Adding request to request queue
        AppController.getInstance(this).addToRequestQueue(req, tag_json_arry);
    }


    private void findName(){

        String tag_json_obj = "json_obj_req";

        String url = "http://planmything.tech/api/entities/" +temp;
        Log.d("findName", "The url is " + url);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("GetPk", response.toString());
                        try {
                            EntityName = response.getString("Name");

                            Log.d("FindName", "EntityName is " + EntityName);
                            guestsPK.add(response.getString("entity"));
                            adapter.add(EntityName);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("findName", "Error: " + error.getMessage());

            }
        });

        AppController.getInstance(this).addToRequestQueue(jsonObjReq, tag_json_obj);




    }




}