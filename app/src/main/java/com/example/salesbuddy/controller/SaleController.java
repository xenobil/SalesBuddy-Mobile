package com.example.salesbuddy.controller;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.salesbuddy.controller.ConexaoDatabase;
import com.example.salesbuddy.model.Sale;

import java.util.ArrayList;

public class SaleController {

    private ConexaoDatabase banco;

    public SaleController(Context context){
        banco = new ConexaoDatabase(context);
    }

    public Boolean insertSaleData(Sale sale) {
        ContentValues values;

        values = new ContentValues();
        values.put("client_buddy", sale.getClientBuddy());
        values.put("cpf_buddy", sale.getCpfBuddy());
        values.put("email_buddy", sale.getEmailBuddy());
        values.put("value_sale", sale.getValueSaleBuddy());
        values.put("value_received", sale.getValueReceivedBuddy());
        values.put("number_sale", sale.getNumberSale());

        return banco.db.insert("sale", null, values) > 0;
    }

    @SuppressLint("Range")
    public ArrayList<Sale> selectSale(){
        ArrayList<Sale> result = new ArrayList<>();
        Cursor cursor;
        Sale sale;
        String sqlSelect = "SELECT * FROM sale";
        cursor = banco.db.rawQuery(sqlSelect,null);
        if(cursor.moveToFirst()){
            do{
                sale = new Sale();
                sale.setId(cursor.getInt(cursor.getColumnIndex("id")));
                sale.setClientBuddy(cursor.getString(cursor.getColumnIndex("client_buddy")));
                sale.setCpfBuddy(cursor.getString(cursor.getColumnIndex("cpf_buddy")));
                sale.setEmailBuddy(cursor.getString(cursor.getColumnIndex("email_buddy")));
                sale.setValueSaleBuddy(cursor.getFloat(cursor.getColumnIndex("value_sale")));
                sale.setValueReceivedBuddy(cursor.getFloat(cursor.getColumnIndex("value_received")));
                sale.setNumberSale(cursor.getInt(cursor.getColumnIndex("number_sale")));

                result.add(sale);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public Sale selectLastRegistredSale(){

        Cursor cursor;
        Sale sale = new Sale();
        String sqlSelect = "SELECT * FROM sale WHERE ID = (SELECT MAX(ID)  FROM sale)";
        cursor = banco.db.rawQuery(sqlSelect,null);
        if(cursor.moveToFirst()){
            sale.setId(cursor.getInt(cursor.getColumnIndex("id")));
            sale.setClientBuddy(cursor.getString(cursor.getColumnIndex("client_buddy")));
            sale.setCpfBuddy(cursor.getString(cursor.getColumnIndex("cpf_buddy")));
            sale.setEmailBuddy(cursor.getString(cursor.getColumnIndex("email_buddy")));
            sale.setValueSaleBuddy(cursor.getFloat(cursor.getColumnIndex("value_sale")));
            sale.setValueReceivedBuddy(cursor.getFloat(cursor.getColumnIndex("value_received")));
            sale.setNumberSale(cursor.getInt(cursor.getColumnIndex("number_sale")));
        }
        cursor.close();
        return sale;
    }

    public boolean deleteSale(int id){
        return banco.db.delete("sale","id=?", new String[]{Integer.toString(id)}) > 0;
    }

    @SuppressLint("Range")
    public boolean selectOneSale(int id_sale){
        boolean result;
        Cursor cursor;
        Sale sale = new Sale();
        String sqlSelect = "SELECT * FROM sale WHERE ID = "+id_sale;
        Log.i("QUERY: ", sqlSelect);
        cursor = banco.db.rawQuery(sqlSelect,null);
        if(cursor.moveToFirst()){
            // venda não reprocessada
            result = false;
        } else {
            // cursor está vazio, venda reprocessada
            result = true;
        }
        cursor.close();
        return result;
    }

    public boolean checkDB(){
        boolean result;
        Cursor cursor;
        String sqlSelect = "SELECT * FROM sale";
        cursor = banco.db.rawQuery(sqlSelect,null);
        if (cursor.moveToFirst()) {
            //cursor não está vazio
            result = false;
        } else {
            //cursor está vazio
            result = true;
        }

        return result;
    }

}
