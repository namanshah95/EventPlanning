package com.example.naman.eventplanning;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.example.naman.eventplanning.fragment.EventroleFragment;
import com.example.naman.eventplanning.fragment.BudgetFragment;
import com.example.naman.eventplanning.fragment.MessageFragment;
import com.example.naman.eventplanning.view.viewpager.CustomViewPager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private CustomViewPager viewPager;
    private ViewPagerAdapter adapter;
    private RadioGroup rgTab;
    private int lastPos = 0;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavMenu;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    lastPos = 0;
                    viewPager.setCurrentItem(lastPos, true);
                } else if (checkedId == R.id.message) {
                    getSupportActionBar().setSubtitle("Message");
                    lastPos = 1;
                    viewPager.setCurrentItem(lastPos, true);
                } else if (checkedId == R.id.budget) {
                    getSupportActionBar().setSubtitle("Budget");
                    lastPos = 2;
                    viewPager.setCurrentItem(lastPos, true);
                }
            }
        });
        viewPager.setCurrentItem(lastPos, true);

        mNavMenu = (NavigationView) findViewById(R.id.nav_menu);
        mNavMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                System.out.println("Nav Item Selected");
                if (item.getItemId() == R.id.SignOut) {
                    mAuth.signOut();
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

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private EventroleFragment tab1Fragment;
        private MessageFragment tab2Fragment;
        private BudgetFragment tab3Fragment;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:// tab1
                    if (tab1Fragment == null) {
                        tab1Fragment = new EventroleFragment();
                    }
                    return tab1Fragment;
                case 1:// tab2
                    if (tab2Fragment == null) {
                        tab2Fragment = new MessageFragment();
                    }
                    return tab2Fragment;
                case 2:// tab3
                    if (tab3Fragment == null) {
                        tab3Fragment = new BudgetFragment();
                    }
                    return tab3Fragment;
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
            return 3;
        }
    }


}

