package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.WithdrawAdapter;
import com.josalla.store.model.PaymentRequests;

import java.util.ArrayList;
import java.util.Collections;

public class WithdrawListActivity extends AppCompatActivity {
    ImageView ivBack;
    RecyclerView rvWithdraw, rvWithdrawDone;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    TextView tvWithdraw, tvDone, tvYet;

    ArrayList<PaymentRequests> paymentRequests = new ArrayList<>();
    ArrayList<PaymentRequests> paymentDone = new ArrayList<>();

    WithdrawAdapter withdrawAdapter, withdrawAdapterDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_list);

        ivBack = findViewById(R.id.ivBack);
        rvWithdraw = findViewById(R.id.rvWithdraw);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvWithdraw = findViewById(R.id.tvWithdraw);
        rvWithdrawDone = findViewById(R.id.rvWithdrawDone);

        tvDone = findViewById(R.id.tvDone);
        tvYet = findViewById(R.id.tvYet);


        CollectionReference collRef = db.collection("paymentRequests");
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("mas", "Listen failed.", e);
                    return;
                }
                paymentRequests.clear();

                for (QueryDocumentSnapshot doc : value) {
                    PaymentRequests payment = doc.toObject(PaymentRequests.class);
                    payment.setRequest_id(doc.getId());

                    if ((doc.get("requset_State") + "").equals("done")) {
                        paymentDone.add(payment);
                    } else {
                        paymentRequests.add(payment);

                    }

                    withdrawAdapter.notifyDataSetChanged();
                    withdrawAdapterDone.notifyDataSetChanged();

                    Collections.reverse(paymentRequests);
                    Collections.reverse(paymentDone);


                }
                if (paymentRequests.size() == 0) {
                    tvWithdraw.setVisibility(View.VISIBLE);
                } else {
                    tvWithdraw.setVisibility(View.GONE);

                }


            }
        });


        withdrawAdapter = new WithdrawAdapter(this, paymentRequests);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);

        rvWithdraw.setLayoutManager(manager);
        rvWithdraw.setAdapter(withdrawAdapter);


        withdrawAdapterDone = new WithdrawAdapter(this, paymentDone);
        RecyclerView.LayoutManager manager2 = new LinearLayoutManager(this);

        rvWithdrawDone.setLayoutManager(manager2);
        rvWithdrawDone.setAdapter(withdrawAdapterDone);


        //select one
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymentDone.size() == 0) {
                    tvWithdraw.setVisibility(View.VISIBLE);
                } else {
                    tvWithdraw.setVisibility(View.GONE);

                }

                rvWithdraw.setVisibility(View.GONE);
                rvWithdrawDone.setVisibility(View.VISIBLE);
                tvDone.setBackground(getDrawable(R.drawable.selected));
                tvYet.setBackground(getDrawable(R.drawable.un_selected));


            }
        });

        tvYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (paymentRequests .size() == 0) {
                    tvWithdraw.setVisibility(View.VISIBLE);
                } else {
                    tvWithdraw.setVisibility(View.GONE);

                }

                rvWithdraw.setVisibility(View.VISIBLE);
                rvWithdrawDone.setVisibility(View.GONE);
                tvYet.setBackground(getDrawable(R.drawable.selected));
                tvDone.setBackground(getDrawable(R.drawable.un_selected));
            }
        });

        //-------------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
