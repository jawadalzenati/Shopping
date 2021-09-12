package com.josalla.store.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.AddressActivity;
import com.josalla.store.AllSizeActivity;
import com.josalla.store.CompleteOrderActivity;
import com.josalla.store.R;
import com.josalla.store.SingInActivity;
import com.josalla.store.model.Cart;
import com.josalla.store.model.Users;
import com.josalla.store.utils.ToolUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class CompleteOrderDialog extends BottomSheetDialog {
    Activity activity;
    Dialog dialog;

    static int count = 1;

    private String id;
    private String image;
    private String price;
    private String colors;
    private String size;
    private String description;
    private String hand_pay;
    private String name;

    ImageView ivProductImage;
    TextView tvProductPrice, tvAllClothesSize, tvRemove, tvCount, tvPlus, tvProductDescription;
    Spinner spColors, spSizes;
    Button btComplete;

    EditText txtCode;

    FirebaseAuth auth;
    FirebaseFirestore db;

    public double NewPrice;

    // public CompleteOrderDialog(Activity activity, String id, String image, String price, String colors, String size) {
    public ArrayList<String> produc_colors = new ArrayList<>();
    public ArrayList<String> produc_sizes = new ArrayList<>();


    public CompleteOrderDialog(Activity activity, String id, String image, String description, String price, String colors, String size, String hand_pay , String name) {
        super(activity);
        this.id = id;
        this.image = image;
        this.price = price;
        this.colors = colors;
        this.size = size;
        this.activity = activity;
        this.description = description;
        this.hand_pay = hand_pay;
        this.name = name;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_order);

        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvAllClothesSize = findViewById(R.id.tvAllClothesSize);
        tvRemove = findViewById(R.id.tvRemove);
        tvCount = findViewById(R.id.tvCount);
        tvPlus = findViewById(R.id.tvPlus);
        spColors = findViewById(R.id.spColors);
        spSizes = findViewById(R.id.spSizes);
        btComplete = findViewById(R.id.btComplete);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        txtCode = findViewById(R.id.txtCode);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        String[] all_colors = colors.split("\\*");
        String[] all_sizes = size.split("\\*");

        produc_colors.addAll(Arrays.asList(all_colors));
        produc_sizes.addAll(Arrays.asList(all_sizes));


        ArrayAdapter<String> colorsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, produc_colors);
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, produc_sizes);
        spColors.setAdapter(colorsAdapter);
        spSizes.setAdapter(sizeAdapter);


        Glide.with(activity).load(image).into(ivProductImage);
        tvProductPrice.setText(price + " $ ");
        tvProductDescription.setText(description);


        final int random = new Random().nextInt(20) + 20; // [0, 60] + 20 => [20, 80]
        String newOrder = System.currentTimeMillis() + "" + random;

        Cart cart = new Cart(id+"" , auth.getCurrentUser().getUid()+"" ,spColors.getSelectedItem()+"" ,spSizes.getSelectedItem()+"" ,count+"" ,
                image+"" ,price+"" ,name+"" ,true ,"",newOrder+"");
        db.collection("user_cart").document(auth.getCurrentUser().getUid()+id)
                .set(cart);


        //-------------- complete order ---------------------------------------
        btComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewPrice = 0.0;
                ToolUtils.showDialog(activity, "جاري الارسال");
                if (auth.getCurrentUser() == null) {
                    Toasty.error(activity, "يجب عليك تسجيل الدخول للمتابعة", Toast.LENGTH_SHORT).show();
                    activity.startActivity(new Intent(activity, SingInActivity.class));
                    ToolUtils.hideDialog();
                } else {
                    db.collection("users").document(auth.getCurrentUser().getUid())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            Users users = doc.toObject(Users.class);
                            if (users.isHas_address()) {

                                if ((txtCode.getText() + "").length() != 0) {

                                    db.collection("pay_codes")
                                            .whereEqualTo("code_text", txtCode.getText() + "")
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot value) {
                                            if (!value.isEmpty()) {
                                                for (QueryDocumentSnapshot doc : value) {

                                                    double discounts = Double.parseDouble(doc.get("code_discounts") + "");
                                                    double shear =  Double.parseDouble(doc.get("code_share") + "");
                                                    double productPrice = Double.parseDouble(price);
                                                    NewPrice = productPrice - ((productPrice / 100.0d) * discounts);
                                                    double userShear = ((productPrice / 100.0d) * shear);

                                                    StartOrderActivity(users, NewPrice + "", doc.get("user_id") + "" , userShear+"");




                                                }

                                            } else {
                                                txtCode.setError("!");
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toasty.error(activity, "رمز غير صحيح", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    StartOrderActivity(users, price, "" , "");
                                }


                            } else {
                                ToolUtils.hideDialog();
                                activity.startActivity(new Intent(activity, AddressActivity.class));
                            }
                            ToolUtils.hideDialog();
                        }

                    });

                }


            }
        });
        //-------------- complete order ---------------------------------------


        tvAllClothesSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, AllSizeActivity.class));
            }
        });


        tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count += 1;
                tvCount.setText(count + "");

            }
        });

        tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count -= 1;
                if (count < 1) {
                    count = 1;
                    Toasty.error(activity, activity.getString(R.string.NotAvailableCount), Toast.LENGTH_SHORT, true).show();
                } else {
                    count -= 1;
                    tvCount.setText(count + "");
                }
            }
        });


        // complete order here  ---------------------------------------------------

//        btComplete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (auth.getCurrentUser() != null) {
//                    long order_number = System.currentTimeMillis();
//                    Log.e("MAS_ORDER", auth.getCurrentUser() + "");
//                    Log.e("MAS_ORDER", "Order Id: " + id + " Order Number: " + order_number + " order_color: " + spColors.getSelectedItem() + " order_size: " + spSizes.getSelectedItem() + " Order Count: " + count);
//                } else {
//                    Toasty.info(activity, "يجب عليك تسجيل الدخول للمتابعة", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });


    }

    public void StartOrderActivity(Users users, String PRICE, String order_code_id , String user_shear) {
        Intent order_intent = new Intent(activity, CompleteOrderActivity.class);

        order_intent.putExtra("order_id", id);
        order_intent.putExtra("order_number", System.currentTimeMillis() + "");
        order_intent.putExtra("order_image", image);
        order_intent.putExtra("order_disc", description);
        order_intent.putExtra("order_price", PRICE);
        order_intent.putExtra("hand_pay", hand_pay);
        order_intent.putExtra("order_size", spSizes.getSelectedItem() + "");
        order_intent.putExtra("order_color", spColors.getSelectedItem() + "");
        order_intent.putExtra("person_name", users.getUser_name());
        order_intent.putExtra("person_address", users.getUser_address() + "");
        order_intent.putExtra("person_location", users.getUser_location() + "");
        order_intent.putExtra("person_phones", users.getUser_phone());
        order_intent.putExtra("order_conut", count + "");
        order_intent.putExtra("order_code_id", order_code_id + "");
        order_intent.putExtra("user_shear", user_shear + "");


        //sendDataHere -------------------------------------------------------------------
        activity.startActivity(order_intent);
    }


}
