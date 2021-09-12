package com.josalla.store.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.AddressActivity;
import com.josalla.store.MapsActivity;
import com.josalla.store.R;

public class AddressDialog extends Dialog {
    Activity activity;
    Dialog dialog;
    String userId;

    TextView tvCurrentName, tvCurrentAddress, tvCurrentPhone;
    Button btnCancel, btnChange;
    FirebaseFirestore db;
    ImageView ivMap;
    static String location;

    public AddressDialog(Activity activity, String userId) {
        super(activity);
        this.activity = activity;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_address);

        btnChange = findViewById(R.id.btnChange);
        btnCancel = findViewById(R.id.btnCancel);
        tvCurrentName = findViewById(R.id.tvCurrentName);
        tvCurrentAddress = findViewById(R.id.tvCurrentAddress);
        tvCurrentPhone = findViewById(R.id.tvCurrentPhone);
        ivMap = findViewById(R.id.ivMap);


        db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                tvCurrentName.setText("الأسم: " + doc.get("user_name"));

                if ((doc.get("user_address") + "").length() != 0) {
                    tvCurrentAddress.setText("العنوان: " + doc.get("user_address"));
                    if ((doc.get("user_location") + "").length() != 0 && !(doc.get("user_location") + "").equals("<>")) {
                        ivMap.setVisibility(View.VISIBLE);
                    }
                } else {
                    location = doc.get("user_location") + "";
                    tvCurrentAddress.setText("تم تحديد العنوان على الخريطة");
                    ivMap.setVisibility(View.VISIBLE);
                }

                tvCurrentPhone.setText("رقم الهاتف: " + (doc.get("user_phone") + "").replace("<>", " , "));

            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    dismiss();
                }
                activity.startActivity(new Intent(activity, AddressActivity.class));

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    dismiss();
                }
            }
        });

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MapsActivity.class);
                intent.putExtra("location", location);
                activity.startActivity(intent);
            }
        });

    }
}
