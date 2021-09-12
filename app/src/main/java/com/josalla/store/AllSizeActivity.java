package com.josalla.store;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class AllSizeActivity extends AppCompatActivity {

    ImageView imageView, imageView2, imageView3, imageView4, imageView6, imageView7, imageView8, imageView9;
    ImageView ivClose;


    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_size);


        db = FirebaseFirestore.getInstance();


        ivClose = findViewById(R.id.ivClose);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);



        db.collection("sizes")
                .document("sizes").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        Glide.with(AllSizeActivity.this).load(doc.get("size")).into(imageView);
                        Glide.with(AllSizeActivity.this).load(doc.get("size5")).into(imageView2);
                        Glide.with(AllSizeActivity.this).load(doc.get("size2")).into(imageView3);
                        Glide.with(AllSizeActivity.this).load(doc.get("size6")).into(imageView4);
                        Glide.with(AllSizeActivity.this).load(doc.get("size4")).into(imageView6);
                        Glide.with(AllSizeActivity.this).load(doc.get("size7")).into(imageView7);
                        Glide.with(AllSizeActivity.this).load(doc.get("size3")).into(imageView8);
                        Glide.with(AllSizeActivity.this).load(doc.get("size7")).into(imageView9);



                    }
                });


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
