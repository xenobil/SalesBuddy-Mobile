package com.example.salesbuddy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesbuddy.adapter.AdapterItens;
import com.example.salesbuddy.R;
import com.example.salesbuddy.controller.ItemSaleController;
import com.example.salesbuddy.controller.MenuController;
import com.example.salesbuddy.controller.SaleController;
import com.example.salesbuddy.controller.SessionController;
import com.example.salesbuddy.model.ItemSale;
import com.example.salesbuddy.model.Sale;
import com.example.salesbuddy.service.ServiceGenerator;
import com.example.salesbuddy.service.ServiceInterfaceRegisterSale;
import com.example.salesbuddy.service.ServiceNumberSale;
import com.example.salesbuddy.service.ServiceSendEmail;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumoVendaActivity extends AppCompatActivity {
    private ImageView btnMenu, btnBack;
    private Button btnFinishSale, btnChange;
    private ConstraintLayout layout;
    private SessionController sessionControl;
    private MenuController menuControl;
    private ProgressDialog progress;
    private RecyclerView recyclerView;
    private TextView name, cpf, email, valueSale, valueReceived, changeDue;
    private Locale ptBr = new Locale("pt", "BR");
    private SaleController saleControl;
    private ItemSaleController itemSaleControl;
    private Sale sale;
    private String jsonSale;
    private int saleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_SalesBuddy);
        setContentView(R.layout.activity_resumo_venda);

        sessionControl = new SessionController(getApplicationContext());
        sessionControl.checkSession();
        saleControl = new SaleController(getApplicationContext());
        itemSaleControl = new ItemSaleController(getApplicationContext());

        name = findViewById(R.id.tx_NameSaleReceipt);
        cpf = findViewById(R.id.tx_cpfSaleReceipt);
        email = findViewById(R.id.tx_emailSaleReceipt);
        valueSale = findViewById(R.id.tx_SaleValueSaleReceipt);
        valueReceived = findViewById(R.id.tx_ValueReceivedSaleReceipt);
        changeDue = findViewById(R.id.tx_ChangeDueSaleReceipt);
        btnMenu = findViewById(R.id.btn_menu);
        btnBack = findViewById(R.id.btn_back);
        btnChange = findViewById(R.id.btn_Change);
        layout = findViewById(R.id.layout);
        btnFinishSale = findViewById(R.id.btn_Finish);
        recyclerView = findViewById(R.id.recycler_itens);
        menuControl = new MenuController(getApplicationContext(), ResumoVendaActivity.this, layout);
        menuControl.showPopup();

        // Recuperando os dados
        Bundle dados = getIntent().getExtras();
        sale = (Sale) dados.getSerializable("saleObejct");
        ArrayList<ItemSale> itemsList = (ArrayList<ItemSale>) dados.getSerializable("listItems");
        String vS, vR;
        vS = NumberFormat.getCurrencyInstance(ptBr).format(sale.getValueSaleBuddy());
        vR = NumberFormat.getCurrencyInstance(ptBr).format(sale.getValueReceivedBuddy());

        // Setando os dados
        name.setText(sale.getClientBuddy());
        cpf.setText(sale.getCpfBuddy());
        email.setText(sale.getEmailBuddy());
        valueSale.setText(vS);
        valueReceived.setText(vR);
        changeDue.setText(ChangeDue(sale.getValueSaleBuddy(), sale.getValueReceivedBuddy()));
        String changeD = ChangeDue(sale.getValueSaleBuddy(), sale.getValueReceivedBuddy());

        // Config adapter
        int layoutItemsSaleSummary = R.layout.adapter_itens;
        AdapterItens adapter = new AdapterItens(itemsList, layoutItemsSaleSummary);

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

        btnFinishSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(ResumoVendaActivity.this, R.style.MyAlertDialogStyle);
                progress.setTitle("Registrando...");
                progress.show();

                String[] itemsArray = new String[itemsList.size()];
                int cont = 0;
                for (ItemSale saleItems : itemsList) {
                    itemsArray[cont] = saleItems.getDescription();
                    cont++;
                }
                sale.setDescription(itemsArray);

                String user_buddy = sessionControl.userSession();
                final String jsonUser = "{\"user\" : \"" + user_buddy + "\"}"; // transforma o user da sessão em json
                RequestBody objectUserJson = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonUser); // Transforma o json em um requestbody
                retrofitRequestNumberSale(objectUserJson, itemsList, changeD);
                Log.i("JSON: ", jsonUser);
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void retrofitRegisterSale(RequestBody objectJson, Sale sale, ArrayList<ItemSale> itemsSale, String changeD) {
        ServiceInterfaceRegisterSale service = ServiceGenerator.createService(ServiceInterfaceRegisterSale.class, this);
        Call<Sale> call = service.registerSalePortal(objectJson);

        call.enqueue(new Callback<Sale>() {
            @Override
            public void onResponse(Call<Sale> call, Response<Sale> response) {
                // Verifica se deu sucesso na chamada.
                if (response.isSuccessful()) {
                    Sale respostaServidor = response.body();

                    // Verifica aqui se o corpo da resposta não é nulo
                    if (respostaServidor != null) {
                        Log.i("RETORNO_SERVICE", "menssagem: " + respostaServidor.getMsn());
                        sale.setMsn(respostaServidor.getMsn());
                        // Aqui você extrai o ID da resposta
                        saleId = respostaServidor.getId();

                        checkRegisteredSale(respostaServidor.getMsn(), sale, itemsSale, changeD);
                        progress.dismiss();
                    } else {
                        // Resposta nula
                        Log.e("RETORNO_SERVICE", "Resposta do servidor nula");
                        Toast.makeText(getApplicationContext(), "Erro: resposta nula", Toast.LENGTH_LONG).show();
                        reprocessInsert(sale, itemsSale, changeD);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao registrar venda", Toast.LENGTH_LONG).show();
                    // Segura os erros de requisição
                    ResponseBody errorBody = response.errorBody();
                    Log.e("Erro: ", " " + errorBody);
                    reprocessInsert(sale, itemsSale, changeD);
                }
                progress.dismiss();
            }

            // Metodo de falha na chamada
            @Override
            public void onFailure(Call<Sale> call, Throwable t) {
                Log.e("Erro: ", t.getMessage());
                Toast.makeText(getApplicationContext(), "Erro na chamada ao servidor", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                progress.dismiss();
                reprocessInsert(sale, itemsSale, changeD);
            }
        });
    }

    public void retrofitRequestNumberSale(RequestBody objectJson, ArrayList<ItemSale> itemsSale, String changeD) {
        Random numberSaleDefault = new Random();
        ServiceNumberSale service = ServiceGenerator.createService(ServiceNumberSale.class, this);

        Call<Sale> call = service.numberSale(objectJson);
        call.enqueue(new Callback<Sale>() {
            @Override
            public void onResponse(Call<Sale> call, Response<Sale> response) {
                // Verifica se de sucesso na chamada.
                if (response.isSuccessful()) {
                    Sale respostaServidor = response.body();

                    // Verifica aqui se o corpo da resposta não é nulo
                    if (respostaServidor != null) {
                        // Se a resposta do servidor não for nula, então o número de venda é válido
                        // Você pode prosseguir com o número de venda retornado pelo servidor
                        sale.setNumberSale(respostaServidor.getNumberSale());
                    } else {
                        // Resposta nula do servidor
                        // Neste caso, você pode definir um valor padrão para o número de venda
                        // ou gerar um número de venda aleatório
                        sale.setNumberSale(numberSaleDefault.nextInt(1000) + 200);
                    }
                } else {
                    // Tratamento para resposta não bem-sucedida
                    // Definir um valor padrão ou gerar um número de venda aleatório
                    sale.setNumberSale(numberSaleDefault.nextInt(1000) + 200);
                }

                // Transforma o objeto Sale em JSON
                jsonSale = new Gson().toJson(sale);
                Log.i("JSON_SALE: ", jsonSale);

                // Cria um novo RequestBody com o JSON atualizado
                RequestBody updatedObjectJson = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonSale);

                // Chama o método para registrar a venda, passando o RequestBody atualizado
                retrofitRegisterSale(updatedObjectJson, sale, itemsSale, changeD);
            }

            @Override
            public void onFailure(Call<Sale> call, Throwable t) {
                // Em caso de falha na chamada ao servidor, definir um número de venda padrão ou aleatório
                sale.setNumberSale(numberSaleDefault.nextInt(1000) + 200);

                // Transforma o objeto Sale em JSON
                jsonSale = new Gson().toJson(sale);
                Log.i("JSON_SALE: ", jsonSale);

                // Cria um novo RequestBody com o JSON atualizado
                RequestBody updatedObjectJson = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonSale);

                // Chama o método para registrar a venda, passando o RequestBody atualizado
                retrofitRegisterSale(updatedObjectJson, sale, itemsSale, changeD);
            }
        });
    }

    public void checkRegisteredSale(String message, Sale sale, ArrayList<ItemSale> itemsSale, String changeD) {
        Log.d("AAA", "Valor de message: " + message);
        if (message != null && "venda registrada".equalsIgnoreCase(message.trim())) {
            // Se a mensagem não for nula e for igual a "venda registrada"
            Toast.makeText(getApplicationContext(), "Venda registrada com sucesso no portal", Toast.LENGTH_SHORT).show();
            Log.d("OrigemActivity", "ID da venda antes de iniciar CheckoutActivity: " + saleId);
            Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("listItems", itemsSale);
            intent.putExtras(bundle);
            intent.putExtra("saleObejct", sale);
            intent.putExtra("changeDue", changeD);
            intent.putExtra("id", saleId);  // Passa o ID retornado aqui
            startActivity(intent);

        } else {
            // Se a mensagem for nula ou não for igual a "venda registrada"
            Toast.makeText(getApplicationContext(), "Erro: Venda não inserida no portal", Toast.LENGTH_SHORT).show();
            if (saleControl.insertSaleData(sale)) {
                Sale lastSale = saleControl.selectLastRegistredSale();
                boolean resultInsertItems = false;
                for (ItemSale itemSale : itemsSale) {
                    if (itemSaleControl.insertItem(itemSale, lastSale.getId())) {
                        Log.i("SUCESS_DB: ", "item " + itemSale.getDescription() + " inserido com sucesso!");
                        resultInsertItems = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro Técnico: Sua venda não foi registrada no banco de dados local", Toast.LENGTH_LONG).show();
                        Log.e("ERROR_DB: ", "Erro ao inserir itens");
                        saleControl.deleteSale(lastSale.getId());
                        break;
                    }
                }
                if (resultInsertItems) {
                    Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("listItems", itemsSale);
                    intent.putExtras(bundle);
                    intent.putExtra("saleObejct", sale);
                    intent.putExtra("changeDue", changeD);
                    intent.putExtra("id", lastSale.getId()); // Passa o ID do último registro inserido aqui
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Erro Técnico: Sua venda não foi registrada no banco de dados local", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Erro Técnico: Sua venda não foi registrada no banco de dados local", Toast.LENGTH_LONG).show();
            }
        }
    }


    public String ChangeDue(float vS, float vR) {
        float change = vR - vS;
        String result = NumberFormat.getCurrencyInstance(ptBr).format(change);
        return result;
    }

    public void reprocessInsert(Sale sale, ArrayList<ItemSale> itemsSale, String changeD) {
        if (saleControl.insertSaleData(sale)) {
            Sale lastSale = saleControl.selectLastRegistredSale();
            boolean resultInsertItems = false;
            for (ItemSale itemSale : itemsSale) {
                if (itemSaleControl.insertItem(itemSale, lastSale.getId())) {
                    Log.i("SUCESS_DB: ", "item " + itemSale.getDescription() + " inserido com sucesso!");
                    resultInsertItems = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Erro Técnico: Sua venda não foi registrada no banco de dados local", Toast.LENGTH_LONG).show();
                    Log.e("ERROR_DB: ", "Erro ao inserir itens");
                    saleControl.deleteSale(lastSale.getId());
                    break;
                }
            }
            if (resultInsertItems) {
                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("listItems", itemsSale);
                intent.putExtras(bundle);
                intent.putExtra("saleObejct", sale);
                intent.putExtra("changeDue", changeD);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Erro Técnico: Sua venda não foi registrada no banco de dados local", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Erro Técnico: Sua venda não foi registrada no banco de dados local", Toast.LENGTH_LONG).show();
        }
    }
}
