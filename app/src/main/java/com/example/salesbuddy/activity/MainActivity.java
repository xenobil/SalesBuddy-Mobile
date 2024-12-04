package com.example.salesbuddy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.salesbuddy.R;
import com.example.salesbuddy.controller.ConexaoDatabase;
import com.example.salesbuddy.model.Session;
import com.example.salesbuddy.model.User;
import com.example.salesbuddy.service.ServiceGenerator;
import com.example.salesbuddy.service.AuthService;
import com.google.gson.Gson;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText txUser;
    private EditText txPassword;
    private Button   btnLogin;
    private Session session;
    private ProgressDialog progress;
    public static final String TAG = "Tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_SalesBuddy);
        setContentView(R.layout.activity_main);

        session    = new Session(getApplicationContext());

        if (!session.getUserName().isEmpty()) {
            // Se o usuário estiver logado, redirecione-o para a página inicial
            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
            startActivity(intent);
            finish(); // Finalize a atividade atual para que o usuário não possa voltar para a tela de login pressionando o botão Voltar
        }

        txUser     = findViewById(R.id.tx_user);
        txPassword = findViewById(R.id.tx_password);
        btnLogin   = findViewById(R.id.btn_login);

        //click do botão para logar
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataInput(); // chamada do metodo de checagem dos dados
            }
        });
        ConexaoDatabase conexaoDatabase = new ConexaoDatabase(this);
    }

    // checagem de dados, caso tudo ok chama o método para consumir api
    public void checkDataInput(){
        String paramUser = txUser.getText().toString();
        String paramPassword = txPassword.getText().toString();

        if (paramUser.equals("")) {
            txUser.setError("Preencha o campo parar continuar");
        } else {
            if (paramPassword.equals("")) {
                txPassword.setError("Preencha o campo parar continuar");
            } else {
                progress = new ProgressDialog(MainActivity.this,R.style.MyAlertDialogStyle);
                progress.setTitle("Enviando...");
                progress.show();
                User user = new User();
                user.setUser(paramUser);
                user.setPassword(paramPassword);
                final String jsonUser = new Gson().toJson(user); //tranforma o objeto User em json
                Log.i("JSON_LOGIN", jsonUser);
                RequestBody objectJson = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonUser); //Transforma o json em um requestbody

                retrofitSearchUser(objectJson,paramUser); //metodo que chama a api de autenticação

            }
        }

    }

    //método de consumo da api na rota de login, caso tudo ok chama o método de userCheckExist
    public void retrofitSearchUser(RequestBody objectJson, String userInput){

        AuthService service = ServiceGenerator.createService(AuthService.class, this);

        Call<User> call = service.autenticar(objectJson); //chama o metodo de buscar os usuarios. userInput,passwordInput, , String passwordInput

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                //Verifica se de sucesso na chamada.
                if (response.isSuccessful()) {

                    User respostaServidor = response.body();

                    //verifica aqui se o corpo da resposta não é nulo
                    if (respostaServidor != null) {
                        //Passar os dados para o adapter e setta a recyclerView.

                        userCheckExist(respostaServidor.getMessage(),userInput);
                        progress.dismiss();
                    } else {
                        //resposta nula significa que o usuario não tem repositorios
                        Toast.makeText(getApplicationContext(),"Erro: resposta nula", Toast.LENGTH_LONG).show();

                    }

                } else {

                    Toast.makeText(getApplicationContext(),"Erro ao buscar usuário", Toast.LENGTH_LONG).show();
                    // segura os erros de requisição
                    ResponseBody errorBody = response.errorBody();
                    Log.e(TAG, "Error: "+ errorBody);

                }
                progress.dismiss();
            }

            //Metodo de falha na chamada
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Erro: "+t.getMessage());
                Toast.makeText(getApplicationContext(),"Erro na chamada ao servidor", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),t.getMessage(), Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        });
    }

    //checka a menssagem de retorno da api, se encontrar usuário atribui uma sessão e libera para a HomeActivity
    public void userCheckExist(String message, String paramUser){
        Log.i("Resposta Login", " "+message);
        if(message.equalsIgnoreCase("Login bem-sucedido")){
            session.setUserName(paramUser);
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Erro: Usuario ou senha incorretos ", Toast.LENGTH_SHORT).show();
        }
    }

}
