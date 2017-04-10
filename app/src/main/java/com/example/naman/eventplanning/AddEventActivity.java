package com.example.naman.eventplanning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by mengdili on 3/25/17.
 */

public class AddEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        final EditText etEventName = (EditText) findViewById(R.id.etEventName1);
        final Button bEventSubmit =  (Button) findViewById(R.id.bEventSubmit1);

        bEventSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String eventName = etEventName.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("name", eventName);
                String s = "yes";
                intent.putExtra("judge", s);

                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });

        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Add Event");
    }

}
