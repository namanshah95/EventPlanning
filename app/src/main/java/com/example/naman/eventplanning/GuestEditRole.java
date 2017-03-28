package com.example.naman.eventplanning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class GuestEditRole extends AppCompatActivity {
    String roleName, desp, peopleNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_edit_role);

        final TextView etEventNameEdit = (TextView) findViewById(R.id.etEditEventName);
        final TextView etdesp = (TextView) findViewById(R.id.editdesp);
        final TextView etnum = (TextView) findViewById(R.id.etMoney);
        final TextView people = (TextView) findViewById(R.id.textView4);
        //final Button bEventEditSubmit =  (Button) findViewById(R.id.bEditRole);

        //set text
        Intent intent = getIntent();
        String EventName = intent.getStringExtra("EventName");
        //roleName = EventName;
        etEventNameEdit.setText(EventName);

        etdesp.setText("The description");
        etnum.setText("5");
        people.setText("Alice, Tom, Jack");


        //submit button
        Button btn = (Button)findViewById(R.id.submit);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GuestEditRole.this,EditRole2.class);
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
                Intent intent = new Intent(GuestEditRole.this, GuestEventRole.class);
                intent.putExtra("nameEdit", eventName);
                String s = "yesEdit";
                intent.putExtra("judgeEdit", s);

                setResult(1, intent);
//                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        //add button - to be implemented
        Button addtn = (Button)findViewById(R.id.add);
        //delete buttonm - to be implemented
        Button deltn = (Button)findViewById(R.id.delete);


        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Edit Task");


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

