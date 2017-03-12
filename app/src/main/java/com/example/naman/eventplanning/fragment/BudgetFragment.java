package com.example.naman.eventplanning.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.naman.eventplanning.R;

/**
 * Created by mengdili on 3/10/17.
 */

public class BudgetFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_event_role, container, false);
        return v;
    }
}
