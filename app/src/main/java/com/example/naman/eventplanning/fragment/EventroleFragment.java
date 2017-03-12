package com.example.naman.eventplanning.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.naman.eventplanning.AddEvent;
import com.example.naman.eventplanning.EditRole;
import com.example.naman.eventplanning.EventRole;
import com.example.naman.eventplanning.Messenger;
import com.example.naman.eventplanning.R;
import com.example.naman.eventplanning.SwipeDismissListViewTouchListener;

import java.util.ArrayList;

/**
 * Created by mengdili on 3/11/17.
 */
public class EventroleFragment extends Fragment {

    ListView lv;
    Button addBtn;
    ArrayList<String> Event = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String EventName, EventNameEdit;
    String judge,judgeEdit;
    int posEdit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_event_role, null);
        initView(view);
        return view;
    }

    private void initView(View view){

        lv = (ListView) view.findViewById(R.id.evenList);
        addBtn = (Button) view.findViewById(R.id.btnAdd);

        //ADAPPTER
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Event);
        lv.setAdapter(adapter);

        //Set selected item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                view.setSelected(true);
//                lv.setItemChecked(1,true);
//                Object listItem = lv.getItemAtPosition(position);
                posEdit = position;
                Intent editIntent = new Intent(getContext(), EditRole.class );
                editIntent.putExtra("EventName", Event.get(position));
                startActivityForResult(editIntent,1);

            }
        });

//////////////////////////////////////////////////////
        // TEMPORARY
        Button msgBtn = (Button) view.findViewById(R.id.btnMsg);
        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Messenger.class);
                startActivity(intent);
            }
        });

        //swipe to delete
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    Event.remove(position);
                                    adapter.notifyDataSetChanged();

                                }

                            }
                        });
        lv.setOnTouchListener(touchListener);

        // Handle events
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getContext(), AddEvent.class );
                startActivityForResult(addIntent,1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                EventName = data.getStringExtra("name");
                judge = data.getStringExtra("judge");
                EventNameEdit = data.getStringExtra("nameEdit");
                judgeEdit = data.getStringExtra("judgeEdit");
                if(judge != null && judge.equals("yes")){
                    add();
                    judge = "";
                }
                if(judgeEdit != null && judgeEdit.equals("yesEdit")){
                    update();
                    judgeEdit = "";
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void add(){
        if(!EventName.isEmpty() && EventName.length()> 0){
            //Add
            adapter.add(EventName);
            //Refresh
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"Added " + EventName, Toast.LENGTH_SHORT).show();
            EventName = "";
        }
        else{
            Toast.makeText(getContext(), "!! Nothing to Add", Toast.LENGTH_SHORT).show();
        }

    }

    private void delete(){
        int pos= lv.getCheckedItemPosition();
        if (pos > -1) {
            //remove
            adapter.remove(Event.get(pos));
            //refresh
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Deleted ", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "!! Nothing to Delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void update(){
        if(!EventNameEdit.isEmpty() && EventNameEdit.length()> 0){
            adapter.remove(Event.get(posEdit));
            adapter.insert(EventNameEdit, posEdit);
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"Edited Event", Toast.LENGTH_SHORT).show();
            EventNameEdit = "";
        }
        else{
            Toast.makeText(getContext(), "!! EventName cannot be null", Toast.LENGTH_SHORT).show();
        }
    }

}
