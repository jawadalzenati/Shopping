package com.josalla.store.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.MyOrderDetailsActivity;
import com.josalla.store.OrderConfirmActivity;
import com.josalla.store.R;
import com.josalla.store.model.Orders;
import com.josalla.store.model.Products;


import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Orders> data;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    static String image;
    boolean isComplete = false;

    public OrdersAdapter(Activity activity, ArrayList<Orders> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public OrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.order_list, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.MyViewHolder holder, final int position) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        db.collection("products").document(data.get(position).getProduct_id())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) {
                    Products products = doc.toObject(Products.class);

                    String[] product_Images = products.getProduct_images().split("<IMAGE>");
                    Glide.with(activity).load(product_Images[1]).into(holder.ivProductImage);
                    image = product_Images[1];
                }
            }
        });


        if (data.get(position).getOrder_ammount()==null){
            holder.tvAmount.setText("الكمية : في الوصف");

        }else {
            holder.tvAmount.setText("الكمية : " + data.get(position).getOrder_ammount());
        }




        holder.tvPrice.setText("السعر الكلي : " + data.get(position).getOrder_price() + " $ ");
        holder.tvDescription.setText("" + data.get(position).getOrder_discrp());
        holder.tvState.setText("الحالة :" + data.get(position).getOrder_state());


        if (data.get(position).getOrder_state().equals("تم شحن الطلب")) {
            holder.tvState.setTextColor(activity.getColor(R.color.green));
        } else if (data.get(position).getOrder_state().equals("أكتمل الطلب")) {
            isComplete = true;
            holder.ivCheck.setVisibility(View.VISIBLE);
            holder.tvState.setTextColor(activity.getColor(R.color.green));
        }
        //---------------delete order

        //----------------------------------------- confirm order--------------------------------
        holder.cdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("MAS_Check" ,"ShareCode: "+ data.get(position).getOrder_code());
                Log.e("MAS_Check" ,"OrderID: "+ data.get(position).getOrder_id());


                if (!data.get(position).getOrder_state().equals("أكتمل الطلب")) {
                    if (data.get(position).getOrder_state().equals("لم يعد متوفر في المخزون")) {
                        Snackbar snackbar = Snackbar
                                .make(holder.cdCard, "نأسف !, هذا المنتج لم يعد متوفر في المخزون", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    } else if (data.get(position).getOrder_state().equals("في انتظار الشحن")) {
                        Intent details_intent = new Intent(activity, MyOrderDetailsActivity.class);
                        details_intent.putExtra("order_confirm_id", data.get(position).getOrder_id());
                        details_intent.putExtra("order_confirm_image", image);
                        details_intent.putExtra("order_state", data.get(position).getOrder_state());
                        details_intent.putExtra("order_time", data.get(position).getOrder_number());
                        details_intent.putExtra("order_code", data.get(position).getOrder_code() + "");
                        activity.startActivity(details_intent);

                    } else {
                        Intent confirm_intnet = new Intent(activity, OrderConfirmActivity.class);
                        confirm_intnet.putExtra("order_confirm_id", data.get(position).getOrder_id());
                        confirm_intnet.putExtra("order_confirm_image", image);
                        confirm_intnet.putExtra("order_state", data.get(position).getOrder_state());
                        confirm_intnet.putExtra("order_time", data.get(position).getOrder_number());
                        confirm_intnet.putExtra("order_code", data.get(position).getOrder_code() + "");
                        activity.startActivity(confirm_intnet);


                    }

                } else {

                    if (data.get(position).getOrder_state().equals("أكتمل الطلب")) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                        dialog.setTitle("لقد اكتمل الطلب")
                                .setMessage("هل تريد حذف الطلب من القائمة")
                                .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                })
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        db.collection("orders").document(data.get(position).getOrder_id()).delete();
                                        data.remove(position);
                                        notifyDataSetChanged();
                                    }
                                }).show();
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cdCard;
        public ImageView ivProductImage, ivCheck;
        public TextView tvAmount, tvPrice, tvDescription, tvState;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cdCard = itemView.findViewById(R.id.cdCard);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvState = itemView.findViewById(R.id.tvState);
            ivCheck = itemView.findViewById(R.id.ivCheck);

        }
    }
}