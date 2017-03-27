package com.example.naman.eventplanning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class EditRole extends AppCompatActivity {
    String RoleName, Desp, peopleNum,Role,Money;
    EditText etEventNameEdit;
    EditText etdesp;
    EditText etnum;
    EditText etMoney;
    String Event ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_role);

        etEventNameEdit = (EditText) findViewById(R.id.etEditEventName);
        etdesp = (EditText) findViewById(R.id.editdesp);
        etnum = (EditText) findViewById(R.id.editnum);
        etMoney = (EditText) findViewById(R.id.editMoney);

        Intent intent = getIntent();
        Event = intent.getStringExtra("Event");
        RoleName = intent.getStringExtra("RoleName");
        Role = intent.getStringExtra("Role");
        Desp = intent.getStringExtra("Descripition");
        peopleNum = intent.getStringExtra("PeopleNumber");
        Money = intent.getStringExtra("Money");

        //roleName = EventName;
        etEventNameEdit.setText(RoleName);
        etdesp.setText(Desp);
        etnum.setText(peopleNum);
        etMoney.setText(Money);
//        getData(Role);



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
                String eventName = etEventNameEdit.getText().toString();
                Intent intent = new Intent(EditRole.this, EventRole.class);
                intent.putExtra("nameEdit", eventName);
                String s = "yesEdit";
                intent.putExtra("judgeEdit", s);

                setResult(1, intent);
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

//    private void getData(String Role) {
//
//        String tag_json_obj = "json_obj_req";
//        String url = "http://planmything.tech/api/event/" + Event + "/roles/" + Role;
//
////        String url = "http://planmything.tech/api/roles/" + Role;
//
//        final ProgressDialog pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
//                url, null,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("InitReq", response.toString());
//                        JSONObject jsonObject = response;
//                        try {
//
//                            Desp = jsonObject.getString("description");
//                            etdesp.setText(Desp);
//                            peopleNum = jsonObject.getString("quantity needed");
//                            etnum.setText(peopleNum);
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//                        pDialog.hide();
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d("InitReq", "Error: " + error.getMessage());
//                // hide the progress dialog
//                pDialog.hide();
//            }
//        });
//
//// Adding request to request queue
//        AppController.getInstance(this).addToRequestQueue(jsonObjReq, tag_json_obj);
//
//    }


    private void changeData(){


        String tag_json_obj = "json_obj_req";
//        String url = "http://planmything.tech/api/event/" + Event + "/roles/" + Role;
        String url = "http://planmything.tech/api/roles/18";
        Log.d("PutReq", "The url is " + url);
        Log.d("PutReq", "The Pk is " + Role);
        Log.d("PutReq", "The desp is " + Desp);
        Log.d("PutReq", "The quantity is " + peopleNum);

//        String url = "http://planmything.tech/api/roles/" + Role;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();




        Map<String, String> params = new HashMap();

        params.put("description", Desp);
//        params.put("quantity_needed", peopleNum);
//        params.put("estimated budget", Money);

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
                        intent.putExtra("Role", Role);
                        startActivityForResult(intent, 2);// Activity is started with requestCode 2

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PutReq"+ ": ", "Put Error Response code: " + error.networkResponse.statusCode);
                pDialog.hide();

            }
        });
//        {

//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
////                params.put("role", Role);
////                params.put("name", RoleName);
//                params.put("description", Desp);
//                return params;
//            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset = utf-8";
//            }

//        };

// Adding request to request queue
        AppController.getInstance(this).addToRequestQueue(jsonObjReq, tag_json_obj);



    }


}

