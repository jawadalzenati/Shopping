package com.josalla.store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Merlin;

public class NoConectionActivity extends AppCompatActivity {
    Button btnReTry;
    Merlin merlin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_conection);

        btnReTry = findViewById(R.id.btnReTry);


        btnReTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
            }
        });

        merlin = new Merlin.Builder().withConnectableCallbacks().build(this);

        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                Snackbar snackbar = Snackbar.make(btnReTry, "اعادة الاتصال", Snackbar.LENGTH_LONG);
                snackbar.show();
                finishAffinity();
                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
            }
        });

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
