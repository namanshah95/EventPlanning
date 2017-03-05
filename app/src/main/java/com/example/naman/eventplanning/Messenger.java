package com.example.naman.eventplanning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;


public class Messenger extends AppCompatActivity {
    private DatabaseReference mDatabase;

    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSendButton = (Button) findViewById(R.id.send_message);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text_box = (EditText) findViewById(R.id.new_message);
                String text = text_box.getText().toString();
                String sender = "TBD"; // TODO: Replace with mAuth.getUID()
                Message msg = new Message(text, sender);
                if (!text.equals("")) {
                    saveMessage(msg);
                }
                text_box.getText().clear();
            }
        });
    }

    private void saveMessage(Message msg) {
        // TODO: Replace all sample_event_id with current event id
        String key = mDatabase.child("sample_event_id").push().getKey();
        mDatabase.child("sample_event_id").child(key).child("Text").setValue(msg.getText());
        mDatabase.child("sample_event_id").child(key).child("Sender").setValue(msg.getSender());
        SimpleDateFormat sdf = new SimpleDateFormat();
        mDatabase.child("sample_event_id").child(key).child("Datetime").setValue(sdf.format(msg.getDatetime()));
    }
}
