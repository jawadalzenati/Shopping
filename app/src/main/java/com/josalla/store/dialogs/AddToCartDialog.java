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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.AllSizeActivity;
import com.josalla.store.R;
import com.josalla.store.model.Cart;
import com.josalla.store.model.UsersShear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class AddToCartDialog extends BottomSheetDialog {
    Activity activity;
    Dialog dialog;

    static int count = 1;

    FirebaseFirestore db;
    FirebaseAuth mAuth;


    ImageView ivProductImage, ivClose;
    TextView tvProductPrice, tvProductDescription, tvCount, tvRemove, tvPlus, tvAllClothesSize;
    Spinner spColors, spSizes;
    EditText txtCode;
    Button btnAddToCart;



    public ArrayList<String> produc_colors = new ArrayList<>();
    public ArrayList<String> produc_sizes = new ArrayList<>();

    String productId, image, price, discrp, colors, size, prduct_name;

    private double NewPrice;


    public AddToCartDialog(Activity activity, String product_id, String image, String price, String discrp, String colors, String size, String prduct_name) {
        super(activity);
        this.activity = activity;
        this.productId = product_id;
        this.image = image;
        this.price = price;
        this.discrp = discrp;
        this.colors = colors;
        this.size = size;
        this.prduct_name = prduct_name;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_cart);

        ivClose = findViewById(R.id.ivClose);
        tvAllClothesSize = findViewById(R.id.tvAllClothesSize);

        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvCount = findViewById(R.id.tvCount);
        tvRemove = findViewById(R.id.tvRemove);
        tvPlus = findViewById(R.id.tvPlus);
        spColors = findViewById(R.id.spColors);
        spSizes = findViewById(R.id.spSizes);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        txtCode = findViewById(R.id.txtCode);
        //------------------------------------------------------------------------




        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Glide.with(activity).load(image).into(ivProductImage);
        tvProductPrice.setText("السعر: " + price);
        tvProductDescription.setText("الوصف: " + discrp);

        // get color and size ----------------------------------------
        String[] all_colors = colors.split("\\*");
        String[] all_sizes = size.split("\\*");

        produc_colors.addAll(Arrays.asList(all_colors));
        produc_sizes.addAll(Arrays.asList(all_sizes));


        ArrayAdapter<String> colorsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, produc_colors);
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, produc_sizes);
        spColors.setAdapter(colorsAdapter);
        spSizes.setAdapter(sizeAdapter);

        //======= add cont or remove  =====================

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


        //================= add to cart ----------------------------
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double totlePrice = Double.parseDouble(price) * count;


                if ((txtCode.getText() + "").length() != 0) {

                    db.collection("pay_codes")
                            .whereEqualTo("code_text", txtCode.getText() + "")
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot value) {
                            if (!value.isEmpty()) {
                                for (QueryDocumentSnapshot doc : value) {

                                    double discounts = Double.parseDouble(doc.get("code_discounts") + "");
                                    double shear = Double.parseDouble(doc.get("code_share") + "");
                                    double productPrice = Double.parseDouble(price);
                                    NewPrice = productPrice - ((productPrice / 100.0d) * discounts);
                                    double userShear = ((productPrice / 100.0d) * shear);

                                    final int random = new Random().nextInt(20) + 20; // [0, 60] + 20 => [20, 80]
                                    String newOrder = System.currentTimeMillis() + "" + random;

                                    UsersShear usersShear = new UsersShear(doc.get("user_id") + "",
                                            userShear+ "", "yet", newOrder + "");
                                    db.collection("usersShear")
                                            .add(usersShear);


                                    String orderCode = txtCode.getText()+"";
                                    AddToCart(NewPrice + "" , orderCode+"" , newOrder+"");


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
                    final int random = new Random().nextInt(20) + 20; // [0, 60] + 20 => [20, 80]
                    String newOrder = System.currentTimeMillis() + "" + random;

                    AddToCart(price + "" , "" ,newOrder+"");

                }


            }
        });


        //=================== close ==========================
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing())
                    dismiss();
            }
        });

        //================== to show all cloth size

        tvAllClothesSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, AllSizeActivity.class));
            }
        });


    }

    private void AddToCart(String price , String orderCode , String order_id) {

        Cart cart = new Cart(productId + "", mAuth.getCurrentUser().getUid() + "", spColors.getSelectedItem() + "", spSizes.getSelectedItem() + ""
                , count + "", image + "", price + "", prduct_name + "", true , orderCode+"" ,order_id+"");
        db.collection("user_cart")
                .document(mAuth.getCurrentUser().getUid() + productId)
                .set(cart)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toasty.success(activity, "تمت الاضافة الى العربة", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }

}
