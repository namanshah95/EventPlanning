package com.example.naman.eventplanning;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class EditRole extends AppCompatActivity {
    String roleName, desp, peopleNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_role);

        final EditText etEventNameEdit = (EditText) findViewById(R.id.etEditEventName);
        final EditText etdesp = (EditText) findViewById(R.id.editdesp);
        final EditText etnum = (EditText) findViewById(R.id.editnum);
        //final Button bEventEditSubmit =  (Button) findViewById(R.id.bEditRole);

        Intent intent = getIntent();
        String EventName = intent.getStringExtra("EventName");
        //roleName = EventName;
        etEventNameEdit.setText(EventName);

        /*bEventEditSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String eventName = etEventNameEdit.getText().toString();
                Intent intent = getIntent();
                intent.putExtra("nameEdit", eventName);
                String s = "yesEdit";
                intent.putExtra("judgeEdit", s);

                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });*/


        //next button
        Button btn = (Button)findViewById(R.id.next);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditRole.this,EditRole2.class);
                startActivityForResult(intent, 2);// Activity is started with requestCode 2
//                startActivity(new Intent(EditRole.this, EditRole2.class));
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
//            String message=data.getStringExtra("MESSAGE");
//            textView1.setText(message);
        }
    }


}

