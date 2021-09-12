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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.TransactionAdapter;
import com.josalla.store.Adapters.UsersShearsAdapter;
import com.josalla.store.model.Transaction;
import com.josalla.store.model.UsersShear;

import java.util.ArrayList;
import java.util.Collections;

public class TransactionsActivity extends AppCompatActivity {

    RecyclerView rvTransactions, rvTransactionsYet;
    TextView empty, tvYet, tvDone , tvv;
    ImageView ivBack;

    FirebaseFirestore db;
    FirebaseAuth mAuth;



    ArrayList<Transaction> transactions = new ArrayList<>();
    ArrayList<UsersShear> usersShears = new ArrayList<>();

    TransactionAdapter transactionAdapter;
    UsersShearsAdapter usersShearsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        tvDone = findViewById(R.id.tvDone);
        tvYet = findViewById(R.id.tvYet);
        ivBack = findViewById(R.id.ivBack);
        rvTransactionsYet = findViewById(R.id.rvTransactionsYet);
        rvTransactions = findViewById(R.id.rvTransactions);

        tvv = findViewById(R.id.tvv);

        empty = findViewById(R.id.empty);

        // get transaction history



        Query collRef = db.collection("transaction").whereEqualTo("user_id", mAuth.getCurrentUser().getUid());
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {

                    Log.w("MAS", "Listen failed.", e);
                    return;

                }
                transactions.clear();

                for (QueryDocumentSnapshot doc : value) {
                    Transaction all_trans = doc.toObject(Transaction.class);
                    all_trans.setT_id(doc.getId());
                    transactions.add(all_trans);
                    Collections.reverse(transactions);
                    transactionAdapter.notifyDataSetChanged();

                }
                if (transactions.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                }


            }
        });

        transactionAdapter = new TransactionAdapter(this, transactions);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rvTransactions.setLayoutManager(manager);
        rvTransactions.setAdapter(transactionAdapter);



        // get the pinding
        // get transaction history
        Query collRefPinding = db.collection("usersShear").whereEqualTo("user_id", mAuth.getCurrentUser().getUid());
        collRefPinding.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {

                    Log.w("MAS", "Listen failed.", e);
                    return;
                }
                usersShears.clear();

                for (QueryDocumentSnapshot doc : value) {
                    if (("" + doc.get("shear_state")).equals("yet")){
                        UsersShear usersShear = doc.toObject(UsersShear.class);
                        usersShear.setShear_id(doc.getId());
                        usersShears.add(usersShear);
                        Collections.reverse(usersShears);
                    }
                    usersShearsAdapter.notifyDataSetChanged();

                }




            }
        });
        usersShearsAdapter = new UsersShearsAdapter(this, usersShears);
        RecyclerView.LayoutManager manager2 = new LinearLayoutManager(this);
        rvTransactionsYet.setLayoutManager(manager2);
        rvTransactionsYet.setAdapter(usersShearsAdapter);



        //--------------------------------------------------------------
        //select one
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transactions.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);

                }
                tvv.setVisibility(View.GONE);
                rvTransactionsYet.setVisibility(View.GONE);
                rvTransactions.setVisibility(View.VISIBLE);
                tvDone.setBackground(getDrawable(R.drawable.selected));
                tvYet.setBackground(getDrawable(R.drawable.un_selected));


            }
        });

        tvYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usersShears .size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);

                }

                tvv.setVisibility(View.VISIBLE);
                rvTransactionsYet.setVisibility(View.VISIBLE);
                rvTransactions.setVisibility(View.GONE);
                tvYet.setBackground(getDrawable(R.drawable.selected));
                tvDone.setBackground(getDrawable(R.drawable.un_selected));
            }
        });

        //---------------------------------------------------------------



        //-------------------------------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
