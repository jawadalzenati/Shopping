package com.josalla.store;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Fragment.AccountFragment;
import com.josalla.store.Fragment.CategoryFragment;
import com.josalla.store.Fragment.HomeFragment;
import com.josalla.store.Fragment.NotificationFragment;
import com.josalla.store.Fragment.OrderFragment;
import com.josalla.store.service.AppService;
import com.josalla.store.dialogs.StartDialog;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;

public class MainActivity extends AppCompatActivity {
    ImageView ivCart, redDot;
    TextView tvSearch;
    public boolean main_open;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    FirebaseFirestore db;
    FirebaseAuth mAuth;


    Merlin merlin;
    StartDialog startDialog;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.maineFrameLayout, new HomeFragment()).commit();
                    return true;
                case R.id.nav_category:
                    getSupportFragmentManager().beginTransaction().replace(R.id.maineFrameLayout, new CategoryFragment()).commit();
                    return true;
                case R.id.nav_order:
                    getSupportFragmentManager().beginTransaction().replace(R.id.maineFrameLayout, new OrderFragment()).commit();
                    return true;
                case R.id.nav_notification:
                    getSupportFragmentManager().beginTransaction().replace(R.id.maineFrameLayout, new NotificationFragment()).commit();
                    return true;
                case R.id.nav_account:
                    getSupportFragmentManager().beginTransaction().replace(R.id.maineFrameLayout, new AccountFragment()).commit();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivCart = findViewById(R.id.ivCart);
        tvSearch = findViewById(R.id.tvSearch);
        redDot = findViewById(R.id.redDot);

        Intent intent = new Intent(getApplicationContext(), AppService.class);
        startService(intent);

        preferences = getSharedPreferences("MASTEKA", MODE_PRIVATE);
        editor = preferences.edit();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();




        //check if the first time app open
        main_open = preferences.getBoolean("main_open", true);
        Log.e("IsFirstOpen", main_open + "");

        startDialog = new StartDialog(this, preferences.getString("popupImage", ""));

        if (main_open) {
            editor.putBoolean("main_open", false);
            editor.commit();


            db.collection("popup").document("popup")
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot doc) {
                    if ((doc.get("show") + "").equals("yes")) {
                        startDialog.show();
                        startDialog.setCancelable(false);
                        new CountDownTimer(2000, 1000) {
                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {

                            }
                        }.start();

                    }

                }
            });
        }


        Query collRef = db.collection("user_cart").whereEqualTo("user_id", mAuth.getCurrentUser().getUid());
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MAS_ERR", e.getMessage());
                }
                if (!value.isEmpty()) {
                    redDot.setVisibility(View.VISIBLE);
                } else {
                    redDot.setVisibility(View.GONE);
                }

            }
        });

        merlin = new Merlin.Builder().withDisconnectableCallbacks().build(this);


        merlin.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {
                finishAffinity();
                startActivity(new Intent(getApplicationContext(), NoConectionActivity.class));

            }
        });
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });
        //------
        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }
        });
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.home);
        getSupportFragmentManager().beginTransaction().replace(R.id.maineFrameLayout, new HomeFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();
    }


    @Override
    protected void onDestroy() {
        merlin.unbind();
        super.onDestroy();

    }

}
