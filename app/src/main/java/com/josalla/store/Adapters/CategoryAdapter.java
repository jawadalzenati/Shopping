package com.josalla.store.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.josalla.store.R;
import com.josalla.store.SubActivity;
import com.josalla.store.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Category> data;

    public CategoryAdapter(Activity activity, ArrayList<Category> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.category_item, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, final int position) {


//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(activity, ProductsPerCategoryActivity.class);
//                intent.putExtra("product_category", data.get(position).getCategory_name());
//                activity.startActivity(intent);
//
//            }
//        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, SubActivity.class);

                intent.putExtra("product_category", data.get(position).getCategory_name());
                activity.startActivity(intent);

            }
        });


        Glide.with(activity).load(data.get(position).getCategory_image()).into(holder.ivCategoryImage);
        holder.tvCategoryName.setText(data.get(position).getCategory_name());


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView ivCategoryImage;
        public TextView tvCategoryName;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);


        }
    }
}