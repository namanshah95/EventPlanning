package com.example.naman.eventplanning;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Naman on 3/4/2017.
 */

public class MessageDataSource {
    private static final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static MessagesListener addMessagesListener(String convoId, final MessagesCallbacks callbacks){
        MessagesListener listener = new MessagesListener(callbacks);
        mDatabase.child(convoId).addChildEventListener(listener);
        return listener;

    }

    public static void stop(MessagesListener listener){
        mDatabase.removeEventListener(listener);
    }

    public static class MessagesListener implements ChildEventListener {
        private MessagesCallbacks callbacks;
        MessagesListener(MessagesCallbacks callbacks){
            this.callbacks = callbacks;
        }
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String text = dataSnapshot.child("Text").getValue().toString();
            String sender = dataSnapshot.child("Sender").getValue().toString();
            Date datetime = new Date();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat();
                datetime = sdf.parse(dataSnapshot.child("Datetime").getValue().toString());
            }catch (Exception e){

            }
            Message message = new Message(text, sender, datetime);
            if(callbacks != null){
                callbacks.onMessageAdded(message);
            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {


        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError firebaseError) {

        }
    }


    public interface MessagesCallbacks{
        public void onMessageAdded(Message message);
    }
}
