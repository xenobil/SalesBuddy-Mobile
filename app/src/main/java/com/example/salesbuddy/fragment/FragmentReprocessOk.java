package com.example.salesbuddy.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.salesbuddy.R;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

public class FragmentReprocessOk extends SupportBlurDialogFragment {


    public FragmentReprocessOk() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reprocess_ok, container, false);
    }

    public void show(FragmentManager supportFragmentManager, String simpleName) {
    }
}