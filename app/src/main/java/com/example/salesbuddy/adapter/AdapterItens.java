package com.example.salesbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salesbuddy.R;
import com.example.salesbuddy.model.ItemSale;

import java.util.ArrayList;

public class AdapterItens extends RecyclerView.Adapter<AdapterItens.MyViewHolder> {

    private ArrayList<ItemSale> itemsList;
    private int idLayout;

    public AdapterItens(ArrayList<ItemSale> lista, int id) {
        this.itemsList = lista;
        this.idLayout = id;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(idLayout, parent, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){

        holder.id.setText(Integer.toString(position+1));
        holder.description.setText(itemsList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView id;
        TextView description;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tx_id_item);
            description = itemView.findViewById(R.id.tx_description_item);
        }
    }
}
