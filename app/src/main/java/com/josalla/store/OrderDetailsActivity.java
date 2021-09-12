package com.josalla.store;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.model.Notification;
import com.josalla.store.utils.SendNotification;
import com.josalla.store.utils.ToolUtils;

import es.dmoral.toasty.Toasty;

public class OrderDetailsActivity extends AppCompatActivity {

    ImageView ivBack;
    ImageView ivOrderImage, tvMap;
    Button btnComplete, btnComplete2, btnCancelOrder;

    TextView tvNumber, tvOrderPrice, tvOrderDescription, tvSizeColor, tvOrderCount, tvMethods, tvOrderDate;
    TextView tvUserName, tvUserPhones, tvUserAddress;


    static String userId;

    FirebaseFirestore db;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        //------------
        db = FirebaseFirestore.getInstance();

        ivBack = findViewById(R.id.ivBack);

        tvNumber = findViewById(R.id.tvNumber);
        tvOrderPrice = findViewById(R.id.tvOrderPrice);
        tvOrderDescription = findViewById(R.id.tvOrderDescription);
        tvSizeColor = findViewById(R.id.tvSizeColor);
        tvOrderCount = findViewById(R.id.tvOrderCount);
        tvMethods = findViewById(R.id.tvMethods);

        btnComplete = findViewById(R.id.btnComplete);
        btnComplete2 = findViewById(R.id.btnComplete2);

        ivOrderImage = findViewById(R.id.ivOrderImage);


        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhones = findViewById(R.id.tvUserPhones);
        tvUserAddress = findViewById(R.id.tvUserAddress);

        tvOrderDate = findViewById(R.id.tvOrderDate);

        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        tvMap = findViewById(R.id.tvMap);

        //------------


        Intent intent = getIntent();
        String order_id = intent.getStringExtra("order_id");
        Log.e("MAS", order_id + "");


        //----- get order information -------------
        ToolUtils.showDialog(this, "جاري تحميل التفاصيل");

        db.collection("admin_orders").document(order_id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) {


                    tvNumber.setText(doc.get("order_number") + "");
                    tvOrderPrice.setText("السعر الاجمالي: " + doc.get("order_price"));
                    tvSizeColor.setText(doc.get("order_discrp") + "");
                    if (doc.get("order_ammount") == null) {
                        tvOrderCount.setText("الكمية: " + "في الوصف");
                    } else {
                        tvOrderCount.setText("الكمية: " + doc.get("order_ammount"));
                    }
                    tvMethods.setText(doc.get("order_payment") + "");
                    tvOrderDate.setText(doc.get("order_date") + "");
                    if ((doc.get("order_state") + "").equals("تم شحن الطلب")) {
                        btnComplete.setVisibility(View.GONE);
                        btnCancelOrder.setVisibility(View.GONE);
                        btnComplete2.setVisibility(View.VISIBLE);
                    }

                    //    --------- get product image name , and description--------------

                    db.collection("products").document(doc.get("product_id") + "")
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            if (doc.exists()) {
                                tvOrderDescription.setText("أسم المنتج: " + doc.get("product_name") + "\n" + "الوصف: " + doc.get("product_description"));
                                String[] images = (doc.get("product_images") + "").split("<IMAGE>");
                                Glide.with(OrderDetailsActivity.this).load(images[1]).into(ivOrderImage);

                            }
                        }
                    });

                    //--------------- get user address , phone and name ---------------------------------
                    db.collection("users").document(doc.get("order_account") + "")
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            if (doc.exists()) {

                                tvUserName.setText(doc.get("user_name") + "");
                                tvUserPhones.setText((doc.get("user_phone") + "").replace("<>", " , "));
                                userId = doc.get("user_id") + "";

                                if ((doc.get("user_address") + "").length() != 0) {
                                    tvUserAddress.setText("العنوان: " + doc.get("user_address"));
                                    if ((doc.get("user_location") + "").length() != 0 && !(doc.get("user_location") + "").equals("<>")) {
                                        tvMap.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    tvUserAddress.setText("تم تحديد العنوان على الخريطة");
                                    tvMap.setVisibility(View.VISIBLE);
                                }


                                ToolUtils.hideDialog();

                                //---------------------- open map ------------------------------------
                                tvMap.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent_map = new Intent(getApplicationContext(), MapsActivity.class);
                                        intent_map.putExtra("location", doc.get("user_location") + "");
                                        startActivity(intent_map);
                                    }
                                });


                            }
                        }
                    });


                }
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(OrderDetailsActivity.this);
                dialog.setTitle("الموافقة واكمال الطلب")
                        .setMessage("هل ستقوم بأرسال الطلب الى الزبون ؟!")
                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.dismiss();

                            }
                        })
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                ToolUtils.showDialog(OrderDetailsActivity.this, "جاري تحديث الطلب");

                                db.collection("admin_orders").document(order_id)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot doc) {
                                        db.collection("users").document(doc.get("order_account") + "")
                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot user_doc) {
                                                SendNotification.send("تم استقبال الطلب", "تم شحن طلبك وسوف يصلك في اقرب وقت ممكن"
                                                        , user_doc.get("user_accessToken") + "");

                                                Notification notification = new Notification("user", "تم استقبال الطلب", "تم استقبال و شحن طلبك وسوف يصلك في اقرب وقت ممكن"
                                                        , userId);

                                                db.collection("notification").add(notification)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                db.collection("orders").document(order_id).update("order_state", "تم شحن الطلب");
                                                                db.collection("admin_orders").document(order_id).update("order_state", "تم شحن الطلب");

                                                                ToolUtils.hideDialog();
                                                                Toasty.success(OrderDetailsActivity.this, "تم تحديث الطلب", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                });


                            }
                        }).show();
            }
        });


        //----- cancel the order
        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(OrderDetailsActivity.this);
                dialog.setTitle("تنبيه!")
                        .setMessage("هل تريد تجاهل هذا الطلب  ؟")
                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                db.collection("orders").document(order_id)
                                        .update("order_state", "لم يعد متوفر في المخزون");

                                db.collection("admin_orders").document(order_id)
                                        .update("order_state", "لم يعد متوفر في المخزون");

                                Toasty.info(OrderDetailsActivity.this, "تم تجاهل الطلب !", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }).show();

            }
        });


        //--------------- finish this activity ---------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
