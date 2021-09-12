package com.josalla.store.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josalla.store.R;
import com.josalla.store.model.Rate;

import java.util.ArrayList;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Rate> data;

    public RateAdapter(Activity activity, ArrayList<Rate> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public RateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.rate_item, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull RateAdapter.MyViewHolder holder, final int position) {


        holder.tvTime.setText(data.get(position).getRate_time());
        holder.tvRateText.setText(data.get(position).getRate_text());
        holder.rbRate.setRating(Float.parseFloat(data.get(position).getRate_stars()));


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView tvTime, tvRateText;
        public RatingBar rbRate;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRateText = itemView.findViewById(R.id.tvRateText);
            rbRate = itemView.findViewById(R.id.rbRate);


        }
    }
}