package com.example.naman.eventplanning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditBudget extends AppCompatActivity {

    private String[] arrText =
            new String[]{"Estimated Expense","Alice","Bob","Cathy"
                    ,"Tom","James"};
    private String[] arrTemp;
    private int totalExpense = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        arrTemp = new String[arrText.length];

        MyListAdapter myListAdapter = new MyListAdapter();
        ListView listView = (ListView) findViewById(R.id.peopleList);
        listView.setAdapter(myListAdapter);

        Button submit =  (Button) findViewById(R.id.submit);
        final TextView tv = (TextView)findViewById(R.id.textView2);

        //click submit button, total expense shows up
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                totalExpense = 0;
                for(int i = 1; i < arrTemp.length; i++){
                    if(!arrTemp[i].equals("")) {
                        totalExpense += Integer.parseInt(arrTemp[i]);
                    }
                    tv.setText(Integer.toString(totalExpense));
                }

                //show alert if total expense larger than estimated expense
                if(arrTemp[0].equals("")){
                    arrTemp[0] = "0";
                }
                if(totalExpense > Integer.parseInt(arrTemp[0])){
                    Toast.makeText(getApplicationContext(),
                            "Total expense is larger than estimated!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Record Saved", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //back button
        Button backtn = (Button)findViewById(R.id.back);

        backtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(EditRole.this, EventRole.class));
                //String eventName = etEventNameEdit.getText().toString();
                Intent intent = new Intent(EditBudget.this, Budget.class);
                //intent.putExtra("nameEdit", eventName);
                //String s = "yesEdit";
                //intent.putExtra("judgeEdit", s);

                setResult(1, intent);
//                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        //set actionbar title
        getSupportActionBar().setTitle("BUDGET MANAGER");
        getSupportActionBar().setSubtitle("Budget Record");
    }

    private class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(arrText != null && arrText.length != 0){
                return arrText.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arrText[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //ViewHolder holder = null;
            final ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                LayoutInflater inflater = EditBudget.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.budget_people_list, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.editText1 = (EditText) convertView.findViewById(R.id.editText1);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.ref = position;

            holder.textView1.setText(arrText[position]);
            holder.editText1.setText(arrTemp[position]);
            holder.editText1.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    arrTemp[holder.ref] = arg0.toString();
                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView textView1;
            EditText editText1;
            int ref;
        }


    }
}
