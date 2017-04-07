package com.example.naman.eventplanning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EditRole extends AppCompatActivity {
    String RoleName, Desp, peopleNum,Role,Money, Needed_Role;
    TextView etEventNameEdit;
    EditText etdesp;
    EditText etnum;
    EditText etMoney;
    String Event;
    String EventName;
    String myEmail, myName,myEntityPK;
    ArrayList<String> Selected;
    ArrayAdapter<String> adapter;
    ListView lv;
    ArrayList<String> candidates;
    ArrayList<String> candidatesPK;
    ArrayList<String> selected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_role);

        etEventNameEdit = (TextView) findViewById(R.id.etEditEventName);
        etdesp = (EditText) findViewById(R.id.editdesp);
        etnum = (EditText) findViewById(R.id.editnum);
        etMoney = (EditText) findViewById(R.id.etMoney);

        Intent intent = getIntent();
        Needed_Role = intent.getStringExtra("needed_role");
        Event = intent.getStringExtra("Event");
        RoleName = intent.getStringExtra("RoleName");
        Role = intent.getStringExtra("Role");
        Desp = intent.getStringExtra("Descripition");
        peopleNum = intent.getStringExtra("PeopleNumber");
        EventName = intent.getStringExtra("EventName");
        Money = intent.getStringExtra("Money");
        myEmail= intent.getStringExtra("myEmail");
        myName = intent.getStringExtra("myName");
        myEntityPK = intent.getStringExtra("myEntityPK");



        //roleName = EventName;
        etEventNameEdit.setText(RoleName);
        etdesp.setText(Desp);
        etnum.setText(peopleNum);
        etMoney.setText(Money);




        lv = (ListView) findViewById(R.id.peopleSelected);

        //ADAPPTER
        Selected = new ArrayList<String>();
        candidates = new ArrayList<String>();
        candidatesPK = new ArrayList<String>();
        selected = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Selected);
        lv.setAdapter(adapter);
        getData();


        //next button
        Button btn = (Button)findViewById(R.id.next);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoleName = etEventNameEdit.getText().toString();
                Desp = etdesp.getText().toString();
                peopleNum = etnum.getText().toString();
                Money = etMoney.getText().toString();


                changeData();

            }
        });

        //back button
        Button backtn = (Button)findViewById(R.id.back);

        backtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(EditRole.this, EventRole.class));
//                String eventName = etEventNameEdit.getText().toString();
//                Intent intent = new Intent(EditRole.this, EventRole.class);
//                intent.putExtra("nameEdit", eventName);
//                String s = "yesEdit";
//                intent.putExtra("judgeEdit", s);
//
//                setResult(1, intent);
//                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Edit Role Description");


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {

        }
    }



    private void changeData(){


        String tag_json_obj = "json_obj_req";
        String url = "http://planmything.tech/api/event/" + Event + "/roles/" + Needed_Role;

//        String url = "http://planmything.tech/api/roles/18";

        Log.d("PutReq", "The url is " + url);
        Log.d("PutReq", "The Pk is " + Role);
        Log.d("PutReq", "The desp is " + Desp);
        Log.d("PutReq", "The quantity is " + peopleNum);
        Log.d("PutReq", "The needed_role is " + Needed_Role);

//        String url = "http://planmything.tech/api/roles/" + Role;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();




        Map<String, String> params = new HashMap();

        params.put("description", Desp);
        params.put("quantity_needed", peopleNum);
        params.put("estimated_budget", Money);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PutReq", response.toString());
                        pDialog.hide();
                        Intent intent=new Intent(EditRole.this,EditRole2.class);
                        intent.putExtra("Event", Event);
                        intent.putExtra("Needed_Role", Needed_Role);
                        intent.putExtra("Role", Role);
                        intent.putExtra("myEmail", myEmail);
                        intent.putExtra("myName", myName);
                        intent.putExtra("myEntityPK", myEntityPK);
                        intent.putExtra("EventNme", EventName);
                        intent.putStringArrayListExtra("candidates", candidates);
                        intent.putStringArrayListExtra("candidatesPK",candidatesPK);
                        intent.putStringArrayListExtra("selected", selected);

                        startActivityForResult(intent, 2);// Activity is started with requestCode 2

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PutReq"+ ": ", "Put Error Response code: " + error.networkResponse.statusCode);
                pDialog.hide();

            }
        });

// Adding request to request queue
        AppController.getInstance(this).addToRequestQueue(jsonObjReq, tag_json_obj);



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
//                                adapter.add(people.get(candidatesPK.get(i)));
//                                adapter.notifyDataSetChanged();

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
                                        Log.d("selected",candidates.get(j));
                                        Selected.add(candidates.get(j));
                                        adapter.notifyDataSetChanged();
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

