package com.josalla.store.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josalla.store.R;
import com.josalla.store.model.Transaction;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Transaction> data;

    public TransactionAdapter(Activity activity, ArrayList<Transaction> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public TransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.transaction_item, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.MyViewHolder holder, final int position) {

        holder.tvText.setText(data.get(position).getT_text());

        if ((data.get(position).getT_text()).contains("اضافة")) {
            holder.ivImage.setImageResource(R.drawable.ic_deposit);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_withdraw);

        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public TextView tvText;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivImage);
            tvText = itemView.findViewById(R.id.tvText);


        }
    }
}