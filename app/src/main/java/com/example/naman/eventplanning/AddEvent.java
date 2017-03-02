package com.example.naman.eventplanning;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        final EditText etEventName = (EditText) findViewById(R.id.etEventName);
        final Button bEventSubmit =  (Button) findViewById(R.id.bEventSubmit);

        bEventSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String eventName = etEventName.getText().toString();
                Intent intent = getIntent();
                intent.putExtra("name", eventName);
                String s = "yes";
                intent.putExtra("judge", s);

                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });


    }

}
