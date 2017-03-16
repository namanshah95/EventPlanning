package com.example.naman.eventplanning;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class Messenger extends AppCompatActivity implements MessageDataSource.MessagesCallbacks{
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ArrayList<Message> messageList;
    private MessageAdapter adapter;
    private MessageDataSource.MessagesListener listener;

    private ListView mMessageList;
    private Button mSendButton;

    private String senderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mMessageList = (ListView) findViewById(R.id.message_list);
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        mMessageList.setAdapter(adapter);

        mSendButton = (Button) findViewById(R.id.send_message);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text_box = (EditText) findViewById(R.id.new_message);
                String text = text_box.getText().toString();
                String sender = mAuth.getCurrentUser().getUid();
                Message msg = new Message(text, sender);
                if (!text.equals("")) {
                    saveMessage(msg);
                }
                text_box.getText().clear();
            }
        });

        listener = MessageDataSource.addMessagesListener("sample_event_id", this);

        //set actionbar title
        getSupportActionBar().setTitle("MESSAGES");
    }

    private void saveMessage(Message msg) {
        // TODO: Replace all sample_event_id with current event id
        String key = mDatabase.child("messages").child("sample_event_id").push().getKey();
        mDatabase.child("messages").child("sample_event_id").child(key).setValue(new MessageHelper(msg));
    }

    @Override
    public void onMessageAdded(Message message) {
        messageList.add(message);
        adapter.notifyDataSetChanged();
    }


    private class MessageAdapter extends ArrayAdapter<Message> {
        MessageAdapter(ArrayList<Message> messages) {
            super(Messenger.this, R.layout.message_item, R.id.message, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            Message message = getItem(position);

            TextView nameView = (TextView)convertView.findViewById(R.id.message);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();

            int sdk = Build.VERSION.SDK_INT;
            if (message.getSender().equals(mAuth.getCurrentUser().getUid())){
                nameView.setText(message.getText());
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getDrawable(R.drawable.bubble_right_green));
                } else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_right_green));
                }
                layoutParams.gravity = Gravity.RIGHT;
            }else{
                nameView.setTextColor(Color.BLACK);
                setSenderName(message);
                nameView.setText(senderName + ":\n" + message.getText());
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
                } else{
                    nameView.setBackgroundDrawable(getDrawable(R.drawable.bubble_left_gray));
                }
                layoutParams.gravity = Gravity.LEFT;
            }

            nameView.setLayoutParams(layoutParams);
            return convertView;
        }
    }

    private void setSenderName(Message message) {
        mDatabase.child("users").child(message.getSender()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                senderName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                senderName = "ERROR";
            }
        });
    }

    private class MessageHelper {
        public String Text;
        public String Sender;
        public String Datetime;

        public MessageHelper() {

        }

        public MessageHelper(Message msg) {
            this.Text = msg.getText();
            this.Sender = msg.getSender();
            SimpleDateFormat sdf = new SimpleDateFormat();
            this.Datetime = sdf.format(msg.getDatetime());
        }
    }
}
