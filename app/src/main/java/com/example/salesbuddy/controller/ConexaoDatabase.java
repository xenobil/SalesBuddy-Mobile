package com.example.salesbuddy.controller;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;


public class ConexaoDatabase extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "salesbuddy.sqlite";
    private static final String TABELA01 = "sale";
    private static final String ID = "id";
    private static final String clientBuddy = "client_buddy";
    private static final String cpfBuddy = "cpf_buddy";
    private static final String emailBuddy = "email_buddy";
    private static final String valueSaleBuddy = "value_sale";
    private static final String valueReceivedBuddy = "value_received";
    private static final String saleNumber = "number_sale";

    private static final String TABELA02 = "itemsSale";
    public static final String saleId = "sale_id"; //chave estrangeira (id da venda)
    private static final String description = "description";

    private static final int VERSAO = 1;
    SQLiteDatabase db;

    public ConexaoDatabase(@Nullable Context context) {
        super(context, NOME_BANCO, null, VERSAO);

        db = getWritableDatabase();


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String tableSale = "CREATE TABLE IF NOT EXISTS "+TABELA01+"("
                + ID + " integer primary key autoincrement,"
                + clientBuddy + " text,"
                + cpfBuddy + " text,"
                + emailBuddy + " text,"
                + valueSaleBuddy + " real,"
                + valueReceivedBuddy + " real,"
                + saleNumber + " integer"
                +")";


        String tableItems = "CREATE TABLE IF NOT EXISTS "+TABELA02+"("
                + ID + " integer primary key autoincrement,"
                + saleId + " integer,"
                + description + " text"
                +")";


        try{
            db.execSQL(tableSale);
            db.execSQL(tableItems);
        }catch (SQLException e){
            Log.e("DB_LOG", "onCreate: "+e.getLocalizedMessage());
        }



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA01);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA02);
        onCreate(db);
    }




}