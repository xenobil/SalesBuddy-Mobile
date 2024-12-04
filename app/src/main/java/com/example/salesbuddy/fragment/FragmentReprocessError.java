package com.example.salesbuddy.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.salesbuddy.R;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

public class   FragmentReprocessError extends SupportBlurDialogFragment {


    public FragmentReprocessError() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reprocess_error, container, false);
    }


    public void show(androidx.fragment.app.FragmentManager supportFragmentManager, String simpleName) {
    }
}