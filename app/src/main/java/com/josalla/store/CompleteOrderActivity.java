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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.dialogs.PaymentDialog;
import com.josalla.store.model.Notification;
import com.josalla.store.model.Orders;
import com.josalla.store.model.Transaction;
import com.josalla.store.model.UsersShear;
import com.josalla.store.utils.ToolUtils;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class CompleteOrderActivity extends AppCompatActivity {
    ImageView ivBack, ivImage, tvMap;

    TextView tvOrderNumber, tvPrice, tvDescription, tvSizeAndColor, tvFullName, tvAddress, tvPhones, tvCount, tvBalance;
    RadioButton rbHand, rbPayPall, rbBalance;
    RadioGroup group;
    PaymentDialog paymentDialog;

    Button btnCompleteOrder;
    public static final int PAYPAL_REQUEST_CODE = 7171;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Intent intent;
    static boolean hand = false, paypal = false, balance = false;
    static String payment_method = "";
    String amount = "";

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
        setContentView(R.layout.activity_complete_order);

        paymentDialog = new PaymentDialog(this);

        ivBack = findViewById(R.id.ivBack);
        ivImage = findViewById(R.id.ivImage);
        tvOrderNumber = findViewById(R.id.tvOrderNumber);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvSizeAndColor = findViewById(R.id.tvSizeAndColor);
        tvFullName = findViewById(R.id.tvFullName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhones = findViewById(R.id.tvPhones);
        rbHand = findViewById(R.id.rbHand);
        rbPayPall = findViewById(R.id.rbPayPall);
        btnCompleteOrder = findViewById(R.id.btnCompleteOrder);
        tvCount = findViewById(R.id.tvCount);
        tvMap = findViewById(R.id.tvMap);
        group = findViewById(R.id.group);
        tvBalance = findViewById(R.id.tvBalance);
        rbBalance = findViewById(R.id.rbBalance);


        intent = getIntent();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


//        order_intent.putExtra("order_id", id);
//        order_intent.putExtra("order_number", System.currentTimeMillis());
//        order_intent.putExtra("order_image", image);
//        order_intent.putExtra("order_disc", description);
//        order_intent.putExtra("order_price", price);
//        order_intent.putExtra("order_size", spSizes.getSelectedItem() + "");
//        order_intent.putExtra("order_color", spColors.getSelectedItem() + "");
//        order_intent.putExtra("person_name", users.getUser_name());
        // tvAddress.setText(intent.getStringExtra("person_location"));
//        order_intent.putExtra("person_address", users.getUser_address());
//        order_intent.putExtra("person_phones", users.getUser_phone());

        if (("" + intent.getStringExtra("hand_pay")).equals("yes")) {
            rbHand.setVisibility(View.VISIBLE);

        }

        double t_ammount = Double.parseDouble(intent.getStringExtra("order_conut"));
        double unit_price = Double.parseDouble(intent.getStringExtra("order_price"));


        double total_price = t_ammount * unit_price;

        Glide.with(this).load(intent.getStringExtra("order_image")).into(ivImage);
        tvOrderNumber.setText("رقم الطلب : " + intent.getStringExtra("order_number"));
        tvPrice.setText("السعر الاجمالي: " + total_price);
        tvDescription.setText(intent.getStringExtra("order_disc"));
        tvSizeAndColor.setText("اللون المحدد: " + intent.getStringExtra("order_color") + " | المقاس : " + intent.getStringExtra("order_size"));
        tvFullName.setText(intent.getStringExtra("person_name"));

        if (intent.getStringExtra("person_address").length() != 0) {
            tvAddress.setText(intent.getStringExtra("person_address"));
            if (intent.getStringExtra("person_location").length() != 0 && !intent.getStringExtra("person_location").equals("<>")) {
                tvMap.setVisibility(View.VISIBLE);
            }
        } else {
            tvAddress.setText(getResources().getString(R.string.location));
            tvMap.setVisibility(View.VISIBLE);
        }
        tvPhones.setText(intent.getStringExtra("person_phones").replace("<>", " , "));
        tvCount.setText("الكمية : " + intent.getStringExtra("order_conut"));


        //-------------------select the payment methods -----------------------

        rbPayPall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hand = false;
                    paypal = true;
                    balance = false;
                }
            }
        });

        rbHand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hand = true;
                    paypal = false;
                    balance = false;
                }
            }
        });

        rbBalance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hand = false;
                paypal = false;
                balance = true;

            }
        });
        //-----------------------------------------------------------------------------------


        btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int random = new Random().nextInt(20) + 20; // [0, 60] + 20 => [20, 80]

                String newOrder = System.currentTimeMillis() + "" + random;

                if (!paypal && !hand && !balance) {
                    Toasty.error(CompleteOrderActivity.this, "يجب اختيار طريقة الدفع", Toast.LENGTH_SHORT).show();
                } else {
                    ToolUtils.showDialog(CompleteOrderActivity.this, "جاري ارسال الطلب!");

                    if (hand && !paypal) {
                        payment_method = "الدفع باليد";
                    } else if (!hand && paypal) {
                        payment_method = "باي بال (PayPal)";
                    }

                    if (hand) {
                        SendOrder(intent);
                    } else if (paypal) {
                        makePayment(total_price + "");
                    } else if (balance) {
                        payment_method = "رصيد الحساب";
                        PayAndSend(total_price + "");
                    }
                }


            }
        });


        //get the current balance

        db.collection("balance")
                .document(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {

                String myBalance = doc.get("user_balance") + "";
                if (myBalance.length() > 4) {
                    myBalance = myBalance.substring(0, 4);

                }
                tvBalance.setText("متوفر: " + myBalance);


            }
        });

        //--------------open map activity -----------------------
        tvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map_intent = new Intent(getApplicationContext(), MapsActivity.class);
                map_intent.putExtra("location", intent.getStringExtra("person_location"));
                startActivity(map_intent);
            }
        });


        // finish this activity and back home ----------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void makePayment(String price) {

        amount = "" + (Double.parseDouble(price));
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD", "الدفع لمتجر شوبنج جو"
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
                    SendOrder(intent);

                }

            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toasty.error(this, "فشلت عمليت الدفع", Toast.LENGTH_SHORT).show();


            }

        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this, "فشلت عمليت الدفع", Toast.LENGTH_SHORT).show();


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToolUtils.hideDialog();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void SendOrder(Intent intent) {


        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MMM/yyyy 'at' hh:mm aa");
        String date = dateformat.format(Calendar.getInstance().getTime());

        double ammount = Double.parseDouble(intent.getStringExtra("order_conut") + "");
        double t_price = Double.parseDouble(intent.getStringExtra("order_price") + "");

        double total_price = ammount * t_price;
        // Orders( product_id,  order_account,  order_price,  order_ammount,  order_discrp) {

        Orders orders = new Orders(intent.getStringExtra("order_id") + "", mAuth.getCurrentUser().getUid(), total_price + ""
                , intent.getStringExtra("order_conut"), "اللون المحدد: " + intent.getStringExtra("order_color") + " | المقاس : " + intent.getStringExtra("order_size"),
                payment_method, "في انتظار الشحن", System.currentTimeMillis() + "", intent.getStringExtra("order_code_id") + "" , date+"" );

        final int random = new Random().nextInt(20) + 20; // [0, 60] + 20 => [20, 80]
        String newOrder = System.currentTimeMillis() + "" + random;

        db.collection("orders").document(newOrder)
                .set(orders).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("admin_orders").document(newOrder)
                        .set(orders).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Notification notification = new Notification("admin", "طلب جديد", "لقد قام : " + (intent.getStringExtra("person_name") + " بطلب منتج من المتجر") + "", mAuth.getCurrentUser().getUid() + "");
                        db.collection("notification").add(notification)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        if ((intent.getStringExtra("user_shear") + "").length() != 0 && (intent.getStringExtra("order_code_id") + "").length() != 0) {

                                            UsersShear usersShear = new UsersShear(intent.getStringExtra("order_code_id") + "",
                                                    intent.getStringExtra("user_shear") + "", "yet", newOrder + "");
                                            db.collection("usersShear")
                                                    .add(usersShear);
                                        }

                                        ToolUtils.SendNotificationToAdmin("طلب جديد", "لقد قام : " + (intent.getStringExtra("person_name") + " بطلب منتج من المتجر"));
                                        if (paymentDialog.isShowing()) {
                                            paymentDialog.dismiss();
                                        }
                                        Toasty.success(CompleteOrderActivity.this, "تم الاضافة بنجاح..شكرا لك على ثقتك", Toast.LENGTH_LONG).show();

                                        db.collection("user_cart").document(mAuth.getCurrentUser().getUid()+intent.getStringExtra("order_id"))
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

    public void PayAndSend(String price) {

        double total = Double.parseDouble(price);


        db.collection("balance").document(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                double balance = Double.parseDouble(doc.get("user_balance") + "");
                if (balance < total) {
                    Toasty.error(CompleteOrderActivity.this, "عذرا لايوجد رصيد كافي", Toast.LENGTH_SHORT).show();
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
                                                    SendOrder(intent);

                                                }
                                            });

                                }
                            });
                }


            }
        });

    }
}

