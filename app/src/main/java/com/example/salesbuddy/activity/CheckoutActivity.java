package com.example.salesbuddy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesbuddy.adapter.AdapterItens;
import com.example.salesbuddy.R;
import com.example.salesbuddy.controller.MenuController;
import com.example.salesbuddy.controller.SessionController;
import com.example.salesbuddy.fragment.FragmentReceipt;
import com.example.salesbuddy.model.ItemSale;
import com.example.salesbuddy.model.Sale;
import com.example.salesbuddy.service.ServiceGenerator;
import com.example.salesbuddy.service.ServiceSendEmail;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private ImageView btnMenu, btnBack;
    private ConstraintLayout layout;
    private SessionController sessionControl;
    private MenuController menuControl;
    private Button btnYes, btnNo;
    private ProgressDialog progress;
    private TextView name, cpf, email, valueSale, valueReceived, changeDue, numberSale;
    private RecyclerView recyclerView;
    private Locale ptBr = new Locale("pt", "BR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_SalesBuddy);
        setContentView(R.layout.activity_checkout);

        name = findViewById(R.id.tx_NameSaleReceipt);
        cpf = findViewById(R.id.tx_cpfSaleReceipt);
        email = findViewById(R.id.tx_emailSaleReceipt);
        valueSale = findViewById(R.id.tx_SaleValueSaleReceipt);
        valueReceived = findViewById(R.id.tx_ValueReceivedSaleReceipt);
        numberSale = findViewById(R.id.tv_number_sale);
        changeDue = findViewById(R.id.tx_ChangeDueSaleReceipt);
        btnMenu = findViewById(R.id.btn_menuSaleReceipt);
        btnBack = findViewById(R.id.btn_backSaleReceipt);
        layout = findViewById(R.id.layout_sale_receipt);
        btnYes = findViewById(R.id.btn_yesSaleReceipt);
        btnNo = findViewById(R.id.btn_notSaleReceipt);
        recyclerView = findViewById(R.id.recycler_itens);
        menuControl = new MenuController(getApplicationContext(), CheckoutActivity.this, layout);
        sessionControl = new SessionController(getApplicationContext());
        sessionControl.checkSession();

        menuControl.showPopup();

        // Recuperando dados
        Bundle dados = getIntent().getExtras();
        Sale sale = (Sale) dados.getSerializable("saleObejct");
        ArrayList<ItemSale> itemsList = (ArrayList<ItemSale>) dados.getSerializable("listItems");
        String changeD = dados.getString("changeDue");
        int saleId = getIntent().getIntExtra("id", 0);
        Log.d("CheckoutActivity", "ID da venda recebido: " + saleId);


        String vS, vR, nS;
        vS = NumberFormat.getCurrencyInstance(ptBr).format(sale.getValueSaleBuddy());
        vR = NumberFormat.getCurrencyInstance(ptBr).format(sale.getValueReceivedBuddy());
        nS = new DecimalFormat("000000").format(sale.getNumberSale());

        // Setando valores
        name.setText(sale.getClientBuddy());
        cpf.setText(sale.getCpfBuddy());
        email.setText(sale.getEmailBuddy());
        valueSale.setText(vS);
        valueReceived.setText(vR);
        changeDue.setText(changeD);
        numberSale.setText(nS);

        // Config adapter
        int layoutItemsSaleReceipt = R.layout.adapter_items_sale_receipt;
        AdapterItens adapter = new AdapterItens(itemsList, layoutItemsSaleReceipt);

        // Config RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuControl.setMenu();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progress = new ProgressDialog(CheckoutActivity.this, R.style.MyAlertDialogStyle);
                progress.setTitle("Enviando...");
                progress.show();

                if (isNetworkConnected()) {
                    // Chama o método para enviar o e-mail
                    retrofitSendEmail(Integer.parseInt(String.valueOf(saleId)), sale.getEmailBuddy());
                } else {
                    // Se o dispositivo não estiver conectado à internet
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Sem acesso à internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistroVendaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void retrofitSendEmail(int saleId, String emailClient) {
        ServiceSendEmail service = ServiceGenerator.createService(ServiceSendEmail.class, this);
        Call<Sale> call = service.sendEmail(Integer.parseInt(String.valueOf(saleId)));

        call.enqueue(new Callback<Sale>() {
            @Override
            public void onResponse(Call<Sale> call, Response<Sale> response) {
                if (response.isSuccessful()) {
                    Sale respostaServidor = response.body();
                    if (respostaServidor != null && respostaServidor.getId() != 0) {
                        sendEmail("Venda registrada com ID: " + respostaServidor.getId(), emailClient, respostaServidor.getId());
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro: resposta nula ou ID inválido", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao enviar para o email", Toast.LENGTH_LONG).show();
                    Log.e("Erro: ", " " + response.errorBody());
                }
                progress.dismiss();
            }


            @Override
            public void onFailure(Call<Sale> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Falha na comunicação com o servidor", Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        });
    }

    public void sendEmail(String message, String emailClient, int saleId) {

        if (saleId != 0) {
            // Mostra a dialog
            FragmentReceipt dialog =  FragmentReceipt.newInstance(emailClient);
            dialog.show(getSupportFragmentManager(), "FragmentReceipt");
        } else {
            Toast.makeText(getApplicationContext(), "Comprovante não enviado", Toast.LENGTH_SHORT).show();
        }
    }
    // Método para verificar se tem acesso a internet
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
