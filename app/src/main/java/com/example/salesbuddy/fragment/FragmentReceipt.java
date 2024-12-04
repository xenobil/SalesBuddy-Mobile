package com.example.salesbuddy.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.salesbuddy.R;
import com.example.salesbuddy.activity.RegistroVendaActivity;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

public class FragmentReceipt extends SupportBlurDialogFragment {
    private static final String ARG_EMAIL = "emailClient";
    private String emailBuddyDialog;

    public static FragmentReceipt newInstance(String emailClient) {
        FragmentReceipt fragment = new FragmentReceipt();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, emailClient);
        fragment.setArguments(args);
        return fragment;
    }

    // Construtor vazio obrigatório
    public FragmentReceipt() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            emailBuddyDialog = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_receipt, container, false);
        TextView tx_emailDialog = layout.findViewById(R.id.tx_email_dialog_receipt);
        Log.d("fragmentReceipt", "Email: " + emailBuddyDialog);
        tx_emailDialog.setText(emailBuddyDialog);
        return layout;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Intent intent = new Intent(getContext(), RegistroVendaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
