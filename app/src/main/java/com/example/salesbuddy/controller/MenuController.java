package com.example.salesbuddy.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.salesbuddy.R;
import com.example.salesbuddy.activity.RegistroVendaActivity;
import com.example.salesbuddy.activity.ReprocessamentoActivity;
import com.example.salesbuddy.databinding.PopupMenuBinding;


public class MenuController {
    private PopupWindow popUpWindow;
    private SessionController sessionControl;
    private Context context;
    private Activity activity;
    private ConstraintLayout layout;
    private @NonNull PopupMenuBinding binding;

    public MenuController(Context context, Activity activity, ConstraintLayout layout) {
        this.context = context;
        this.activity = activity;
        this.layout = layout;
        sessionControl = new SessionController(context);
    }

    public void showPopup() {
        binding = PopupMenuBinding.inflate(LayoutInflater.from(context));
        popUpWindow = new PopupWindow(binding.getRoot(), ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);

        binding.routLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionControl.logout();
            }
        });

        binding.routRegisterSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegistroVendaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                popUpWindow.dismiss();
            }
        });

        binding.routReprocess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReprocessamentoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                popUpWindow.dismiss();
            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpWindow.dismiss();
            }
        });

        popUpWindow.setBackgroundDrawable(new ColorDrawable(R.drawable.transparent));
    }

    public void setMenu() {
        popUpWindow.showAsDropDown(layout, 0, 3);
    }
}
