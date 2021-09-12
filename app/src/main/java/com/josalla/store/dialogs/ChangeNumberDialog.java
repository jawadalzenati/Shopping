package com.josalla.store.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.R;

import es.dmoral.toasty.Toasty;

public class ChangeNumberDialog extends Dialog {
    Activity activity;
    Dialog dialog;

    TextView tvCurrentNumber;
    EditText txtNewNumber;
    Button btnChange, btnCancel;

    FirebaseFirestore db;



    public ChangeNumberDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_number);

        tvCurrentNumber = findViewById(R.id.tvCurrentNumber);
        txtNewNumber = findViewById(R.id.txtNewNumber);
        btnChange = findViewById(R.id.btnChange);
        btnCancel = findViewById(R.id.btnCancel);


        db = FirebaseFirestore.getInstance();





        db.collection("contacts").document("contacts")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                tvCurrentNumber.setText("الرقم الحالي : "+doc.get("mobile") + "");

            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("contacts").document("contacts").update("mobile", txtNewNumber.getText().toString());
                Toasty.success(activity, "تم تعديل الرقم", Toast.LENGTH_SHORT).show();


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    dismiss();
                }
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
