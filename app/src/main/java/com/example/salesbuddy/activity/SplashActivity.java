package com.example.salesbuddy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.salesbuddy.R;


import com.example.salesbuddy.databinding.SplashAbrindoBinding;

import android.content.Intent;

import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 4000; //4 SEGUNDOS
    private SplashAbrindoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SplashAbrindoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Após o tempo definido, inicia a MainActivity
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Finaliza a SplashActivity para que o usuário não possa voltar a ela pressionando o botão de volta
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
