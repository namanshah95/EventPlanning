package com.example.naman.eventplanning.fragment;

import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.naman.eventplanning.Message;
import com.example.naman.eventplanning.MessageDataSource;
import com.example.naman.eventplanning.Messenger;
import com.example.naman.eventplanning.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by mengdili on 3/10/17.
 */

public class MessageFragment extends Fragment implements MessageDataSource.MessagesCallbacks {

    private DatabaseReference mDatabase;

    private ArrayList<Message> messageList;
    private MessageAdapter adapter;
    private MessageDataSource.MessagesListener listener;

    private ListView mMessageList;
    private Button mSendButton;
    private EditText text_box;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_messenger, null);
        initView(view);
        return view;
    }

    private void initView(View view) {


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mMessageList = (ListView) view.findViewById(R.id.message_list);
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        mMessageList.setAdapter(adapter);
        text_box = (EditText) view.findViewById(R.id.new_message);

        mSendButton = (Button) view.findViewById(R.id.send_message);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditText text_box = (EditText) view.findViewById(R.id.new_message);
                String text = text_box.getText().toString();
                String sender = "TBD"; // TODO: Replace with mAuth.getUID()
                Message msg = new Message(text, sender);
                if (!text.equals("")) {
                    saveMessage(msg);
                }
                text_box.getText().clear();
            }
        });

        listener = MessageDataSource.addMessagesListener("sample_event_id", this);


    }

    private void saveMessage(Message msg) {
        // TODO: Replace all sample_event_id with current event id
        String key = mDatabase.child("sample_event_id").push().getKey();
        mDatabase.child("sample_event_id").child(key).setValue(new MessageHelper(msg));
    }

    @Override
    public void onMessageAdded(Message message) {
        messageList.add(message);
        adapter.notifyDataSetChanged();
    }



    private class MessageAdapter extends ArrayAdapter<Message> {
        MessageAdapter(ArrayList<Message> messages) {
            super(getActivity(), R.layout.message_item, R.id.message, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            Message message = getItem(position);

            TextView nameView = (TextView)convertView.findViewById(R.id.message);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();

            int sdk = Build.VERSION.SDK_INT;
            if (message.getSender().equals("TBD")){
                nameView.setText(message.getText());
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackgroundResource(R.drawable.bubble_right_green);
                } else{
                    nameView.setBackgroundResource(R.drawable.bubble_right_green);
                }
                layoutParams.gravity = Gravity.RIGHT;
            }else{
                nameView.setText(message.getSender() + ":\n" + message.getText());
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) {
                    nameView.setBackgroundResource(R.drawable.bubble_left_gray);
                } else{
                    nameView.setBackgroundResource(R.drawable.bubble_left_gray);
                }
                layoutParams.gravity = Gravity.LEFT;
            }

            nameView.setLayoutParams(layoutParams);
            return convertView;
        }
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



