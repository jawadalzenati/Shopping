package com.josalla.store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.model.PayCodes;

import es.dmoral.toasty.Toasty;

public class EranActivity extends AppCompatActivity {

    TextView tvCopy, tvCurrentCode;
    Button btnCode;

    ImageView ivBack;

    FirebaseFirestore db;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eran);

        btnCode = findViewById(R.id.btnCode);
        tvCopy = findViewById(R.id.tvCopy);
        tvCurrentCode = findViewById(R.id.tvCurrentCode);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ivBack = findViewById(R.id.ivBack);






        //read the code
        db.collection("pay_codes")
                .document(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) {
                    tvCurrentCode.setText(doc.get("code_text") + "");
                } else {
                    tvCurrentCode.setText("لا يوجد لديك رمز بعد");

                }


            }
        });


        //get new code -----------------------------------------------
        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String id = (mAuth.getCurrentUser().getUid() + "").substring(0, 5);
                String time = System.currentTimeMillis() + "";
                String s_code = time.substring((time.length() - 6), time.length() - 1);
                String UserCode = id + s_code + "";
                PayCodes payCodes = new PayCodes(mAuth.getCurrentUser().getUid() + "", UserCode + "" , "5" , "3");
                db.collection("pay_codes").document(mAuth.getCurrentUser().getUid()).
                        set(payCodes).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tvCurrentCode.setText(UserCode);


                    }
                });


            }
        });

        //---- code copy

        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Text", tvCurrentCode.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toasty.success(EranActivity.this, "تم نسخ الرمز الى الحافظة", Toast.LENGTH_SHORT).show();
            }
        });


        //----------------------------finish ------------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
