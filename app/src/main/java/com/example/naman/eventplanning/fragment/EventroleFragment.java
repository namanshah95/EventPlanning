package com.example.naman.eventplanning.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.naman.eventplanning.EditRole;
import com.example.naman.eventplanning.Messenger;
import com.example.naman.eventplanning.R;
import com.example.naman.eventplanning.SwipeDismissListViewTouchListener;
import com.example.naman.eventplanning.AppController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mengdili on 3/11/17.
 */
public class EventroleFragment extends Fragment {

    ListView lv;
    Button addBtn;
    ArrayList<String> Roles = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String RoleName, RoleNameEdit;
    String judge,judgeEdit;
    int posEdit;
    String PK, desc, p_number, money;
    boolean flag = false;
    boolean exist = false;
    String Event = "1" ;
    String EventName;
    String needed_role;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_event_role, null);
        try {
            initView(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View view) throws JSONException {


        lv = (ListView) view.findViewById(R.id.evenList);
        addBtn = (Button) view.findViewById(R.id.btnAdd);

        //ADAPPTER
        Roles = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Roles);
        lv.setAdapter(adapter);

        getData();




        //Set selected item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posEdit = position;
                EditRole(position);

//                Intent editIntent = new Intent(getContext(), EditRole.class );
//                    editIntent.putExtra("RoleName", Roles.get(position));
//                    editIntent.putExtra("Role","1");
//                    startActivityForResult(editIntent,1);



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

                                    Log.d("postion", String.valueOf(position));

                                    getPK(position);

                                    Roles.remove(position);
                                    adapter.notifyDataSetChanged();



                                }

                            }
                        });
        lv.setOnTouchListener(touchListener);

        // Handle events
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getContext(), AddEvent.class );
                startActivityForResult(addIntent,1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                RoleName = data.getStringExtra("name");
                judge = data.getStringExtra("judge");
                RoleNameEdit = data.getStringExtra("nameEdit");
                judgeEdit = data.getStringExtra("judgeEdit");
                if(judge != null && judge.equals("yes")){
                    try {
                        add();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    }//onActivityResult

    private void add() throws JSONException {
        if(!RoleName.isEmpty() && RoleName.length()> 0){

            findData();
            //Add
            adapter.add(RoleName);
            //Refresh
            adapter.notifyDataSetChanged();

            Toast.makeText(getContext(),"Added " + RoleName, Toast.LENGTH_SHORT).show();
//            RoleName = "";
        }
        else{

            Toast.makeText(getContext(), "!! Nothing to Add", Toast.LENGTH_SHORT).show();
        }

    }


    private void update(){
        if(!RoleNameEdit.isEmpty() && RoleNameEdit.length()> 0){
            adapter.remove(Roles.get(posEdit));
            adapter.insert(RoleNameEdit, posEdit);
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"Edited Roles", Toast.LENGTH_SHORT).show();
            RoleNameEdit = "";
        }
        else{
            Toast.makeText(getContext(), "!! RoleName cannot be null", Toast.LENGTH_SHORT).show();
        }
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
                                    adapter.add(name);
                                    adapter.notifyDataSetChanged();
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

// Adding request to request queue
        AppController.getInstance(getContext()).addToRequestQueue(req, tag_json_arry);
    }



    private void findData() {
        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/roles/";

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("findData", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = response.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String name = null;
                            try {
                                Log.d("findData", "works here");
                                name = jsonObject.getString("name");
                                Log.d("findData", "name is " + name);
                                if (name.equals(RoleName)){
                                    Log.d("findData", "RoleName is " + RoleName);
                                    exist = true;
                                    PK = jsonObject.getString("role");
                                    Log.d("findData","PK is "+ PK );
                                    break;


                                }
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }


                        }
                        if (exist == true) {
                            try {
                                postData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            addData();
                        }

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

    // add role to roles
    private void addData(){

        String tag_json_obj = "json_obj_req";

        String url = "http://planmything.tech/api/roles/";

        final ProgressDialog pDialog = new ProgressDialog(getContext());
//        pDialog.setMessage("Loading...");
//        pDialog.show();


        Map<String, String> params = new HashMap();
        params.put("name", RoleName);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PostReq", response.toString());
                        try {
                            PK = response.getString("role");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        pDialog.hide();
                        try {
                            postData();
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
        AppController.getInstance(getContext()).addToRequestQueue(jsonObjReq, tag_json_obj);

    }









    // assign the role to event
    private void postData() throws JSONException {
        exist = false;
        String tag_json_obj = "json_obj_req";

       String url = "http://planmything.tech/api/event/" + Event + "/roles/";

        final ProgressDialog pDialog = new ProgressDialog(getContext());
//        pDialog.setMessage("Loading...");
//        pDialog.show();


        Map<String, String> params = new HashMap();
        params.put("needed_role", PK);
//        params.put("needed_role_name", RoleName);


        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PostReq", response.toString());
//                        pDialog.hide();

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
        AppController.getInstance(getContext()).addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    private void deleteData() {
        String tag_json_obj = "json_obj_req";
//        String url = "http://planmything.tech/api/roles/" + PK;
        String url = "http://planmything.tech/api/event/" + Event + "/roles/" + PK;
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
                            Log.d("DeleteReq"+ ": ", "delete Error Response code: " + error.networkResponse.statusCode);

                        }
                    }
                });
//    requesQueue.add(request);
        AppController.getInstance(getContext()).addToRequestQueue(request, tag_json_obj);

    }

    private void EditRole(int position){
        final String RoleName = Roles.get(position);

        String tag_json_arry = "json_array_req";
        Log.d("RoleName", "is " + RoleName);

//        String url = "http://planmything.tech/api/roles/";
        String url = "http://planmything.tech/api/event/" + Event + "/roles/";
        final ProgressDialog pDialog = new ProgressDialog(getContext());

        pDialog.setMessage("Loading...");
        pDialog.show();


        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("EditReq", response.toString());
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
                                if (name.equals(RoleName)){
                                    desc = jsonObject.getString("description");
                                    p_number = jsonObject.getString("quantity_needed");
                                    PK = jsonObject.getString("event_needed_role");
//                                    String Desp = jsonObject.getString("description");\
                                    Log.d("EditReq","PK is "+ PK );
                                    break;


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                        pDialog.hide();
                        flag = true;
                        Intent editIntent = new Intent(getContext(), EditRole.class );
                        editIntent.putExtra("RoleName", RoleName);
                        editIntent.putExtra("Role",PK);
                        editIntent.putExtra("Descripition", desc);
                        editIntent.putExtra("PeopleNumber", p_number);
                        editIntent.putExtra("Money", money);
                        editIntent.putExtra("Event", Event);


                        startActivityForResult(editIntent,1);
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

        final String RoleName = Roles.get(position);
        String tag_json_arry = "json_array_req";

        String url = "http://planmything.tech/api/roles/";

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
                                name = jsonObject.getString("name");
                                if (name.equals(RoleName)){
                                    PK = jsonObject.getString("role");
//                                    String Desp = jsonObject.getString("description");\
                                    Log.d("EditReq","PK is "+ PK );
                                    deleteData();
                                    break;


                                }
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }


                        }

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
