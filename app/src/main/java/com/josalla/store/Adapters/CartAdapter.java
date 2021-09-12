package com.josalla.store.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.ProductDetailsActivity;
import com.josalla.store.R;
import com.josalla.store.model.Cart;


import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Cart> data;
    FirebaseFirestore db;
    FirebaseAuth mAuth;


    public CartAdapter(Activity activity, ArrayList<Cart> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.cart_item, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, final int position) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();




        if (data.get(position).isSet_selected()) {
            holder.setCheek.setChecked(true);
        } else {
            holder.setCheek.setChecked(false);

        }


        holder.setCheek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.collection("user_cart").document(mAuth.getCurrentUser().getUid() + data.get(position).getProduct_id())
                            .update("set_selected", true);
                } else {
                    db.collection("user_cart").document(mAuth.getCurrentUser().getUid() + data.get(position).getProduct_id())
                            .update("set_selected", false);
                }



            }
        });


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, ProductDetailsActivity.class);
                db.collection("products").document(data.get(position).getProduct_id())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot doc) {
                                if (doc.exists()) {
                                    intent.putExtra("id", data.get(position).getProduct_id());
                                    intent.putExtra("name", doc.get("product_name") + "");
                                    intent.putExtra("category", doc.get("product_category") + "");
                                    intent.putExtra("images", doc.get("product_images") + "");
                                    intent.putExtra("price", doc.get("product_price") + "");
                                    intent.putExtra("description", doc.get("product_description") + "");
                                    intent.putExtra("colors", doc.get("product_colors") + "");
                                    intent.putExtra("sizes", doc.get("product_sizes") + "");
                                    intent.putExtra("rate", doc.get("product_rate") + "");
                                    intent.putExtra("handPay", doc.get("hand_pay") + "");
                                    intent.putExtra("discrip1", doc.get("product_discrip1") + "");
                                    intent.putExtra("discrip2", doc.get("product_discrip2") + "");
                                    intent.putExtra("discrip3", doc.get("product_discrip3") + "");
                                    intent.putExtra("discrip4", doc.get("product_discrip4") + "");
                                    intent.putExtra("discrip5", doc.get("product_discrip5") + "");
                                    activity.startActivity(intent);

                                } else {
                                    Toasty.error(activity, "نأسف , المنتج لم يعد متوفر في المخزون", Toast.LENGTH_LONG).show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(activity, "هناك خطأ ما", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


        String productDiscrip = "المواصفات المحددة: " + "العدد: " + data.get(position).getProduct_count() + " , " + data.get(position).getSelected_color() + " , " + data.get(position).getSelected_size();
        Glide.with(activity).load(data.get(position).getProduct_image()).into(holder.ivProductImage);
        holder.tvProductName.setText(data.get(position).getProduct_name());
        holder.tvProductPrice.setText(data.get(position).getTotal_price() + "$");
        holder.tvProductDescription.setText(productDiscrip);


        //--------------- remove form cart  -----------------------------------
        holder.ivRemoveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                String docId = user.getUid() + data.get(position).getProduct_id();

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("تنبيه!")
                        .setMessage("هل تريد حذف هذا المنتج من العربة!")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("user_cart").document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toasty.success(activity, "تمت الازالة من العربة", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                });

                            }
                        }).setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
        public CardView cardView;
        public ImageView ivProductImage, ivRemoveCart;
        public TextView tvProductName, tvProductPrice, tvProductDescription;
        public CheckBox setCheek;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            ivRemoveCart = itemView.findViewById(R.id.ivRemoveCart);
            setCheek = itemView.findViewById(R.id.setCheek);

        }
    }
}