package com.example.naman.eventplanning.fragment;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

public class MessageFragment extends Fragment {

    private DatabaseReference mDatabase;

    private ArrayList<Message> messageList;

    private MessageDataSource.MessagesListener listener;

    private ListView mMessageList;
    private Button mSendButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_messenger, null);

        return view;
    }
}

