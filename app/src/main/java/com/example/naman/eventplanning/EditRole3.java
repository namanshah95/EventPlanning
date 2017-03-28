package com.example.naman.eventplanning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.example.naman.eventplanning.fragment.EventroleFragment;
import com.example.naman.eventplanning.fragment.GuestFragment;

public class EditRole3 extends AppCompatActivity {

    String[] resultArr;
    String[] resultPK;

    String Role;
    String Entity;
    String Event ;
    String EventName;
    String myEmail, myName,myEntityPK;

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

        resultArr = intent.getStringArrayExtra("selectedItems");
        ListView lv = (ListView) findViewById(R.id.outputList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, resultArr);
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
                Toast.makeText(getApplicationContext(),
                        "Sumbit Sucessfully", Toast.LENGTH_SHORT).show();
                Log.d("Intent", "Event is " + Event);
                Log.d("Intent", "Role is " + Role);



                Intent intent = new Intent(EditRole3.this, MainActivity.class);
                intent.putExtra("Event", Event);
                intent.putExtra("myEmail", myEmail);
                intent.putExtra("myName", myName);
                intent.putExtra("myEntityPK", myEntityPK);
                intent.putExtra("EventNme", EventName);
                startActivityForResult(intent,1);

            }


        });

        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Selected People");
    }

    // POST /event/{event}/entities/{entity}/roles/

    private void postData(){
        String tag_json_obj = "json_obj_req";

        String url = "http://planmything.tech/api/event/" + Event + "/entities/" + Entity + "/roles/" ;

        final ProgressDialog pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading...");
//        pDialog.show();


        Map<String, String> params = new HashMap();
        params.put("role", Role);
//        params.put("needed_role_name", RoleName);


        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PostReq", response.toString());
//                        pDialog.hide();

//                        Intent intent = new Intent(EditRole3.this, EventRole.class);
//                        startActivityForResult(intent,1);


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

}