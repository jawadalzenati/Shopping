package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.ProdcutsAdapter;
import com.josalla.store.Adapters.SubAdapter;
import com.josalla.store.model.Products;
import com.josalla.store.model.SubCategory;

import java.util.ArrayList;
import java.util.Collections;

public class SubActivity extends AppCompatActivity {

    ImageView ivBack;
    TextView tvPageTitle;
    RecyclerView rvAllCategory, rvAllProducts;

    ArrayList<SubCategory> subCategories = new ArrayList<>();
    ArrayList<Products> all_products = new ArrayList<>();
    SubAdapter subAdapter;
    ProdcutsAdapter prodcutsAdapter;



    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);


        rvAllCategory = findViewById(R.id.rvAllCategory);
        rvAllProducts = findViewById(R.id.rvAllProducts);
        ivBack = findViewById(R.id.ivBack);
        tvPageTitle = findViewById(R.id.tvPageTitle);


        Intent intent = getIntent();
        String main_category = intent.getStringExtra("product_category");

        db = FirebaseFirestore.getInstance();

        tvPageTitle.setText(main_category);

        // get all category =========================================================

        Query collRef = db.collection("sub_category").whereEqualTo("category_main", main_category);
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value
                    , @Nullable FirebaseFirestoreException e) {
                if (!value.isEmpty()) {
                    subCategories.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        SubCategory subs = doc.toObject(SubCategory.class);
                        subs.setCategory_id(doc.getId());
                        subCategories.add(subs);
                        subAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
        subAdapter = new SubAdapter(this, subCategories);
        rvAllCategory.setLayoutManager(new GridLayoutManager(this, 2 , GridLayoutManager.HORIZONTAL ,false));
        rvAllCategory.setAdapter(subAdapter);
        //================================================================================

        // get the product by sub category -------------


        Query sub = db.collection("products").whereEqualTo("product_category", main_category);
        sub.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value
                    , @Nullable FirebaseFirestoreException e) {
                if (!value.isEmpty()) {
                    all_products.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Products products = doc.toObject(Products.class);
                        products.setProduct_id(doc.getId());
                        all_products.add(products);
                        prodcutsAdapter.notifyDataSetChanged();
                        Collections.shuffle(all_products);



                    }
                }


            }

        });



        prodcutsAdapter = new ProdcutsAdapter(this, all_products);
        rvAllProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvAllProducts.setAdapter(prodcutsAdapter);











        //finish ==========================================================================
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
