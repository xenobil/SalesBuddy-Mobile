package com.example.salesbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salesbuddy.R;
import com.example.salesbuddy.model.Sale;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdapterReprocessamento extends RecyclerView.Adapter<AdapterReprocessamento.MyViewHolder> {
    private ArrayList<Sale> reprocessList;
    private String currencySymbol = "R$ "; //simbolo monetário REAL, com espaço.
    private Locale ptBr = new Locale("pt","BR");

    public AdapterReprocessamento(ArrayList<Sale> lista){
        this.reprocessList = lista;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View saleList = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_reprocess_layout, parent,false);
        return new MyViewHolder(saleList);
    }

    @Override

    public void onBindViewHolder(@NonNull AdapterReprocessamento.MyViewHolder holder, int position) {
        if (reprocessList != null && position < reprocessList.size()) {
            holder.nameClient.setText(reprocessList.get(position).getClientBuddy());
            double valueSaleBuddy = reprocessList.get(position).getValueSaleBuddy();
            String valueSaleFormat = NumberFormat.getCurrencyInstance(ptBr).format(valueSaleBuddy);
            valueSaleFormat = valueSaleFormat.substring(2);
            String valueSaleText = currencySymbol + valueSaleFormat;
            if (holder.valueSale != null) {
                holder.valueSale.setText(valueSaleText);
            }
        }
    }


    @Override
    public int getItemCount() {
        return reprocessList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nameClient, valueSale;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameClient = itemView.findViewById(R.id.tv_name_client_reprocess);
            valueSale = itemView.findViewById(R.id.tv_value_sale_reprocess);

        }

    }
}
