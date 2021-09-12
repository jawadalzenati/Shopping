package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.dialogs.PaymentDialog;
import com.josalla.store.model.Transaction;
import com.josalla.store.utils.ToolUtils;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

import es.dmoral.toasty.Toasty;

import static com.josalla.store.CompleteOrderActivity.PAYPAL_REQUEST_CODE;

public class AddBalanceActivity extends AppCompatActivity {

    ImageView ivBack;
    TextView tvState;
    Button btnAdd, btnCancel;
    String text2;
    String pay_amount;
    PaymentDialog paymentDialog;

    FirebaseFirestore db;
    FirebaseAuth mAuth;


    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(ToolUtils.PAYPAL_CLIENT_ID)
            .merchantName("Mesteck")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.paypal.com/us/webapps/mpp/ua/privacy-full"))
            .merchantUserAgreementUri(Uri.parse("https://www.paypal.com/us/webapps/mpp/ua/useragreement-full"));

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);

        Intent intent = getIntent();

        String amount = intent.getStringExtra("amount");
        String payment = intent.getStringExtra("payment");
        String text1 = "سوف يتم اضافة رصيد الى حسابك بقيمة : ";
        String text3 = " هل تريد الأستمرار ؟!";

        if (payment.equals("paypall")) {
            text2 = " $ , بأستخدام باي بال";
        } else if (payment.equals("google_pay")) {
            text2 = " $ , بأستخدام Google Play";
        }


        ivBack = findViewById(R.id.ivBack);
        tvState = findViewById(R.id.tvState);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        paymentDialog = new PaymentDialog(this);

        tvState.setText(text1 + amount + text2 + text3);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (payment.equals("paypall")) {
                    makePayment(amount + "");

                }

            }
        });


        //===========================================================
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void makePayment(String price) {

        pay_amount = "" + (Double.parseDouble(price) );
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(pay_amount)), "USD", "الدفع لمتجر شوبنج جو"
                , PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    paymentDialog.show();
                    AddBalanceForUser();
                }

            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toasty.error(this, "فشلت عمليت الدفع", Toast.LENGTH_SHORT).show();


            }

        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this, "فشلت عمليت الدفع", Toast.LENGTH_SHORT).show();


        }
    }

    private void AddBalanceForUser() {
        Intent intent = getIntent();
        db.collection("balance")
                .document(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) {
                    double current = Double.parseDouble(doc.get("user_balance") + "");
                    double new_amount = current + Double.parseDouble(intent.getStringExtra("amount") + "");

                    db.collection("balance").document(mAuth.getCurrentUser().getUid())
                            .update("user_balance", new_amount + "");

                    Transaction transaction = new Transaction(mAuth.getCurrentUser().getUid()+"", "اضافة رصيد بمبلغ : "+intent.getStringExtra("amount")+" $ ");

                    db.collection("transaction").add(transaction)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    new CountDownTimer(1000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                        }
                                        public void onFinish() {
                                            paymentDialog.dismiss();
                                            finish();
                                            startActivity(new Intent(getApplicationContext() , BalanceActivity.class));

                                        }
                                    }.start();
                                }
                            });

                }
            }
        });

    }
}
