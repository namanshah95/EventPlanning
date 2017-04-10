package com.example.naman.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.content.Intent;


import com.example.naman.eventplanning.fragment.EventroleFragment;
import com.example.naman.eventplanning.fragment.BudgetFragment;
import com.example.naman.eventplanning.fragment.GuestFragment;
import com.example.naman.eventplanning.fragment.MessageFragment;

import com.example.naman.eventplanning.view.viewpager.CustomViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private CustomViewPager viewPager;
    private ViewPagerAdapter adapter;
    private RadioGroup rgTab;
    private int lastPos = 0;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavMenu;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String userName;
    String userEmail;
    String Event; // pr of current event
    String Entity; // pk of current user
    String EventName; // name of current Event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        userName = intent.getStringExtra("Name");
        userEmail = intent.getStringExtra("Email");
        Event = intent.getStringExtra("Event");
        Entity = intent.getStringExtra("Entity");
        EventName = intent.getStringExtra("EventName");
        String s1 = intent.getStringExtra("Check");

//        if(s1 != null && s1.equals("EventRole"))
//        {
//            s1 = "";
//            Fragment fragment = new EventroleFragment();
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.activity_main, fragment);
//            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
//
//        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };




        setContentView(R.layout.activity_main);

        //set navigation bar
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_event_role);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set actionbar title
        getSupportActionBar().setTitle("Event Planning");
        getSupportActionBar().setSubtitle("Task Manage");

        viewPager = (CustomViewPager) findViewById(R.id.viewPager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);


        rgTab = (RadioGroup) findViewById(R.id.rg_tab);

        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.task_manage) {
                    getSupportActionBar().setSubtitle("Task Manage");
                    lastPos = 1;
                    viewPager.setCurrentItem(lastPos, true);
                } else if (checkedId == R.id.message) {
                    getSupportActionBar().setSubtitle("Message");
                    lastPos = 2;
                    viewPager.setCurrentItem(lastPos, true);
                } else if (checkedId == R.id.budget) {
                    getSupportActionBar().setSubtitle("budget");
                    lastPos = 3;
                    viewPager.setCurrentItem(lastPos, true);
                } else if (checkedId == R.id.guest){
                    getSupportActionBar().setSubtitle("Guest");
                    lastPos = 0;
                    viewPager.setCurrentItem(lastPos, true);
                }
            }
        });

        if(s1 != null && s1.equals("EventRole")){
            Log.d("viewPage", "work here");
            getSupportActionBar().setSubtitle("Task Manage");
            viewPager.setCurrentItem(1,true);
            rgTab.check(R.id.task_manage);
        }
        else {
            viewPager.setCurrentItem(lastPos, true);
        }


        mNavMenu = (NavigationView) findViewById(R.id.nav_menu);
        mNavMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                System.out.println("Nav Item Selected");
                if (item.getItemId() == R.id.SignOut) {
                    mAuth.signOut();
                }

                if(item.getItemId() == R.id.Events){
                    Intent intent = new Intent(MainActivity.this, EventActivity.class);
                    startActivity(intent);
                }


                return true;
            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getEvent() {

        return Event;
    }
    public String getEntity() {

        return Entity;
    }
    public String getName() {

        return userName;
    }
    public String getEmail() {

        return userEmail;
    }
    public String getEventName() {

        return EventName;
    }






    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private GuestFragment tab1Fragment;
        private EventroleFragment tab2Fragment;
        private MessageFragment tab3Fragment;
        private BudgetFragment tab4Fragment;


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:// tab1
                    if (tab1Fragment == null){
                        tab1Fragment = new GuestFragment();
                    }
                    return tab1Fragment;

                case 1:// tab2
                    if (tab2Fragment == null) {
                        tab2Fragment = new EventroleFragment();
                    }

                    return tab2Fragment;
                case 2:// tab3
                    if (tab3Fragment == null) {
                        tab3Fragment = new MessageFragment();
                    }

                    return tab3Fragment;
                case 3:// tab4
                    if (tab4Fragment == null) {
                        tab4Fragment = new BudgetFragment();
                    }
                    return tab4Fragment;

                default:
                    break;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }


}
