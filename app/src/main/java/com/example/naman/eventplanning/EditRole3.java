package com.example.naman.eventplanning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.naman.eventplanning.fragment.EventroleFragment;
import com.example.naman.eventplanning.fragment.GuestFragment;

public class EditRole3 extends AppCompatActivity {

    String[] resultArr;
    String[] resultPK;

    String Role, Needed_Role;
    String Event ;
    String EventName;
    String myEmail, myName,myEntityPK;
    ArrayList<String> candidates, candidatesPK;
    ArrayList<Integer> selectedPosOld;
    ArrayList<Integer> selectedPosNew;
    ArrayAdapter<String> adapter;

    String RoleName, Desp, peopleNum,Money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_role3);

        Intent intent = getIntent();
        EventName = intent.getStringExtra("Money");
        myEmail= intent.getStringExtra("myEmail");
        myName = intent.getStringExtra("myName");
        myEntityPK = intent.getStringExtra("myEntityPK");
        Event = getIntent().getStringExtra("Event");
        Role = getIntent().getStringExtra("Role");
        Needed_Role = getIntent().getStringExtra("Needed_Role");

        RoleName = intent.getStringExtra("RoleName");
        Desp = intent.getStringExtra("Descripition");
        peopleNum = intent.getStringExtra("PeopleNumber");
        Money = intent.getStringExtra("Money");

        resultArr = intent.getStringArrayExtra("selectedItems");
        resultPK = intent.getStringArrayExtra("selectedItemsPK");
        candidates= intent.getStringArrayListExtra("candidates");
        candidatesPK = intent.getStringArrayListExtra("candidatesPK");
        selectedPosNew = intent.getIntegerArrayListExtra("selectedNew");
        selectedPosOld = intent.getIntegerArrayListExtra("selectedOld");
        ListView lv = (ListView) findViewById(R.id.outputList);

        adapter = new ArrayAdapter<String>(this,
                R.layout.mylist, resultArr);
        lv.setAdapter(adapter);

        //back button
        Button backtn = (Button)findViewById(R.id.back);

        backtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //startActivity(new Intent(EditRole3.this, EditRole2.class));

                Intent intent = new Intent();
                setResult(3,intent);
                finish();
            }
        });


        //submit button
        Button submitn = (Button) findViewById(R.id.submit);
        submitn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignRole();


            }


        });

        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Selected People");
    }

    // POST /event/{event}/entities/{entity}/roles/
    // Assign people to the role


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  if the request code is same as what is passed  here it is 2
        if (requestCode == 4) {

        }
    }


    private void assignRole(){
        if (selectedPosOld.contains(0) && !selectedPosNew.contains(0)){
           //delete Owner
            Log.d("select", "delete owner");
            deleteDataOwner();
        }
        if (!selectedPosOld.contains(0) && selectedPosNew.contains(0)){
            Log.d("select", "post owner");
            postData(0);
        }

        for(int i = 1;i < candidatesPK.size();i ++){
            if (selectedPosOld.contains(i) && !selectedPosNew.contains(i)){
                deleteData(i);
            }
            else if(!selectedPosOld.contains(i) && selectedPosNew.contains(i)){
                postData(i);
            }
        }


        Toast.makeText(getApplicationContext(),
                "Sumbit Sucessfully", Toast.LENGTH_SHORT).show();
        Log.d("Intent", "Event is " + Event);
        Log.d("Intent", "Role is " + Role);



        Intent intent = new Intent(EditRole3.this, MainActivity.class);
        intent.putExtra("Check","EventRole");
        intent.putExtra("Event", Event);
        intent.putExtra("myEmail", myEmail);
        intent.putExtra("myName", myName);
        intent.putExtra("myEntityPK", myEntityPK);
        intent.putExtra("EventName", EventName);
        intent.putExtra("Money", Money);
//        intent.putExtra("Descripition", Desp);
//        intent.putExtra("PeopleNumber", peopleNum);
//        intent.putExtra("RoleName", RoleName);
//        intent.putExtra("needed_role", Needed_Role);
//        intent.putExtra("Role",Role);
        startActivity(intent);

    }






    private void postData(int position){
        String tag_json_obj = "json_obj_req";

        String url = "http://planmything.tech/api/event/" + Event + "/entities/" + candidatesPK.get(position) + "/roles/" ;
        Log.d("PostReq", "The url of assigning role is "+ url);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        Map<String, String> params = new HashMap();
        params.put("role", Needed_Role);
        Log.d("PostReq", "the role is " + Needed_Role);


        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PostReq", response.toString());
                        pDialog.hide();


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PostReq", "Error: " + error.networkResponse.statusCode);
                pDialog.hide();
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

    //DELETE /event/{event}/guests/{guest}/roles/{role}

    private void deleteData(int position) {
        String tag_json_obj = "json_obj_req";
        String url = "http://planmything.tech/api/event/" + Event + "/guests/" + candidatesPK.get(position)+"/roles/"+ Needed_Role;
        Log.d("DeleteReq: ", "url is " + url);

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DeleteReq" + ": ", "delete onResponse : " + response.toString());

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (null != error.networkResponse) {
                            Log.d("DeleteReq"+ ": ", "delete Error Response code: " + error.networkResponse.statusCode);

                        }
                    }
                });
        AppController.getInstance(this).addToRequestQueue(request, tag_json_obj);

    }


    private void deleteDataOwner() {
        String tag_json_obj = "json_obj_req";
        String url = "http://planmything.tech/api/event/" + Event + "/owner/roles/"+ Needed_Role;
        Log.d("DeleteReq: ", "url is " + url);

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DeleteReq" + ": ", "delete onResponse : " + response.toString());

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (null != error.networkResponse) {
                            Log.d("DeleteReq"+ ": ", "delete Error Response code: " + error.networkResponse.statusCode);

                        }
                    }
                });
        AppController.getInstance(this).addToRequestQueue(request, tag_json_obj);

    }






}