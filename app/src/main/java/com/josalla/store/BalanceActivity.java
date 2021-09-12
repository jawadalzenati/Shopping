package com.josalla.store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.dialogs.AddBalacneDailog;

import es.dmoral.toasty.Toasty;

public class BalanceActivity extends AppCompatActivity {
    public ImageView ivBack , ivMore;
    public Button btnAddBalance;
    public TextView tvCurrentBalance, tvWithdraw, empty;
    public RecyclerView rvTransactions;

    AddBalacneDailog addBalacneDailog;


    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public static String balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        ivBack = findViewById(R.id.ivBack);
        btnAddBalance = findViewById(R.id.btnAddBalance);
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        tvWithdraw = findViewById(R.id.tvWithdraw);
        rvTransactions = findViewById(R.id.rvTransactions);
        empty = findViewById(R.id.empty);
        ivMore =  findViewById(R.id.ivMore);

        addBalacneDailog = new AddBalacneDailog(BalanceActivity.this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        //show the current balance

        db.collection("balance").document(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) {
                    String myBalance = (doc.get("user_balance") + "");
                    if (myBalance.length() > 4) {
                        myBalance = myBalance.substring(0, 5);
                    }
                    tvCurrentBalance.setText(myBalance);
                    balance = doc.get("user_balance") + "";

                }


            }
        });





        // add balance
        btnAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser().isAnonymous()) {
                    Toasty.info(BalanceActivity.this, "يرجى تسجيل الدخول للمتابعة", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(BalanceActivity.this , SingInActivity.class));

                } else {
                    addBalacneDailog.show();

                }
            }
        });


        //tvWithdraw withdraw money form app

        tvWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WithdrawActivity.class);
                intent.putExtra("balance", balance);
                startActivity(intent);
                finish();
            }
        });


        // back to home==========================================
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //=======================================================

        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(BalanceActivity.this , ivMore);
                popupMenu.getMenuInflater().inflate(R.menu.transactions , popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id){
                            case R.id.mTransactions :
                                startActivity(new Intent(getApplicationContext() , TransactionsActivity.class));
                                break;
                        }

                        return true;
                    }
                });
                popupMenu.show();


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (addBalacneDailog.isShowing()) {
            addBalacneDailog.dismiss();
        }
    }


}
