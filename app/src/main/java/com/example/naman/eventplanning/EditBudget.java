package com.example.naman.eventplanning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class EditBudget extends AppCompatActivity {

    String Needed_Role;
    String Event;
    String Role;
    String EstimedMoney;
    String EventName;
    String myEmail,myEntityPK,myName;
    String Entity;
    MyListAdapter myListAdapter;
    ArrayList<String> candidates;
    ArrayList<String> candidatesPK;
    ArrayList<String> budget;


    private String[] arrText =
            new String[]{"Alice","Bob","Cathy"
                    };

    private String[] arrTemp;
    private int totalExpense = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);
        final TextView estMoney = (TextView)findViewById(R.id.textView11);


        Intent intent = getIntent();
        Needed_Role = intent.getStringExtra("Needed_Role");
        Event = intent.getStringExtra("Event");
        Role = intent.getStringExtra("Role");
        EstimedMoney = intent.getStringExtra("Money");
        EventName = intent.getStringExtra("Money");
        myEmail= intent.getStringExtra("myEmail");
        myName = intent.getStringExtra("myName");
        myEntityPK = intent.getStringExtra("myEntityPK");
        Log.d("Role", "the role is" + Role);
        Log.d("money", "the money is" + EstimedMoney);


        estMoney.setText(EstimedMoney);
        candidates = new ArrayList<>();
        candidatesPK = new ArrayList<>();
        budget = new ArrayList<>();

        getData();


//
//        arrTemp = new String[candidates.size()];
//
//        myListAdapter = new MyListAdapter();
//        ListView listView = (ListView) findViewById(R.id.peopleList);
//        listView.setAdapter(myListAdapter);

        Button submit =  (Button) findViewById(R.id.submit);
        final TextView tv = (TextView)findViewById(R.id.textView10);

        //click submit button, total expense shows up
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                totalExpense = 0;
                for(int i = 0; i < arrTemp.length; i++){
                    if(!arrTemp[i].equals("")) {
                        totalExpense += (int) Double.parseDouble(arrTemp[i]);
                        ChangeBudget(candidatesPK.get(i), arrTemp[i]);
                    }
                    tv.setText(Integer.toString(totalExpense));
                }

                //show alert if total expense larger than estimated expense
                if(totalExpense > Float.parseFloat(EstimedMoney)){
                    Toast.makeText(getApplicationContext(),
                            "Recoed Saved and Total expense is larger than estimated!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Record Saved", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(EditBudget.this, MainActivity.class);
                intent.putExtra("Event", Event);
                intent.putExtra("myEmail", myEmail);
                intent.putExtra("myName", myName);
                intent.putExtra("myEntityPK", myEntityPK);
                intent.putExtra("EventNme", EventName);
                startActivity(intent);

            }
        });

        //back button
        Button backtn = (Button)findViewById(R.id.back);

        backtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//
//                setResult(1, intent);
//                setResult(Activity.RESULT_OK, intent);
              finish();


//
//                Intent intent = new Intent(EditBudget.this, MainActivity.class);
//
//                intent.putExtra("Check","budget");
//                intent.putExtra("Event", Event);
//                intent.putExtra("myEmail", myEmail);
//                intent.putExtra("myName", myName);
//                intent.putExtra("myEntityPK", myEntityPK);
//                intent.putExtra("EventNme", EventName);
//                startActivity(intent);



            }
        });

        //set actionbar title
        getSupportActionBar().setTitle("BUDGET MANAGER");
        getSupportActionBar().setSubtitle("budget Record");
    }


    private void getData(){
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
                                    candidatesPK.add(jsonObject.getString("entity"));
                                    Log.d("budget", "candidatesPk is "+ jsonObject.getString("entity"));
                                    if (jsonObject.getString("estimated_budget").equals("null")){
                                        budget.add("0.0");
                                    }
                                    else{
                                        budget.add(jsonObject.getString("estimated_budget"));
                                    }
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
                        Log.d("BudgetInit", response.toString());
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
                                Log.d("BudgetInit", "candidates is "+ candidates.get(i));

                            }

                            arrTemp = new String[candidates.size()];
                            arrTemp = budget.toArray(arrTemp);
//                            Log.d("budget", "ArryTemp is " + arrTemp[0] + " " + arrTemp[1] +" " + arrTemp[2] + " " + arrTemp[3]);

                            myListAdapter = new MyListAdapter();
                            ListView listView = (ListView) findViewById(R.id.peopleList);
                            listView.setAdapter(myListAdapter);



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

    private void ChangeBudget(String Entity, String money){

        String tag_json_obj = "json_obj_req";
        String url = "http://planmything.tech/api/event/" + Event + "/entities/" + Entity + "/roles/"+ Needed_Role;

        Log.d("ChangeBudget", "The url is " + url);


        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();




        Map<String, String> params = new HashMap();

        params.put("estimated_budget", money);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PutReq", response.toString());
                        pDialog.hide();

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








    private class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(candidates != null && candidates.size() != 0){
                return candidates.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return candidates.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //ViewHolder holder = null;
            final ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                LayoutInflater inflater = EditBudget.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.budget_people_list, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.editText1 = (EditText) convertView.findViewById(R.id.editText1);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.ref = position;
            if (candidates != null) {

                holder.textView1.setText(candidates.get(position));
                holder.editText1.setText(arrTemp[position]);
                holder.editText1.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        arrTemp[holder.ref] = arg0.toString();
                    }
                });
            }

            return convertView;

        }

        private class ViewHolder {
            TextView textView1;
            EditText editText1;
            int ref;
        }


    }



}
