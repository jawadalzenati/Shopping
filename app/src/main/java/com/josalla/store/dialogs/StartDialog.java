package com.josalla.store.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.R;

import pl.droidsonroids.gif.GifImageView;

public class StartDialog extends Dialog {
    Dialog dialog;
    Activity activity;

    ImageView tvClose;
    FirebaseFirestore db;
    GifImageView imageView;
    String Image;



    public StartDialog(Activity activity , String Image) {
        super(activity);
        this.activity = activity;
        this.Image = Image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_dialog);

        tvClose = findViewById(R.id.tvClose);
        imageView = findViewById(R.id.imageView);



        Glide.with(activity).load(Image).into(imageView);


        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
