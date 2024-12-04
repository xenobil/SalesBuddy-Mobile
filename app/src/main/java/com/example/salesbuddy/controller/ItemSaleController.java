package com.example.salesbuddy.controller;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.salesbuddy.model.ItemSale;

import java.util.ArrayList;


public class ItemSaleController {
    private ConexaoDatabase banco;

    public ItemSaleController(Context context){
        banco = new ConexaoDatabase(context);
    }

    public Boolean insertItem(ItemSale itemSale, int idSale) {
        ContentValues values;

        values = new ContentValues();
        values.put("sale_id", idSale);
        values.put("description", itemSale.getDescription());

        return banco.db.insert("itemsSale", null, values) > 0;
    }

    @SuppressLint("Range")
    public ArrayList<ItemSale> selectItems(int idSale){
        ArrayList<ItemSale> result = new ArrayList<>();
        Cursor cursor;
        ItemSale itemSale;
        String sqlSelect = "SELECT * FROM itemsSale WHERE sale_id = "+idSale;
        cursor = banco.db.rawQuery(sqlSelect,null);
        if(cursor.moveToFirst()){
            do{
                itemSale = new ItemSale();
                itemSale.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                result.add(itemSale);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public String[] selectItemsArray(int idSale){
        Cursor cursor;
        String sqlSelect = "SELECT * FROM itemsSale WHERE sale_id = "+idSale;
        cursor = banco.db.rawQuery(sqlSelect,null);
        String[] result =  new String[cursor.getCount()];
        int cont = 0;
        if(cursor.moveToFirst()){
            do{
                result[cont] = cursor.getString(cursor.getColumnIndex("description"));
                cont++;
            }while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public boolean deleteItem(int saleId){
        return banco.db.delete("itemsSale","sale_id=?", new String[]{Integer.toString(saleId)}) > 0;
    }
}
