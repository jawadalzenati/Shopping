package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.dialogs.PaymentDialog;
import com.josalla.store.model.Notification;
import com.josalla.store.model.Orders;
import com.josalla.store.model.Transaction;
import com.josalla.store.utils.ToolUtils;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class PayActivity extends AppCompatActivity {

    TextView txtAmount, tvCancel;
    RadioButton rbPayPall, rbBalance;
    Button btnAdd;
    boolean payPal = false, balance = false;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ImageView ivBack;

    static String payment_method = "";

    PaymentDialog paymentDialog;


    public static final int PAYPAL_REQUEST_CODE = 7171;
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
        setContentView(R.layout.activity_pay);

        txtAmount = findViewById(R.id.txtAmount);
        rbPayPall = findViewById(R.id.rbPayPall);
        rbBalance = findViewById(R.id.rbBalance);
        btnAdd = findViewById(R.id.btnAdd);
        ivBack = findViewById(R.id.ivBack);
        tvCancel = findViewById(R.id.tvCancel);


        Intent intent = getIntent();

        paymentDialog = new PaymentDialog(this);


        String price = intent.getStringExtra("price_toPay") + "";

        double totalPrice = Double.parseDouble(price);
        txtAmount.setText(totalPrice + "$");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // close this activity ------------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //------------------------ get the selected payment methods--------------------------
        rbBalance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    payPal = false;
                    balance = true;
                }
            }
        });
        rbPayPall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    payPal = true;
                    balance = false;
                }
            }
        });
        //-------------------------------------------------------------------------------------


        //---- get the payment and send order
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!payPal && !balance) {
                    Toasty.error(PayActivity.this, "الرجاء اختيار طريقة الدفع", Toast.LENGTH_SHORT).show();
                } else {
                    if (payPal) {
                        payment_method = "باي بال (PayPal)";
                        PayFromPayPal(totalPrice);
                    } else if (balance) {
                        payment_method = "رصيد الحساب";
                        PayFromBalance(totalPrice);
                    }
                }
            }
        });


    }

    private void PayFromPayPal(double price) {
        double amount = price;
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD", "الدفع لمتجر شوبنج جو"
                , PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }


    public void PayFromBalance(double price) {
        ToolUtils.showDialog(PayActivity.this, "جاري ارسال الطلب..");
        double total = price;
        db.collection("balance").document(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                double balance = Double.parseDouble(doc.get("user_balance") + "");
                if (balance < total) {
                    Toasty.error(PayActivity.this, "عذرا لايوجد رصيد كافي , الرجاء اضافة رصيد اولا او استخدام طريقة اخرى", Toast.LENGTH_LONG).show();
                    ToolUtils.hideDialog();
                } else {
                    double newBalance = balance - total;
                    db.collection("balance").document(mAuth.getCurrentUser().getUid())
                            .update("user_balance", newBalance + "")

                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Transaction transaction = new Transaction(mAuth.getCurrentUser().getUid() + "",
                                            "شراء من المتجر بقيمة : " + total + " $ ");
                                    db.collection("transaction")
                                            .add(transaction)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    SendOrder();
                                                }
                                            });
                                }
                            });
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    paymentDialog.show();
                    SendOrder();

                }

            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toasty.error(this, "فشلت عمليت الدفع", Toast.LENGTH_SHORT).show();


            }

        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this, "فشلت عمليت الدفع", Toast.LENGTH_SHORT).show();


        }
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void SendOrder() {

        Intent sendIntent = getIntent();

        String selectedDiscrip = sendIntent.getStringExtra("selectedDiscrip");
        String selectedProductId = sendIntent.getStringExtra("selectedProductId");
        String shareCodes = sendIntent.getStringExtra("shareCodes");
        String orderIds = sendIntent.getStringExtra("orders_ids");


        String[] productId = selectedProductId.split("<SELECTED>");
        String[] discripe = selectedDiscrip.split("<DISCRIP>");
        String[] codeShare = shareCodes.split("<SHARED>");
        String[] allOrdersId = orderIds.split("<ORDER>");




        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MMM/yyyy 'at' hh:mm aa");
        String date = dateformat.format(Calendar.getInstance().getTime());


        String price = sendIntent.getStringExtra("price_toPay") + "";


        for (int i = 0; i < productId.length; ++i) {
            Orders orders ;

            if (codeShare.length!=0) {
                orders = new Orders(productId[i] + "", mAuth.getCurrentUser().getUid(), price + ""
                        , discripe[i] + "", payment_method + "", "في انتظار الشحن", System.currentTimeMillis() + "", codeShare[i] + "", date + "");
            }else {
                orders = new Orders(productId[i] + "", mAuth.getCurrentUser().getUid(), price + ""
                        , discripe[i] + "", payment_method + "", "في انتظار الشحن", System.currentTimeMillis() + "",  "", date + "");
            }

//            final int random = new Random().nextInt(20) + 20; // [0, 60] + 20 => [20, 80]
//            String newOrder = System.currentTimeMillis() + "" + random;

            //--- set Order-------------------------------------------

            int finalI = i;
            int finalI1 = i;
            db.collection("orders").document(String.valueOf(allOrdersId[i]))
                    .set(orders).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db.collection("admin_orders").document(String.valueOf(allOrdersId[finalI]))
                            .set(orders).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Notification notification = new Notification("admin", "طلبات جديدة", "هنالك مجموعة طلبات جديدة..", mAuth.getCurrentUser().getUid() + "");
                            db.collection("notification").add(notification)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            ToolUtils.SendNotificationToAdmin("طلبات جديدة", "هنالك مجموعة طلبات جديدة..");
                                            if (paymentDialog.isShowing()) {
                                                paymentDialog.dismiss();
                                            }
                                            Toasty.success(PayActivity.this, "تم الاضافة بنجاح..شكرا لك على ثقتك", Toast.LENGTH_LONG).show();

                                            db.collection("user_cart").document(mAuth.getCurrentUser().getUid() + productId[finalI1])
                                                    .delete();
                                            ToolUtils.hideDialog();
                                            finishAffinity();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));


                                        }
                                    });
                        }
                    });

                }
            });

        }


    }

}
