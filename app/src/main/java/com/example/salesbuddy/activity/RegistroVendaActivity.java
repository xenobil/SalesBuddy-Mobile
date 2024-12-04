package com.example.salesbuddy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.salesbuddy.R;
import com.example.salesbuddy.controller.MenuController;
import com.example.salesbuddy.controller.SessionController;
import com.example.salesbuddy.databinding.ActivityRegistroVendaBinding;
import com.example.salesbuddy.model.ItemSale;
import com.example.salesbuddy.model.Sale;
import com.example.salesbuddy.util.MaskEditUtil;
import com.example.salesbuddy.util.MoneyTextWatcher;
import java.util.ArrayList;
import com.example.salesbuddy.util.CPFUtil;

public class RegistroVendaActivity extends AppCompatActivity {
    private ActivityRegistroVendaBinding binding;
    private SessionController sessionControl;
    private MenuController menuControl;
    private Sale sale;
    private ArrayList<ItemSale> itemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroVendaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionControl = new SessionController(getApplicationContext());
        sessionControl.checkSession();
        sale = new Sale();

        binding.btnMenuRegisterSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuControl.setMenu();
            }
        });

        binding.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        binding.btnBackRegisterSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnGotoResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFieldsRegisterSale();
            }
        });

        // Other click listeners...

        // Initialize menu control
        menuControl = new MenuController(getApplicationContext(), RegistroVendaActivity.this, binding.layoutRegisterSale);
        menuControl.showPopup();

        // Set up EditTexts
        setupEditTexts();
    }

    private void setupEditTexts() {
        // Add text change listener for CPF
        binding.txCpfRegisterSale.addTextChangedListener(MaskEditUtil.mask(binding.txCpfRegisterSale, MaskEditUtil.FORMAT_CPF));


        // Add text change listeners for money format
        binding.txValueSaleRegisterSale.addTextChangedListener(new MoneyTextWatcher(binding.txValueSaleRegisterSale));
        binding.txValueReceivedRegisterSale.addTextChangedListener(new MoneyTextWatcher(binding.txValueReceivedRegisterSale));

        // Set up action listener for valueReceived EditText
        binding.txValueReceivedRegisterSale.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || actionId == EditorInfo.IME_ACTION_DONE) {
                    checkFieldsRegisterSale();
                }
                return false;
            }
        });
    }


    private void addItem() {
        final View itemView = getLayoutInflater().inflate(R.layout.item, null, false);

        EditText editText = itemView.findViewById(R.id.tx_item);
        ImageButton imageBtnAddItem =  itemView.findViewById(R.id.btn_add_item);
        ImageButton imageBtnRemoveItem = itemView.findViewById(R.id.btn_remove_item);


        imageBtnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        imageBtnRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.linearLayoutContentItems.removeView(itemView);
                for(int i = 0; i < binding.linearLayoutContentItems.getChildCount(); i++) {

                    View itemsViewChild = binding.linearLayoutContentItems.getChildAt(i);

                    EditText editTextItem = itemsViewChild.findViewById(R.id.tx_item);
                    if (i >= 9) {
                        editTextItem.setHint("ITEM " + (i + 1));
                    } else {
                        editTextItem.setHint("ITEM 0" + (i + 1));
                    }
                }
            }
        });
        changeButtonAddToRemove();
        binding.linearLayoutContentItems.addView(itemView);
        for(int i = 0; i < binding.linearLayoutContentItems.getChildCount(); i++) {

            View itemsViewChild = binding.linearLayoutContentItems.getChildAt(i);

            EditText editTextItem = itemsViewChild.findViewById(R.id.tx_item);
            if (i >= 9) {
                editTextItem.setHint("ITEM " + (i + 1));
            } else {
                editTextItem.setHint("ITEM 0" + (i + 1));
            }
        }
    }

    private void checkFieldsRegisterSale() {
        String nameClientBuddy = binding.txNameClientRegisterSale.getText().toString();
        String cpfBuddy = binding.txCpfRegisterSale.getText().toString();
        String emailBuddy = binding.txEmailRegisterSale.getText().toString();

        if (nameClientBuddy.equals("")) {
            binding.txNameClientRegisterSale.setError("Preencha o campo para continuar");
        } else if (cpfBuddy.equals("")) {
            binding.txCpfRegisterSale.setError("Preencha o campo para continuar");
        } else if (cpfBuddy.length() < 14) {
            binding.txCpfRegisterSale.setError("Por favor preencha seu CPF completo. Exemplo: (000.000.000-00)");
        } else if (!isValidCPF(cpfBuddy)) {
            binding.txCpfRegisterSale.setError("CPF inválido.");
        } else if (emailBuddy.equals("")) {
            binding.txEmailRegisterSale.setError("Preencha o campo para continuar");
        } else if (!isValidEmail(emailBuddy)) {
            binding.txEmailRegisterSale.setError("Insira um email válido. Exemplo(usuario@exemplo.com)");
        } else if (binding.txItem.getText().toString().equals("")) {
            binding.txItem.setError("Preencha o campo para continuar");
        } else if (binding.txValueSaleRegisterSale.getText().toString().equals("")) {
            binding.txValueSaleRegisterSale.setError("Preencha o campo para continuar");
        } else if (binding.txValueReceivedRegisterSale.getText().toString().equals("")) {
            binding.txValueReceivedRegisterSale.setError("Preencha o campo para continuar");
        } else {
            String valueReceivedFormat = binding.txValueReceivedRegisterSale.getText().toString().substring(2);
            String valueSaleFormat = binding.txValueSaleRegisterSale.getText().toString().substring(2);
            float valueSaleFloat;
            float valueReceivedFloat;

            if (valueSaleFormat.length() > 6) {
                valueSaleFloat = Float.parseFloat(valueSaleFormat.replaceAll("\\.", "").replaceAll(",", "."));
            } else {
                valueSaleFloat = Float.parseFloat(valueSaleFormat.replaceAll(",", "."));
            }
            if (valueReceivedFormat.length() > 6) {
                valueReceivedFloat = Float.parseFloat(valueReceivedFormat.replaceAll("\\.", "").replaceAll(",", "."));
            } else {
                valueReceivedFloat = Float.parseFloat(valueReceivedFormat.replaceAll(",", "."));
            }
            if (valueReceivedFloat < valueSaleFloat){
                binding.txValueReceivedRegisterSale.setError("O valor recebido não pode ser menor que o valor da venda");
                return;
            }

            sale.setClientBuddy(nameClientBuddy);
            sale.setCpfBuddy(cpfBuddy);
            sale.setEmailBuddy(emailBuddy);
            sale.setValueSaleBuddy(valueSaleFloat);
            sale.setValueReceivedBuddy(valueReceivedFloat);

            if (checkFieldsItems()) {
                Intent intent = new Intent(getApplicationContext(), ResumoVendaActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("listItems", itemsList);
                intent.putExtras(bundle);
                intent.putExtra("saleObejct", sale);
                startActivity(intent);
            }
        }
    }


    private boolean isValidCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", ""); // Remove non-digit characters
        if (cpf.length() != 11) return false;

        String cpfParcial = cpf.substring(0, 9);
        String cpfCompletoGerado = CPFUtil.gerarCPFCompleto(cpfParcial);
        return cpf.equals(cpfCompletoGerado);
    }
    private void changeButtonAddToRemove() {
        int count = binding.linearLayoutContentItems.getChildCount();
        for (int i = 0; i < count; i++) {
            View itemViewChild = binding.linearLayoutContentItems.getChildAt(i);
            ImageButton imageBtnAddItem = itemViewChild.findViewById(R.id.btn_add_item);
            ImageButton imageBtnRemoveItem = itemViewChild.findViewById(R.id.btn_remove_item);
            imageBtnAddItem.setVisibility(View.GONE);
            imageBtnRemoveItem.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkFieldsItems() {
        itemsList.clear();
        boolean result = true;

        for (int i = 0; i < binding.linearLayoutContentItems.getChildCount(); i++) {

            View itemsViewChild = binding.linearLayoutContentItems.getChildAt(i);

            EditText editTextItem = itemsViewChild.findViewById(R.id.tx_item);

            ItemSale item = new ItemSale();
            if (i == 0) {
                if (!editTextItem.getText().toString().equals("")) {
                    item.setDescription(editTextItem.getText().toString());
                    itemsList.add(item);
                } else {
                    editTextItem.setError("Preencha o campo para continuar");
                    result = false;
                    break;
                }
            } else {
                if (!editTextItem.getText().toString().equals("")) {
                    item.setDescription(editTextItem.getText().toString());
                    itemsList.add(item);
                }
            }
        }

        return result;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
