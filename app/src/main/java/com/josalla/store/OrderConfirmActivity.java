package com.josalla.store;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.model.Notification;
import com.josalla.store.model.Rate;
import com.josalla.store.model.Transaction;
import com.josalla.store.utils.SendNotification;
import com.josalla.store.utils.ToolUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class OrderConfirmActivity extends AppCompatActivity {
    TextView tvNumber, tvOrderPrice, tvSizeColor, tvOrderCount, tvOrderDate;
    ImageView ivBack, ivOrderImage, ivWhatsApp, ivFacebook, ivCall;

    RatingBar rbLeaveFeedback;

    EditText txtFeedback;

    Button btnComplete, btnCancelOrder;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    static String product_id;
    float rate_stars = 5;
    float newRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        ivBack = findViewById(R.id.ivBack);
        tvNumber = findViewById(R.id.tvNumber);
        ivOrderImage = findViewById(R.id.ivOrderImage);
        tvOrderPrice = findViewById(R.id.tvOrderPrice);
        tvSizeColor = findViewById(R.id.tvSizeColor);
        tvOrderCount = findViewById(R.id.tvOrderCount);
        rbLeaveFeedback = findViewById(R.id.rbLeaveFeedback);
        txtFeedback = findViewById(R.id.txtFeedback);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnComplete = findViewById(R.id.btnComplete);

        ivWhatsApp = findViewById(R.id.ivWhatsApp);
        ivFacebook = findViewById(R.id.ivFacebook);
        ivCall = findViewById(R.id.ivCall);

        tvOrderDate = findViewById(R.id.tvOrderDate);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();

        String orderId = intent.getStringExtra("order_confirm_id");
        String orderImage = intent.getStringExtra("order_confirm_image");
        String orderState = intent.getStringExtra("order_state");
        long orderTime = Long.parseLong(intent.getStringExtra("order_time") + "");
        String order_code = intent.getStringExtra("order_code") + "";

        Date currentTime = Calendar.getInstance().getTime();


        Glide.with(this).load(orderImage).into(ivOrderImage);




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


        //------------- get order details ------------------------------------
        db.collection("orders").document(orderId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                tvNumber.setText("رقم الطلب: " + doc.get("order_number"));
                tvOrderPrice.setText("السعر الكلي: " + doc.get("order_price"));
                tvSizeColor.setText("" + doc.get("order_discrp"));
                if (doc.get("order_ammount") == null) {
                    tvOrderCount.setText("الكمية: " + "في الوصف");

                } else {
                    tvOrderCount.setText("الكمية: " + doc.get("order_ammount"));
                }
                tvOrderDate.setText(doc.get("order_date") + "");
                product_id = doc.get("product_id") + "";
            }
        });

        if (orderState.equals("في انتظار الشحن")) {
            btnComplete.setVisibility(View.GONE);

        }


        if (43200000 < (Long.parseLong(String.valueOf(System.currentTimeMillis())) - orderTime)) {
            btnCancelOrder.setVisibility(View.GONE);
        } else {
            if (orderState.equals("تم شحن الطلب")) {
                btnCancelOrder.setVisibility(View.GONE);
            } else {
                btnCancelOrder.setVisibility(View.VISIBLE);
            }
        }

        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(OrderConfirmActivity.this);
                dialog.setTitle("تنبيه!")
                        .setMessage("هل حقا تريد حذف الطلب ؟!")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.dismiss();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                Toasty.success(OrderConfirmActivity.this, "تم حذف الطلب ", Toast.LENGTH_SHORT).show();
                                db.collection("orders").document(orderId).delete();
                                finish();
                            }
                        }).show();


            }
        });

        //-----------------------get rating ------------------------------------
        rbLeaveFeedback.setRating(5);

        rbLeaveFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate_stars = rating;

            }
        });


        //----------------complete order ------------------------------
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //1- set order as completed -----------------
                db.collection("orders").document(orderId).update("order_state", "أكتمل الطلب");
                db.collection("admin_orders").document(orderId).update("order_state", "أكتمل الطلب");


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

                //2- add user rate

                Rate rate = new Rate(product_id, rate_stars + "", txtFeedback.getText() + "", sdf.format(new Date()) + "");
                db.collection("rate")
                        .add(rate).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        //get old rate of product
                        db.collection("products").document(product_id)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot doc) {

                                //get old rate and add int to new rate ;
                                float oldRate = Float.parseFloat(doc.get("product_rate") + "");
                                int numOfRate = Integer.parseInt(doc.get("num_rate") + "");

                                if (numOfRate == 0) {
                                    newRate = rate_stars;
                                    numOfRate = 1;

                                } else if (numOfRate == 1) {
                                    numOfRate += 1;
                                    newRate = (rate_stars + oldRate) / 2;
                                } else {
                                    newRate = (rate_stars + (oldRate * numOfRate)) / (numOfRate + 1);
                                    numOfRate += 1;
                                }

                                db.collection("products").document(product_id).update("product_rate", (newRate + "").substring(0, 3) + "");
                                db.collection("products").document(product_id).update("num_rate", numOfRate + "");


                            }
                        });

                        if (!order_code.equals("")) {
                            Log.e("MAS_Check", "orderHere");

                            db.collection("usersShear").whereEqualTo("order_id", orderId)
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot doc) {
                                    for (QueryDocumentSnapshot document : doc) {

                                        db.collection("usersShear")
                                                .document(document.getId()).update("shear_state", "done");
                                        //send notification to user and add balance
                                        String shear = document.get("user_shear") + "$";
                                        String userShear = document.get("user_shear") + "";

                                        Notification notification = new Notification("user", "اضافة دفعة", "تم اضافة دفعة عن بيع بالعمولة بقيمة: " + shear,
                                                document.get("user_id") + "");

                                        db.collection("notification")
                                                .add(notification);

                                        Transaction transaction = new Transaction(document.get("user_id") + "", "تم اضافة دفعة عن بيع بالعمولة بقيمة: " + shear);
                                        db.collection("transaction")
                                                .add(transaction);

                                        db.collection("balance")
                                                .document(document.get("user_id") + "")
                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot balance) {

                                                double current = Double.parseDouble(balance.get("user_balance") + "");
                                                double new_amount = current + Double.parseDouble(userShear + "");

                                                db.collection("balance").document(balance.getId())
                                                        .update("user_balance", new_amount + "");

                                            }
                                        });

                                        db.collection("users")
                                                .document(document.get("user_id") + "")
                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot userDoc) {
                                                SendNotification.send("اضافة دفعة", "تم اضافة دفعة عن بيع بالعمولة", userDoc.get("user_accessToken") + "");
                                            }
                                        });

                                    }


                                }
                            });

                        }

                        Notification notification = new Notification("admin", "استلام الطلب", "تم تأكيد استلام الطلب رقم : " + orderTime + "", mAuth.getCurrentUser().getUid() + "");
                        db.collection("notification")
                                .add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {


                                ToolUtils.SendNotificationToAdmin("استلام الطلب", "تم تأكيد استلام الطلب رقم : " + orderTime + "");
                                Toasty.success(OrderConfirmActivity.this, "شكرا لتقيمك ", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    }
                });


            }
        });


        //------------finish --------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
