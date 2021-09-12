package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.RateAdapter;
import com.josalla.store.model.Rate;

import java.util.ArrayList;

public class AllRateActivity extends AppCompatActivity {
    ImageView ivBack;
    RecyclerView rvRate;
    FirebaseFirestore db;
    ArrayList<Rate> data = new ArrayList<>();
    RateAdapter rateAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rate);

        ivBack = findViewById(R.id.ivBack);
        rvRate = findViewById(R.id.rvRate);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String productID = intent.getStringExtra("rate_product_id");


        Query collRef = db.collection("rate").whereEqualTo("rate_product_id", productID);
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.e("MAS", "Listen failed.", e);
                    return;
                }
                data.clear();

                for (QueryDocumentSnapshot doc : value) {
                    Rate rate = doc.toObject(Rate.class);
                    rate.setRate_id(doc.getId());
                    data.add(rate);
                    rateAdapter.notifyDataSetChanged();

                }


            }
        });

        rateAdapter = new RateAdapter(this, data);
        rvRate.setLayoutManager(new LinearLayoutManager(this));
        rvRate.setAdapter(rateAdapter);


        //------------------ back ----------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
