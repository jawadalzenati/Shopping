package com.josalla.store;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class PolicyActivity extends AppCompatActivity {

    FirebaseFirestore db;
    TextView tvPolicy;
    ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        tvPolicy = findViewById(R.id.tvPolicy);
        ivBack = findViewById(R.id.ivBack);
        db = FirebaseFirestore.getInstance();




        db.collection("policy").document("policy")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                String policy = (doc.get("policy") + "");

                tvPolicy.setText(policy.replaceAll("nn", "\n"));


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
