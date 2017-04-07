package com.example.naman.eventplanning;

/**
 * Created by mengdili on 3/23/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.naman.eventplanning.R;


public class home_activity extends AppCompatActivity {


    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayout());
        ButterKnife.bind(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Intent intent = new Intent();

                        intent.setClass(home_activity.this, EventActivity.class);

                    startActivity(intent);
                    finish();
                } catch (Exception e) {// Interrupted
                    e.printStackTrace();
                }
            }
        }).start();
    }


    protected int getContentLayout() {
        return R.layout.activity_firstpage;
    }

}
