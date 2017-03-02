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
                startActivity(new Intent(EditRole.this, EditRole2.class));
            }
        });

        //back button
        Button backtn = (Button)findViewById(R.id.back);

        backtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(EditRole.this, EventRole.class));
                String eventName = etEventNameEdit.getText().toString();
                Intent intent = getIntent();
                intent.putExtra("nameEdit", eventName);
                String s = "yesEdit";
                intent.putExtra("judgeEdit", s);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


    }
}

