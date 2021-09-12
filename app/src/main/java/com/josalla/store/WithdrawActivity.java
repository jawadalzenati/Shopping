package com.josalla.store;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.model.PaymentRequests;
import com.josalla.store.model.Transaction;
import com.josalla.store.utils.ToolUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class WithdrawActivity extends AppCompatActivity {
    EditText txtAmount, txtEmail;
    Button btnAdd, btnCancel;
    ImageView ivBack;
    TextView tvAvilable;

    FirebaseAuth mAuth;
    FirebaseFirestore db;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        txtAmount = findViewById(R.id.txtAmount);
        txtEmail = findViewById(R.id.txtEmail);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        ivBack = findViewById(R.id.ivBack);
        tvAvilable = findViewById(R.id.tvAvilable);

        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy 'at' hh:mm aa");

        Intent intent = getIntent(); //balance
        tvAvilable.setText(intent.getStringExtra("balance") + " د.أ ");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String amount = txtAmount.getText() + "";



                if (amount.length() == 0 || (txtEmail.getText() + "").length() == 0) {
                    if (amount.length() == 0)
                        txtAmount.setError("يرجى اضافة المبلغ");
                    else
                        txtEmail.setError("مطلوب");
                } else {
                    double amount_pay = Double.parseDouble(txtAmount.getText() + "");
                    double current = Double.parseDouble(intent.getStringExtra("balance") + "");

                    if (amount_pay < 25) {
                        txtAmount.setError("اقل مبلغ هو 25 دينار");
                    } else {
                        if (amount_pay > current) {
                            txtAmount.setError("لايوجد رصيد كافي");
                        } else {

                            String date = dateformat.format(Calendar.getInstance().getTime());
                            double newBalance = current - amount_pay;
                            db.collection("balance").document(mAuth.getCurrentUser().getUid())
                                    .update("user_balance", newBalance + "");

                            PaymentRequests paymentRequests = new PaymentRequests(mAuth.getCurrentUser().getUid() + ""
                                    , txtEmail.getText().toString() + "", date+"", amount + "" , "yet");

                            db.collection("paymentRequests")
                                    .add(paymentRequests)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Transaction transaction = new Transaction(mAuth.getCurrentUser().getUid(), "طلب سحب رصيد بمبلغ : " + amount + " دينار ");
                                            db.collection("transaction")
                                                    .add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    ToolUtils.SendNotificationToAdmin("طلب دفع", "هنالك طلب دفع جديد !");

                                                    Toasty.success(WithdrawActivity.this, "تم أرسال طلب الدفع", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                    startActivity(new Intent(getApplicationContext(), BalanceActivity.class));

                                                }
                                            });

                                        }
                                    });
                        }

                    }

                }


            }
        });


        ////--------------------------
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), BalanceActivity.class));
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), BalanceActivity.class));

            }
        });
    }
}