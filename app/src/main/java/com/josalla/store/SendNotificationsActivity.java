package com.josalla.store;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.josalla.store.utils.SendNotification;

import es.dmoral.toasty.Toasty;

public class SendNotificationsActivity extends AppCompatActivity {
    EditText txtTitle, txtText;
    Button btnSend;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notifications);

        btnSend = findViewById(R.id.btnSend);
        txtTitle = findViewById(R.id.txtTitle);
        txtText = findViewById(R.id.txtText);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtText.getText().length() == 0 || txtTitle.getText().length() == 0) {
                    Toasty.error(SendNotificationsActivity.this, "جميع الحقول مطلوبة", Toast.LENGTH_SHORT).show();
                } else {
                    SendNotification.send(txtTitle.getText() + "", txtText.getText() + "");
                    Toasty.success(SendNotificationsActivity.this, "تم ارسال الاشعار", Toast.LENGTH_SHORT).show();
                    finish();

                }


            }
        });
    }
}
