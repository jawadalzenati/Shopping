package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.CartAdapter;
import com.josalla.store.model.Cart;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class CartActivity extends AppCompatActivity {

    RecyclerView rvCart;
    ImageView ivBack;
    ConstraintLayout empty;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    ArrayList<Cart> myCarts = new ArrayList<>();
    CartAdapter cartAdapter;




    Button btnComplete;
    double total = 0.0;

    TextView tvTotalPrice;
    static String selectedProductId;
    static String selectedDiscrip;
    static String shareCodes;
    static String orders_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        empty = findViewById(R.id.empty);
        rvCart = findViewById(R.id.rvCart);
        ivBack = findViewById(R.id.ivBack);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnComplete = findViewById(R.id.btnComplete);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);






        Query collRef = db.collection("user_cart").whereEqualTo("user_id", mAuth.getCurrentUser().getUid() + "");
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                total = 0.0;
                selectedProductId = "";
                selectedDiscrip = "";
                shareCodes = "";
                orders_id = "";

                if (e != null) {
                    Log.w("MAS", "Listen failed.", e);
                    return;
                }
                myCarts.clear();

                for (QueryDocumentSnapshot doc : value) {
                    if (doc.exists()) {
                        if (!isFinishing())

                        //check if the product is still in product data base-------
                        db.collection("products")
                                .document(doc.get("product_id") + "")
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot productDoc) {
                                if (productDoc.exists()) {
                                    //if product is exist then get the product details
                                    Cart cart = doc.toObject(Cart.class);
                                    cart.setUser_id(doc.getId());
                                    myCarts.add(cart);
                                    if (cart.isSet_selected()) {
                                        total = total + Double.parseDouble(doc.get("total_price") + "");
                                        selectedProductId = productDoc.getId() + "<SELECTED>" + selectedProductId;
                                        String discripe = "اللون: " + cart.getSelected_color() + " ,العدد: " + cart.getProduct_count() + " , " + cart.getSelected_size();
                                        selectedDiscrip = discripe + "<DISCRIP>" + selectedDiscrip;
                                        shareCodes = cart.getShear_code() + "<SHARED>" + shareCodes;
                                        orders_id = cart.getOrder_id() + "<ORDER>" + orders_id;
                                        discripe = "";
                                    }

                                    cartAdapter.notifyDataSetChanged();
                                    //102.0359
                                    if ((total + "").length() > 6) {

                                        String  totalPrice = (total+"").substring(0,5);
                                        tvTotalPrice.setText(totalPrice + "$");
                                    } else {
                                        tvTotalPrice.setText(total + "$");
                                    }
                                    if (myCarts.size() > 0) {
                                        empty.setVisibility(View.GONE);
                                    }


                                } else {
                                    Log.e("MAS", "product is not exist");

                                }
                            }
                        });
                    } else {
                        Log.e("MAS", "product is not exist");

                    }


                }
                if (myCarts.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                    total = 0.0;
                    tvTotalPrice.setText(total + "$");
                    new CountDownTimer(1000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {

                        }
                    }.start();

                }

            }
        });


        cartAdapter = new CartAdapter(CartActivity.this, myCarts);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);


        //-------------------- to complete the order ----------------------
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total != 0.0) {

                    db.collection("users")
                            .document(mAuth.getCurrentUser().getUid())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            if (doc.exists()) {
                                if ((doc.get("has_address") + "").equals("true")) {

                                    Intent intent = new Intent(getApplicationContext(), PayActivity.class);
                                    intent.putExtra("price_toPay", total + "");
                                    intent.putExtra("selectedProductId", selectedProductId);
                                    intent.putExtra("selectedDiscrip", selectedDiscrip);
                                    intent.putExtra("shareCodes", shareCodes);
                                    intent.putExtra("orders_ids", orders_id);
                                    startActivity(intent);
                                } else {
                                    Toasty.info(CartActivity.this, "يرجى اضافة عنوان للأستمرار", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(CartActivity.this, AddressActivity.class));

                                }

                            } else {

                                Toasty.error(CartActivity.this, "هناك خطأ ما!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } else {
                    Snackbar.make(btnComplete, "عذراً .. العربة فارغة , او لايوجد عنصار محددة", Snackbar.LENGTH_SHORT).setTextColor(Color.YELLOW).show();
                }


            }
        });

        //------------------ finish this activity ----------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
