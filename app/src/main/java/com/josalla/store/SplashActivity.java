package com.josalla.store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.josalla.store.utils.ToolUtils;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String newToken = "";

    static String popupImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        preferences = getSharedPreferences("MASTEKA" , MODE_PRIVATE);
        editor = preferences.edit();

        boolean isFirstOpen = preferences.getBoolean("first_open" , true);

        db = FirebaseFirestore.getInstance();



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        // full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();


        db = FirebaseFirestore.getInstance();

        db.collection("popup").document("popup")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                popupImage = (doc.get("popup") + "");

                editor.putString("popupImage", popupImage);
                editor.commit();
            }
        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);

                    if (!ToolUtils.IsConected(SplashActivity.this)) {
                        startActivity(new Intent(getApplicationContext(), NoConectionActivity.class));
                        finish();

                    } else {
                        //Token Should taken here
                        if (user == null || isFirstOpen) {

                            editor.putBoolean("first_open" ,false);
                            editor.commit();

                            Intent intent = new Intent(getApplicationContext(), SingInActivity.class);
                            startActivity(intent);
                            finish();



                        } else {
                            Log.e("MAS_USER" , user.getUid()+"");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        };
        thread.start();

        //SendNotification.send("title","massege");
    }




}
