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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.FavProductAdapter;
import com.josalla.store.model.Products;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    RecyclerView rvFavorite;
    ImageView ivBack;
    FavProductAdapter favProductAdapter;
    ArrayList<Products> fav_products = new ArrayList<>();

    ConstraintLayout clEmpty;
    FirebaseFirestore db;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        rvFavorite = findViewById(R.id.rvFavorite);
        ivBack = findViewById(R.id.ivBack);

        clEmpty = findViewById(R.id.clEmpty);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();


        Query collRef = db.collection("favorite").whereEqualTo("favorite_user_id", user.getUid());
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {




                if (e != null) {
                    Log.e("MAS", "Listen failed.", e);

                    return;
                }

                fav_products.clear();
                for (QueryDocumentSnapshot doc : value) {
                    db.collection("products").document(doc.get("favorite_product_id") + "")
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {
                            if (document.exists()) {
                                Products products = document.toObject(Products.class);
                                products.setProduct_id(document.getId());

                                fav_products.add(products);
                                favProductAdapter.notifyDataSetChanged();

                                if (fav_products.size() > 0) {
                                    clEmpty.setVisibility(View.GONE);


                                } else {
                                    clEmpty.setVisibility(View.VISIBLE);


                                }

                            } else {


                            }
                        }
                    });

                }


            }

        });


        favProductAdapter = new FavProductAdapter(this, fav_products);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
        rvFavorite.setLayoutManager(manager);
        rvFavorite.setAdapter(favProductAdapter);


        //-------------------------finish -----------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
