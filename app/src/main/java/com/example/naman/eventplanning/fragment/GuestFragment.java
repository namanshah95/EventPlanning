package com.example.naman.eventplanning.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Entity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.naman.eventplanning.AddEvent;
import com.example.naman.eventplanning.AddGuest;
import com.example.naman.eventplanning.EditBudget;
import com.example.naman.eventplanning.EditRole;
import com.example.naman.eventplanning.LoginActivity;
import com.example.naman.eventplanning.MainActivity;
import com.example.naman.eventplanning.Messenger;
import com.example.naman.eventplanning.R;
import com.example.naman.eventplanning.SwipeDismissListViewTouchListener;
import com.example.naman.eventplanning.AppController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mengdili on 3/11/17.
 */
public class GuestFragment extends Fragment {

    ListView lv;
    Button addBtn;
    ArrayList<String> guest;
    ArrayAdapter<String> adapter;
    String temp;
    String judge, judgeEdit;
    int posEdit;
    String EventName;
    String PK;
    String EntityEmail, EntityName, EntityPK; // other usr's information
    String Event; // current Event

    String myEntityPK; // Curret user's PK

    boolean flag = false;
    boolean exist = false;
    String myEmail, myName; // current user's Email and Name
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ArrayList<String> candidates;
    ArrayList<String> candidatesPK;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_add_guest, null);
        try {
            initView(view);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;


    }

    private void initView(View view) throws JSONException {

        MainActivity activity = (MainActivity) getActivity();
        Event = activity.getEvent();
        myEmail = activity.getEmail();
        myName = activity.getName();
        myEntityPK = activity.getEntity();
        EventName = activity.getEventName();
        Log.d("fragment","Event is" + Event);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        candidates = new ArrayList<>();
        candidatesPK = new ArrayList<>();


        lv = (ListView) view.findViewById(R.id.guestList);
        addBtn = (Button) view.findViewById(R.id.btnAdd);


        //ADAPPTER
        guest = new ArrayList<String>();
        //guest = new ArrayList<String>(Arrays.asList("Alice", "Bob", "Alex", "Grace","Emily", "Cathy", "Tom", "James"));
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, guest){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);


                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 200;
                view.setLayoutParams(params);

                return view;
            }
        };
        lv.setAdapter(adapter);

        getData();


        //Set selected item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posEdit = position;
//                EditRole(position);


            }
        });


        //swipe to delete
        SwipeDismissListViewTouchListener touchListenerNew =
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

                                    Log.d("postion", String.valueOf(position));

//                                    getPK(position);

                                    guest.remove(position);
                                    adapter.notifyDataSetChanged();


                                }

                            }
                        });
        lv.setOnTouchListener(touchListenerNew);

        // Handle events
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getContext(), AddGuest.class);
                startActivityForResult(addIntent, 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                EntityEmail = data.getStringExtra("email");
                Log.d("intent", "Entity's email" + EntityEmail);
                judge = data.getStringExtra("judge");

                if (judge != null && judge.equals("yes")) {
                    try {
                        add();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    judge = "";
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void add() throws JSONException {
        if (!EntityEmail.isEmpty() && EntityEmail.length() > 0) {
           CheckData();


        } else {

            Toast.makeText(getContext(), "!! Nothing to Add", Toast.LENGTH_SHORT).show();
        }

    }


//    private void update(){
//        if(!EventNameEdit.isEmpty() && EventNameEdit.length()> 0){
//            adapter.remove(Event.get(posEdit));
//            adapter.insert(EventNameEdit, posEdit);
//            adapter.notifyDataSetChanged();
//            Toast.makeText(getContext(),"Edited Event", Toast.LENGTH_SHORT).show();
//            EventNameEdit = "";
//        }
//        else{
//            Toast.makeText(getContext(), "!! EventName cannot be null", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void getData(){
        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/event/" + Event + "/guests/?role=-2";
        final ProgressDialog pDialog = new ProgressDialog(getContext());

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


        AppController.getInstance(getContext()).addToRequestQueue(req, tag_json_arry);

    }





    private void postData() throws JSONException {
        String tag_json_obj = "json_obj_req";

        String url = "http://planmything.tech/api/event/" + Event + "/guests/";



        Map<String, String> params = new HashMap();
        params.put("entity", EntityPK);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PostReq", response.toString());

                        adapter.add(EntityName);
                        //Refresh
                        adapter.notifyDataSetChanged();

                        Toast.makeText(getContext(), "Added " + EntityName, Toast.LENGTH_SHORT).show();

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("PostReq", "Error: " + error.getMessage());


            }
        }) {


        };

// Adding request to request queue
        AppController.getInstance(getContext()).addToRequestQueue(jsonObjReq, tag_json_obj);

    }




    private void deleteData() {
        String tag_json_obj = "json_obj_req";
        String url = "http://planmything.tech/api/event/" + Event + "/guests/ + PK";
        Log.d("DeleteReq" + ": ", "Delete PK is" + PK);
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
                            Log.d("DeleteReq" + ": ", "delete Error Response code: " + error.networkResponse.statusCode);

                        }
                    }
                });
//    requesQueue.add(request);
        AppController.getInstance(getContext()).addToRequestQueue(request, tag_json_obj);

    }


    // check whether the user exist
    private void CheckData(){
        String tag_json_arry = "json_array_req";
        EntityEmail =  EntityEmail.replace('.',',');

        String url = "http://planmything.tech/api/entities/?Email=" + EntityEmail;
        Log.d("GetPK", "The url is " + url);

        final ProgressDialog pDialog = new ProgressDialog(getContext());

        pDialog.setMessage("Loading...");
        pDialog.show();



        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GetPk", response.toString());
                        try {
                            EntityName = response.getJSONObject(0).getString("Name");
                            EntityPK = response.getJSONObject(0).getString("entity");
                            Log.d("GetPk", "EntityName is "+ EntityName);
                            exist = true;
                            postData();
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "The person added has not signed up !", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

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

        AppController.getInstance(getContext()).addToRequestQueue(req, tag_json_arry);
    }

    //find name of all guests
    private void findName(){

        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/entities/";
        Log.d("findName", "The url is " + url);

        final ProgressDialog pDialog = new ProgressDialog(getContext());

        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GuestInit", response.toString());
                        if (response != null) {
                            Map<String, String> people = new HashMap();
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
                                adapter.add(people.get(candidatesPK.get(i)));
                                adapter.notifyDataSetChanged();

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


        AppController.getInstance(getContext()).addToRequestQueue(req, tag_json_arry);



    }




}