package com.josalla.store.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.R;
import com.josalla.store.model.Notification;
import com.josalla.store.model.PaymentRequests;
import com.josalla.store.utils.SendNotification;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<PaymentRequests> data;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    static String userToken;

    public WithdrawAdapter(Activity activity, ArrayList<PaymentRequests> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public WithdrawAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.payment_request, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WithdrawAdapter.MyViewHolder holder, final int position) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (data.get(position).getRequset_State().equals("done")) {
            holder.btnAccept.setVisibility(View.GONE);
            holder.ivSend.setVisibility(View.VISIBLE);
        } else {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.ivSend.setVisibility(View.GONE);
        }


        db.collection("users").document(data.get(position).getRequest_user_id()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        holder.tvName.setText(" الأسم : " + doc.get("user_name"));
                        holder.tvPhones.setText("رقم الهاتف: " + (doc.get("user_phone")+"").replace("<>" ," , "));
                        userToken = doc.get("user_accessToken") + "";

                    }
                });

        holder.tvEmail.setText(" البريد الالكتروني " + data.get(position).getRequset_user_email());
        holder.tvDate.setText("تاريخ الطلب: " + "  "+data.get(position).getRequest_date());
        holder.tvAmount.setText("المبلغ المطلوب للدفع: " + data.get(position).getRequset_amount() + " $ ");

        db.collection("balance").document(data.get(position).getRequest_user_id())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                holder.tvCurrentBalance.setText(" الرصيد المتبقي : " + doc.get("user_balance"));

            }
        });


        //accept and sent
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = data.get(position).getRequset_user_email();
                String amount = data.get(position).getRequset_amount();


                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("تنبيه!")
                        .setMessage("هل تريد أو قمت بالفعل بأرسال الدفعة ؟")
                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {

                                db.collection("paymentRequests")
                                        .document(data.get(position).getRequest_id())
                                        .update("requset_State", "done");
                                notifyDataSetChanged();


                                SendNotification.send("تم أضافة دفعة", "تم ارسال دفعة بقمية " + amount + " دأ الى حسابك  " + email, userToken);

                                Notification notification = new Notification("user" , "تم ارسال دفعة مالية" ,"تم ارسال دفعة بقمية " + amount + " دأ الى حسابك  " + email
                                        ,data.get(position).getRequest_user_id()+"");
                                db.collection("notification").add(notification)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toasty.success(activity, "تم تأكيد الطلب", Toast.LENGTH_SHORT).show();
                                                userToken = "";
                                            }
                                        });



                            }
                        }).show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvEmail, tvDate, tvCurrentBalance, tvAmount, tvPhones;
        public Button btnAccept;
        public ImageView ivSend;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCurrentBalance = itemView.findViewById(R.id.tvCurrentBalance);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPhones = itemView.findViewById(R.id.tvPhones);

            btnAccept = itemView.findViewById(R.id.btnAccept);
            ivSend = itemView.findViewById(R.id.ivSend);


        }
    }
}