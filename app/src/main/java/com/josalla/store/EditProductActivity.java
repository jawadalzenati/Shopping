package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.AllProductAdapter;
import com.josalla.store.model.Products;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class EditProductActivity extends AppCompatActivity {
    ArrayList<Products> all_product = new ArrayList<>();
    ArrayList<Products> search_result = new ArrayList<>();


    AllProductAdapter allProductAdapter ,searchAdapter;
    RecyclerView rvAllProducts , rvSearchResult;
    FirebaseFirestore db;
    ImageView ivBack;

    SearchView svSearch ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        rvAllProducts = findViewById(R.id.rvAllProducts);
        ivBack = findViewById(R.id.ivBack);
        svSearch = findViewById(R.id.svSearch);
        rvSearchResult = findViewById(R.id.rvSearchResult);

        db = FirebaseFirestore.getInstance();


        CollectionReference collRef = db.collection("products");
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.e("MAS", "Listen failed.", e);
                    return;
                }
                all_product.clear();

                for (QueryDocumentSnapshot doc : value) {
                    Products products = doc.toObject(Products.class);
                    products.setProduct_id(doc.getId());
                    all_product.add(products);
                    allProductAdapter.notifyDataSetChanged();

                }


            }
        });

        allProductAdapter = new AllProductAdapter(this, all_product);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);

        rvAllProducts.setLayoutManager(manager);
        rvAllProducts.setAdapter(allProductAdapter);


        //-------------- search

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {
                    rvAllProducts.setVisibility(View.GONE);
                    rvSearchResult.setVisibility(View.VISIBLE);


                    Query collRef = db.collection("products").orderBy("product_name").startAt(newText).endAt(newText + "\uf8ff");
                    collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.e("MAS", "Listen failed.", e);
                                return;
                            }
                            search_result.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                Products products = doc.toObject(Products.class);
                                products.setProduct_id(doc.getId());
                                search_result.add(products);
                                searchAdapter.notifyDataSetChanged();

                            }

                            if (search_result.size() == 0) {
                                Toasty.info(EditProductActivity.this, "لايوجد نتائج", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                    searchAdapter = new AllProductAdapter(EditProductActivity.this, search_result);
                    rvSearchResult.setLayoutManager(new GridLayoutManager(EditProductActivity.this, 1));
                    rvSearchResult.setAdapter(searchAdapter);


                } else {
                    rvAllProducts.setVisibility(View.VISIBLE);
                    rvSearchResult.setVisibility(View.GONE);
                }


                return false;

            }

        });



        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
