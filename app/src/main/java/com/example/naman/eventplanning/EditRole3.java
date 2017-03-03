package com.example.naman.eventplanning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class EditRole3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_role3);

        Bundle b = getIntent().getExtras();
        String[] resultArr = b.getStringArray("selectedItems");
        ListView lv = (ListView) findViewById(R.id.outputList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, resultArr);
        lv.setAdapter(adapter);

        //back button
        Button backtn = (Button)findViewById(R.id.back);

        backtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(EditRole3.this, EditRole2.class));
                Intent intent = new Intent();
                setResult(3,intent);
                finish();
            }
        });

        //set actionbar title
        getSupportActionBar().setTitle("TASK MANAGER");
        getSupportActionBar().setSubtitle("Selected People");
    }
}