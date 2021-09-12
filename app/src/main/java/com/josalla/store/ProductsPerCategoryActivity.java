package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import com.josalla.store.model.Products;

import java.util.ArrayList;

public class ProductsPerCategoryActivity extends AppCompatActivity {

    ConstraintLayout clEmptyCategory;
    ArrayList<Products> all_products = new ArrayList<>();
    ProdcutsAdapter prodcutsAdapter;

    ImageView ivBack ,ivFavList;

    TextView tvCategoryTitle , tvSearch;

    FirebaseFirestore db;
    RecyclerView rvProducts;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_per_category);

        clEmptyCategory = findViewById(R.id.clEmptyCategory);
        ivBack = findViewById(R.id.ivBack);
        rvProducts = findViewById(R.id.rvProducts);

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvSearch = findViewById(R.id.tvSearch);
        ivFavList = findViewById(R.id.ivFavList);




        Intent intent = getIntent();
        db = FirebaseFirestore.getInstance();




        String product_category = intent.getStringExtra("product_category");
        tvCategoryTitle.setText("الفئة الحالية : "+product_category);


        //get products by category --------------------------------------------


        Query collRef = db.collection("products").whereEqualTo("product_sub_category", product_category);
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                        if (all_products.size() > 0) {
                            clEmptyCategory.setVisibility(View.GONE);
                        }

                    }
                }


            }
        });


        prodcutsAdapter = new ProdcutsAdapter(this, all_products);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(prodcutsAdapter);


        //------ back to home --------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //---------------------------------------------
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });


        //------
        ivFavList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FavoriteActivity.class));
            }
        });


    }


}
