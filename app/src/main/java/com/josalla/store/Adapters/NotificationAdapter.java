package com.josalla.store.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.josalla.store.OrdersActivity;
import com.josalla.store.R;
import com.josalla.store.model.Notification;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Notification> data;

    public NotificationAdapter(Activity activity, ArrayList<Notification> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.notifi_itme, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, final int position) {


        holder.tvText.setText(data.get(position).getNotifi_text());
        holder.tvTitle.setText(data.get(position).getNotifi_title());

        holder.clCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.get(position).getNotifi_title().contains("طلب جديد")){
                    activity.startActivity(new Intent(activity , OrdersActivity.class));
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


        public ConstraintLayout clCard;
        public TextView tvTitle, tvText;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            clCard = itemView.findViewById(R.id.clCard);
            tvText = itemView.findViewById(R.id.tvText);
            tvTitle = itemView.findViewById(R.id.tvTitle);


        }
    }
}