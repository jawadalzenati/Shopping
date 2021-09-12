package com.josalla.store.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.josalla.store.AddBalanceActivity;
import com.josalla.store.R;

import es.dmoral.toasty.Toasty;

public class AddBalacneDailog extends Dialog {
    Activity activity;
    Dialog dialog;
    RadioButton rbPayPall, rbGooglePay;
    Button btnAdd;
    EditText txtAmount;
    String payment;
    TextView tvCancel;

    public AddBalacneDailog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_balance);

        btnAdd = findViewById(R.id.btnAdd);
        rbPayPall = findViewById(R.id.rbPayPall);
        rbGooglePay = findViewById(R.id.rbGooglePay);
        txtAmount = findViewById(R.id.txtAmount);
        tvCancel = findViewById(R.id.tvCancel);


        //select the method --------------
        rbPayPall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    payment = "paypall";
                }
            }
        });

        rbGooglePay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    payment = "google_pay";
                }
            }
        });

        ///===== start add
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((txtAmount.getText() + "").length() == 0) {
                    txtAmount.setError("الرجاء ادخال المبلغ");
                } else {
                    double amount = Double.parseDouble(txtAmount.getText() + "");
                    if (amount < 10) {
                        txtAmount.setError("اقل مبلغ يمكن اضافته هو 10$ ");
                    } else if (((txtAmount.getText().toString()).length()) == 0) {
                        txtAmount.setError("يردى ادخال القيمة المراد اضافتها");
                    } else if (payment == null) {
                        Toasty.error(activity, "الرجاء تحديد طريقة الدفع", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(activity, AddBalanceActivity.class);
                        intent.putExtra("amount", txtAmount.getText() + "");
                        intent.putExtra("payment", payment);

                        activity.startActivity(intent);
                        activity.finish();
                        txtAmount.setText("");
                        dismiss();
                    }
                }
            }
        });


        //cancel
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }


}
