package com.example.naman.eventplanning;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.naman.eventplanning.fragment.SignInFragment;
import com.example.naman.eventplanning.fragment.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements SignInFragment.OnFragmentInteractionListener, SignUpFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignInFragment signin_fragment = new SignInFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, signin_fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
