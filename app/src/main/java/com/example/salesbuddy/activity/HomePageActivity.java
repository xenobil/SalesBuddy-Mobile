package com.example.salesbuddy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.example.salesbuddy.R;
import com.example.salesbuddy.controller.MenuController;
import com.example.salesbuddy.controller.SessionController;

public class HomePageActivity extends AppCompatActivity {
    private Button btnRegisterSale;
    private Button btnReprocess;
    private ImageView btnTesteMenu;
    private PopupWindow popUpWindow;
    private ConstraintLayout layout;
    private SessionController sessionControl;
    private MenuController menuControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_SalesBuddy);
        setContentView(R.layout.activity_home_page);

        btnRegisterSale = findViewById(R.id.btn_RegisterSale);
        btnReprocess = findViewById(R.id.btn_Reprocess);
        btnTesteMenu = findViewById(R.id.btn_testeMenu);

        sessionControl = new SessionController(getApplicationContext());
        sessionControl.checkSession();

        layout = findViewById(R.id.constraint); // Corrigido para usar o ID correto
        menuControl = new MenuController(getApplicationContext(), HomePageActivity.this, layout);
        menuControl.showPopup();

        btnTesteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuControl.setMenu();
            }
        });

        btnRegisterSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistroVendaActivity.class);
                startActivity(intent);
            }
        });

        btnReprocess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReprocessamentoActivity.class);
                startActivity(intent);
            }
        });
    }

}