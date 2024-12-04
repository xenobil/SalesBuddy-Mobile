package com.example.salesbuddy.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.salesbuddy.activity.HomePageActivity;
import com.example.salesbuddy.model.Session;
import com.example.salesbuddy.activity.MainActivity;

public class SessionController {
    private final Session session;
    private final Context context;

    public SessionController(Context context){
        this.session = new Session(context);
        this.context = context;
    }

    public void checkSession(){
        if (session.getUserName() == null || session.getUserName().equals("")) {
            logout();
        }


    }
    public void logout(){
        session.setUserName("");
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
    public String userSession(){
        return session.getUserName();
    }

    public String getAuthToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NomePreferencias", Context.MODE_PRIVATE);
        return sharedPreferences.getString("AuthToken", null);

    }
}
