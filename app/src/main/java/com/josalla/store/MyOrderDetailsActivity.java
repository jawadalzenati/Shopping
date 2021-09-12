package com.josalla.store;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.model.Transaction;

import es.dmoral.toasty.Toasty;

public class MyOrderDetailsActivity extends AppCompatActivity {
    ImageView ivBack, ivOrderImage, ivFacebook, ivCall, ivWhatsApp;
    TextView tvNumber, tvOrderPrice, tvSizeColor, tvOrderCount, tvOrderDate;
    Button btnCancelOrder;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    static double toatlePrice;
    static String order_payment, orderDate;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);

        ivBack = findViewById(R.id.ivBack);

        ivOrderImage = findViewById(R.id.ivOrderImage);
        tvNumber = findViewById(R.id.tvNumber);
        tvOrderPrice = findViewById(R.id.tvOrderPrice);
        tvSizeColor = findViewById(R.id.tvSizeColor);
        tvOrderCount = findViewById(R.id.tvOrderCount);

        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        tvOrderDate = findViewById(R.id.tvOrderDate);



        ivFacebook = findViewById(R.id.ivFacebook);
        ivCall = findViewById(R.id.ivCall);
        ivWhatsApp = findViewById(R.id.ivWhatsApp);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String image = intent.getStringExtra("order_confirm_image");
        String id = intent.getStringExtra("order_confirm_id");
        String order_time = intent.getStringExtra("order_time");
        Glide.with(this).load(image).into(ivOrderImage);

        orderDate = "";


        db.collection("orders").document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) {
                    tvNumber.setText("رقم الطلب: " + doc.get("order_number"));
                    orderDate = doc.get("order_number") + "";
                    tvOrderPrice.setText("السعر الكلي: " + doc.get("order_price"));
                    tvSizeColor.setText("" + doc.get("order_discrp"));
                    if (doc.get("order_ammount")==null){
                        tvOrderCount.setText("الكمية: في الوصف");
                    }else {
                    tvOrderCount.setText("الكمية: " + doc.get("order_ammount"));}
                    tvOrderDate.setText(doc.get("order_date") + "");
                    toatlePrice = Double.parseDouble((doc.get("order_price") + ""));
                    order_payment = doc.get("order_payment") + "";




                } else {

                }
            }
        });

        //cancel order and back money to user --------




        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrderDetailsActivity.this);
                dialog.setTitle("تنبيه!")
                        .setMessage("هل حقا تريد حذف الطلب ؟!")
                        .setNegativeButton("الغائ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.dismiss();
                            }
                        })
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {



                                if (!order_payment.equals("الدفع باليد")) {

                                    db.collection("balance")
                                            .document(mAuth.getCurrentUser().getUid())
                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot doc) {

                                            double current = Double.parseDouble(doc.get("user_balance") + "");
                                            double newBalance = current + toatlePrice;

                                            db.collection("balance")
                                                    .document(mAuth.getCurrentUser().getUid())
                                                    .update("user_balance", newBalance + "");

                                            Transaction transaction = new Transaction(mAuth.getCurrentUser().getUid() + "", "تم ارجاع مبلغ بقيمة: " + toatlePrice + " د.أ ");
                                            newBalance = 0.0;
                                            toatlePrice = 0.0;

                                            db.collection("transaction")
                                                    .add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toasty.success(MyOrderDetailsActivity.this, "تم حذف الطلب , واضافة المبلغ الى رصيدك في التطبيق", Toast.LENGTH_LONG).show();
                                                    db.collection("orders").document(id).delete();
                                                    db.collection("admin_orders").document(id).delete();

                                                    finish();

                                                }
                                            });


                                        }
                                    });
                                } else {
                                    Toasty.success(MyOrderDetailsActivity.this, "تم حذف الطلب ", Toast.LENGTH_LONG).show();
                                    db.collection("orders").document(id).delete();
                                    db.collection("admin_orders").document(id).delete();

                                    finish();
                                }


                            }
                        }).show();


            }
        });


        //==============================if order have more that 24 then  cant cancel ===================================
        long currentTime = System.currentTimeMillis();
        long orderTime = Long.parseLong(order_time + "");

        if (43200000 < (currentTime - orderTime)) {
            btnCancelOrder.setVisibility(View.GONE);
        } else {
            btnCancelOrder.setVisibility(View.VISIBLE);

        }





        //==============================================================================================================
        //conect us ------------------------------
        ivWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("contacts").document("contacts")
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        Uri uri = Uri.parse("smsto:" + doc.get("whatsapp"));
                        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                        i.setPackage("com.whatsapp");
                        startActivity(Intent.createChooser(i, "استفسار"));

                    }
                });
            }
        });

        //--
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("contacts").document("contacts")
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + doc.get("mobile")));
                        startActivity(intent);

                    }
                });
            }
        });

        //---

        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                db.collection("contacts").document("contacts")
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {

                        String FACEBOOK_URL = "https://www.facebook.com/" + doc.get("facebook");
                        String FACEBOOK_PAGE_ID = "" + doc.get("facebook");
                        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);

                        PackageManager packageManager = getPackageManager();
                        try {
                            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                            if (versionCode >= 3002850) { //newer versions of fb app
                                String facebookUrl = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
                                facebookIntent.setData(Uri.parse(facebookUrl));
                                startActivity(facebookIntent);
                            } else { //older versions of fb app
                                String facebookUrl = "fb://page/" + FACEBOOK_PAGE_ID;
                                facebookIntent.setData(Uri.parse(facebookUrl));
                                startActivity(facebookIntent);
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            facebookIntent.setData(Uri.parse(FACEBOOK_URL));
                            startActivity(facebookIntent);
                        }


                    }
                });
            }
        });


        //finish
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
