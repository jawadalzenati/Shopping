package com.josalla.store.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.ProductDetailsActivity;
import com.josalla.store.R;
import com.josalla.store.model.Products;


import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class FavProductAdapter extends RecyclerView.Adapter<FavProductAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Products> data;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public FavProductAdapter(Activity activity, ArrayList<Products> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public FavProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.fav_list, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull FavProductAdapter.MyViewHolder holder, final int position) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, ProductDetailsActivity.class);
                intent.putExtra("id" , data.get(position).getProduct_id());
                intent.putExtra("name" , data.get(position).getProduct_name());
                intent.putExtra("category" , data.get(position).getProduct_category());
                intent.putExtra("images" , data.get(position).getProduct_images());
                intent.putExtra("price" , data.get(position).getProduct_price());
                intent.putExtra("description" , data.get(position).getProduct_description());
                intent.putExtra("colors" ,data.get(position).getProduct_colors());
                intent.putExtra("sizes" ,data.get(position).getProduct_sizes());
                intent.putExtra("rate" , data.get(position).getProduct_rate());
                intent.putExtra("handPay" , data.get(position).getHand_pay());
                intent.putExtra("discrip1" ,data.get(position).getProduct_discrip1());
                intent.putExtra("discrip2" ,data.get(position).getProduct_discrip2());
                intent.putExtra("discrip3" ,data.get(position).getProduct_discrip3());
                intent.putExtra("discrip4" ,data.get(position).getProduct_discrip4() );
                intent.putExtra("discrip5" , data.get(position).getProduct_discrip5());

                activity.startActivity(intent);


            }
        });

        String[] product_Images = data.get(position).getProduct_images().split("<IMAGE>");

        Glide.with(activity).load(product_Images[1]).into(holder.ivProductImage);
        holder.tvProductName.setText(data.get(position).getProduct_name());
        holder.tvProductPrice.setText(data.get(position).getProduct_price() + " د.أ ");
        holder.tvProductDescription.setText(data.get(position).getProduct_description());


        //--------------- add product to fav -----------------------------------



        holder.ivRemoveFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = mAuth.getCurrentUser();
                String docId = data.get(position).getProduct_id() + user.getUid();


                db.collection("favorite").document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(activity, "تمت الازالة من المفضلة", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView ivProductImage, ivRemoveFav;
        public TextView tvProductName, tvProductPrice, tvProductDescription;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            ivRemoveFav = itemView.findViewById(R.id.ivRemoveFav);

        }
    }
}