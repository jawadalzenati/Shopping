package com.josalla.store.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.R;
import com.josalla.store.OrderDetailsActivity;
import com.josalla.store.model.Orders;


import java.util.ArrayList;

public class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Orders> data;
    FirebaseFirestore db;
    static String[] images;
    static String name;

    public AllOrdersAdapter(Activity activity, ArrayList<Orders> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public AllOrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.all_orders_list, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AllOrdersAdapter.MyViewHolder holder, final int position) {


        db = FirebaseFirestore.getInstance();

        db.collection("products").document(data.get(position).getProduct_id())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) {

                    Orders orders = doc.toObject(Orders.class);
                    orders.setOrder_id(doc.getId());


                    images = (doc.get("product_images") + "").split("<IMAGE>");
                    name = doc.get("product_name") + "";
                    Glide.with(activity).load(images[1]).into(holder.ivProductImage);
                    holder.tvProductName.setText(name);

                }
            }
        });

        if (data.get(position).getOrder_state().equals("تم شحن الطلب")) {
            holder.tvState.setTextColor(activity.getColor(R.color.green));
        }


        holder.tvPrice.setText("السعر الاجمالي: " + data.get(position).getOrder_price()+" $ ");
        if (data.get(position).getOrder_state().equals("أكتمل الطلب")) {
            holder.ivComplete.setVisibility(View.VISIBLE);
            holder.tvState.setTextColor(activity.getColor(R.color.green));
            holder.tvState.setText(data.get(position).getOrder_state());

        } else {
            holder.tvState.setText(data.get(position).getOrder_state());
        }

        if (data.get(position).getOrder_ammount()==null){
            holder.tvAmount.setText("عدد القطع: في الوصف");

        }else {
            holder.tvAmount.setText("عدد القطع: " + data.get(position).getOrder_ammount());
        }

        holder.cdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                    db.collection("admin_orders").document(data.get(position).getOrder_id()).delete();
                                    data.remove(position);
                                    notifyDataSetChanged();
                                }
                            }).show();

                } else {

                    Intent intent = new Intent(activity, OrderDetailsActivity.class);
                    intent.putExtra("order_id", data.get(position).getOrder_id());
                    activity.startActivity(intent);
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
        public ImageView ivProductImage, ivComplete;
        public TextView tvProductName, tvPrice, tvState, tvAmount;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cdCard = itemView.findViewById(R.id.cdCard);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvState = itemView.findViewById(R.id.tvState);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            ivComplete = itemView.findViewById(R.id.ivComplete);

        }
    }
}