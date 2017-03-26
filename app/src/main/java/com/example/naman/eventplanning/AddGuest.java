package com.example.naman.eventplanning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddGuest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest2);

        final EditText etGuestEmail = (EditText) findViewById(R.id.etGuestEmail);
        final Button bEventSubmit =  (Button) findViewById(R.id.bGuestSubmit);

        bEventSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String guestEmail = etGuestEmail.getText().toString();
                Intent intent = getIntent();
                intent.putExtra("email", guestEmail);
                String s = "yes";
                intent.putExtra("judge", s);

                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });

        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Add Role");
    }

}
