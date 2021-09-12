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

import com.bumptech.glide.Glide;
import com.josalla.store.ProductsPerCategoryActivity;
import com.josalla.store.R;
import com.josalla.store.model.SubCategory;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<SubCategory> data;

    public SubAdapter(Activity activity, ArrayList<SubCategory> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public SubAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.sub_category, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull SubAdapter.MyViewHolder holder, final int position) {


        holder.clCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, ProductsPerCategoryActivity.class);
                intent.putExtra("product_category", data.get(position).getCategory_name());

                activity.startActivity(intent);


            }
        });


        Glide.with(activity).load(data.get(position).getCategory_image()).into(holder.ivImage);
        holder.tvName.setText(data.get(position).getCategory_name());


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout clCard;
        public CircleImageView ivImage;
        public TextView tvName;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            clCard = itemView.findViewById(R.id.clCard);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);


        }
    }
}