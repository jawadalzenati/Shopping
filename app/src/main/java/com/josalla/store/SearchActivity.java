package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.ProdcutsAdapter;
import com.josalla.store.model.Products;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ImageView ivBack;
    SearchView svSearch;
    RecyclerView rvSearchResult;

    SpinKitView spin_kit;
    FirebaseFirestore db;

    ConstraintLayout clEmpty;


    ArrayList<Products> search_result = new ArrayList<>();

    ProdcutsAdapter prodcutsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ivBack = findViewById(R.id.ivBack);
        svSearch = findViewById(R.id.svSearch);
        rvSearchResult = findViewById(R.id.rvSearchResult);

        db = FirebaseFirestore.getInstance();

        clEmpty = findViewById(R.id.clEmpty);
        svSearch.setFocusable(true);
        svSearch.onActionViewExpanded();

        ProgressBar progressBar = findViewById(R.id.spin_kit);
        Sprite doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        svSearch.setQueryHint("أبحث هنا...");

        progressBar.setVisibility(View.GONE);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                progressBar.setVisibility(View.VISIBLE);

                if (newText.length() != 0) {
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
                                prodcutsAdapter.notifyDataSetChanged();

                            }

                            if (search_result.size() == 0) {
                                clEmpty.setVisibility(View.VISIBLE);
                            } else {
                                clEmpty.setVisibility(View.GONE);

                            }
                        }
                    });

                    progressBar.setVisibility(View.GONE);

                    prodcutsAdapter = new ProdcutsAdapter(SearchActivity.this, search_result);
                    rvSearchResult.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                    rvSearchResult.setAdapter(prodcutsAdapter);

                } else {
                    search_result.clear();
                    progressBar.setVisibility(View.GONE);

                }

                return false;
            }
        });


        //---- finish ------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
