package com.example.salesbuddy.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salesbuddy.R;
import com.example.salesbuddy.adapter.AdapterReprocessamento;
import com.example.salesbuddy.controller.ItemSaleController;
import com.example.salesbuddy.controller.MenuController;
import com.example.salesbuddy.controller.SaleController;
import com.example.salesbuddy.controller.SessionController;
import com.example.salesbuddy.fragment.FragmentReprocessError;
import com.example.salesbuddy.fragment.FragmentReprocessOk;
import com.example.salesbuddy.model.Sale;
import com.example.salesbuddy.service.ServiceGenerator;
import com.example.salesbuddy.service.ServiceInterfaceRegisterSale;
import com.google.gson.Gson;

import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReprocessamentoActivity extends AppCompatActivity {
    private ImageButton btnMenu,btnBack;
    private Button btnReprocess, btnReprocessDisable;
    private ProgressDialog progress;
    private ConstraintLayout layout;
    private SessionController sessionControl;
    private SaleController saleControl;
    private ItemSaleController itemSaleControl;
    private MenuController menuControl;
    private RecyclerView recyclerView;
    private ArrayList<Sale> saleList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_SalesBuddy);
        setContentView(R.layout.activity_reprocessamento);

        sessionControl = new SessionController(getApplicationContext());
        sessionControl.checkSession();
        btnMenu = findViewById(R.id.btn_menuReprocess);
        btnBack = findViewById(R.id.btn_backReprocess);
        btnReprocess = findViewById(R.id.btn_Reprocess);
        btnReprocessDisable = findViewById(R.id.btn_reprocess_disable);
        layout = findViewById(R.id.layout_reprocess);
        recyclerView = findViewById(R.id.recycler_sales_reprocess);
        menuControl = new MenuController(getApplicationContext(),ReprocessamentoActivity.this, layout);
        menuControl.showPopup();
        saleControl = new SaleController(getApplicationContext());
        itemSaleControl = new ItemSaleController(getApplicationContext());
        saleList = saleControl.selectSale();

        if(saleList.isEmpty()){
            btnReprocess.setClickable(false);
            btnReprocessDisable.setVisibility(View.VISIBLE);
            btnReprocess.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "A lista de vendas está vazia.", Toast.LENGTH_SHORT).show();
        } else {
            //Config Adapter
            AdapterReprocessamento adapter = new AdapterReprocessamento(saleList);

            //Config RecyclerView
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

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

        //botão de reprocesso de vendas: verifica a conexão com internet, caso tenha chamado o metodo de reporcessar as vendas, caso não tenha mostra na tela.
        btnReprocess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNetworkConnected();
                if (isNetworkConnected()) {
                    // Se o aparelho estiver conectado à internet
                    progress = new ProgressDialog(ReprocessamentoActivity.this, R.style.MyAlertDialogStyle);
                    progress.setTitle("Reprocessando...");
                    progress.show();
                    reprocess();
                } else {
                    // Se o aparelho não estiver conectado à internet
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Sem acesso à internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Checka se a venda foi registrada com sucesso, se sim deleta a venda e seus itens do banco local, além de mudar a cor do vloco do item para cinza. Senão mostra na tela "erro ao reporcessar venda"
    public void checkRegisteredSale(String message,int id_sale, int position, int lastSale) {
        if (message != null) {
            Log.i("RESPROCESS_RESPONSE", message);
        } else {
            Log.i("RESPROCESS_RESPONSE", "Message is null");
        }

        if (message != null && message.trim().equalsIgnoreCase("venda registrada")) {
            saleControl.deleteSale(id_sale);
            itemSaleControl.deleteItem(id_sale);
            View itemsViewChild = recyclerView.getChildAt(position);
            View viewBlockSale = itemsViewChild.findViewById(R.id.view_reprocess_left);
            viewBlockSale.setBackgroundResource(R.drawable.rounded_bg_reprocess_gray);
            Log.i("DELETE_SALE", "Venda Deletada" + id_sale);
        } else {
            Log.i("REPROCESS_CHECK_SALE", "Venda do id: " + id_sale + "Não reprocessada");
            Toast.makeText(getApplicationContext(), "Erro ao reprocessar venda", Toast.LENGTH_SHORT).show();
        }

    }
    // chama a api de registrar a venda, se tudo ok na chamada, executa metodo de checagem da resposta do servidor.
    public void retrofitReprocessRegisterSale(RequestBody objectJson, int id_sale, int position, int lastSale) {
        // Obtenha o token de autenticação da sua fonte de dados
        String authToken = sessionControl.getAuthToken(); // Supondo que você tenha um método getAuthToken na sua classe SessionController

        ServiceInterfaceRegisterSale service = ServiceGenerator.createService(ServiceInterfaceRegisterSale.class, this);

        Call<Sale> call = service.registerSalePortal(objectJson);

        call.enqueue(new Callback<Sale>() {
            @Override
            public void onResponse(Call<Sale> call, Response<Sale> response) {
                //Verifica se de sucesso na chamada.
                if (response.isSuccessful()) {

                    Sale respostaServidor = response.body();

                    //verifica aqui se o corpo da resposta não é nulo
                    if (respostaServidor != null) {
                        checkRegisteredSale(respostaServidor.getMsn(), id_sale, position, lastSale);
                        if(position == lastSale-1){
                            if(saleControl.checkDB()){
                                FragmentReprocessOk dialog = new FragmentReprocessOk();
                                dialog.show(getSupportFragmentManager() ,dialog.getClass().getSimpleName());


                                Log.i("BD_SELECT_REPROCESS: ","Sucesso - Banco de dados Vazio");
                                btnReprocess.setClickable(false);
                                btnReprocessDisable.setVisibility(View.VISIBLE);
                                btnReprocess.setVisibility(View.GONE);
                                progress.dismiss();
                            }else{
                                // banco não está vazio, algumas vendas não foram reprocessadas

                                FragmentReprocessError dialog = new FragmentReprocessError();
                                dialog.show(getSupportFragmentManager(), dialog.getClass().getSimpleName());

                                Log.i("BD_SELECT_REPROCESS: ","Erro ao reprocessar algumas vendas - Banco de dados ainda contêm algumas vendas para serem reprocessadas");
                                progress.dismiss();

                            }

                        }
                    }else{
                        Log.e("SERVICE_REPROCESS: ", "Resposta nula do servidor, venda não reprocessada"+id_sale);
                        if(position == lastSale-1){
                            if(saleControl.checkDB()){

                                FragmentReprocessOk dialog = new FragmentReprocessOk();
                                dialog.show(getSupportFragmentManager(),dialog.getClass().getSimpleName());
                                Log.i("BD_SELECT_REPROCESS: ","Sucesso - Banco de dados Vazio");
                                btnReprocess.setClickable(false);
                                btnReprocessDisable.setVisibility(View.VISIBLE);
                                btnReprocess.setVisibility(View.GONE);
                            }else{
                                // banco não está vazio, algumas vendas não foram reprocessadas

                                FragmentReprocessError dialog = new FragmentReprocessError();
                                dialog.show(getSupportFragmentManager(),dialog.getClass().getSimpleName());
                                Log.i("BD_SELECT_REPROCESS: ","Erro ao reprocessar algumas vendas - Banco de dados ainda contêm algumas vendas para serem reprocessadas");

                            }
                            progress.dismiss();

                        }
                    }
                } else {
                    Log.e("SERVICE_REPROCESS: ", "Resposta do servidor deu erro, venda não reprocessada"+id_sale);
                    // segura os erros de requisição
                    ResponseBody errorBody = response.errorBody();
                    Log.e("SERVICE_REPROCESS: ", " "+errorBody);
                    if(position == lastSale-1){
                        if(saleControl.checkDB()){

                            FragmentReprocessOk dialog = new FragmentReprocessOk();
                            dialog.show(getSupportFragmentManager(),dialog.getClass().getSimpleName());
                            Log.i("BD_SELECT_REPROCESS: ","Sucesso - Banco de dados Vazio");
                            btnReprocess.setClickable(false);
                            btnReprocessDisable.setVisibility(View.VISIBLE);
                            btnReprocess.setVisibility(View.GONE);
                            progress.dismiss();
                        }else{
                            // banco não está vazio, algumas vendas não foram reprocessadas

                            FragmentReprocessError dialog = new FragmentReprocessError();
                            dialog.show(getSupportFragmentManager(),dialog.getClass().getSimpleName());
                            Log.i("BD_SELECT_REPROCESS: ","Erro ao reprocessar algumas vendas - Banco de dados ainda contêm algumas vendas para serem reprocessadas");
                            progress.dismiss();

                        }

                    }
                }

            }

            //Metodo de falha na chamada
            @Override
            public void onFailure(Call<Sale> call, Throwable t) {
                Log.e("SERVICE_REPROCESS: ",t.getMessage());
                Log.e("SERVICE_REPROCESS: ","Erro na chamada do servidor, venda não reprocessada"+id_sale);
                if(position == lastSale-1){
                    if(saleControl.checkDB()){

                        FragmentReprocessOk dialog = new FragmentReprocessOk();
                        dialog.show(getSupportFragmentManager(),dialog.getClass().getSimpleName());
                        Log.i("BD_SELECT_REPROCESS: ","Sucesso - Banco de dados Vazio");
                        btnReprocess.setClickable(false);
                        btnReprocessDisable.setVisibility(View.VISIBLE);
                        btnReprocess.setVisibility(View.GONE);
                        progress.dismiss();
                    }else{
                        // banco não está vazio, algumas vendas não foram reprocessadas

                        FragmentReprocessError dialog = new FragmentReprocessError();
                        dialog.show(getSupportFragmentManager(),dialog.getClass().getSimpleName());
                        Log.i("BD_SELECT_REPROCESS: ","Erro ao reprocessar algumas vendas - Banco de dados ainda contêm algumas vendas para serem reprocessadas");
                        progress.dismiss();

                    }

                }
            }
        });

    }

    //Método para verificar se tem acesso a internet
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    //faz select de todas as vendas do banco e executa o metodo de chamada da api de registrar vendas para cada uma das vendas que estão no banco local. Além de verificar se, ao final da varredura das vendas, alguma venda não foi reporcessada e mostrar as dialogs na tela.
    @SuppressLint("ResourceType")
    public void reprocess(){
        ArrayList<Sale> saleListAfterReprocess = saleControl.selectSale();
        int cont = 0;
        for (Sale sale: saleListAfterReprocess) {
            String[] items = itemSaleControl.selectItemsArray(sale.getId());
            sale.setDescription(items);
            final String jsonSale = new Gson().toJson(sale); //tranforma o objeto Sale em json
            Log.i("JSONVENDA", jsonSale);
            RequestBody objectJson = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonSale); //Transforma o json em um requestbody
            retrofitReprocessRegisterSale(objectJson,sale.getId(), cont, saleListAfterReprocess.size());
            cont = cont+1;

        }
    }

}
