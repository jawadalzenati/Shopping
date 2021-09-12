package com.josalla.store.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.josalla.store.EditActivity;
import com.josalla.store.OffersActivity;
import com.josalla.store.R;
import com.josalla.store.model.Products;
import com.josalla.store.utils.ToolUtils;


import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Products> data;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;

    String state ;
    String hand;

    public AllProductAdapter(Activity activity, ArrayList<Products> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public AllProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.all_products, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AllProductAdapter.MyViewHolder holder, final int position) {

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://shoppingjo-be29c.appspot.com/images");



        holder.tvPName.setText("الأسم: " + data.get(position).getProduct_name());
        holder.tvPPrice.setText("السعر: " + data.get(position).getProduct_price() + " $ ");
        holder.tvPColors.setText("الألوان: " + data.get(position).getProduct_colors());
        holder.tvPSize.setText("الاحجام: " + data.get(position).getProduct_sizes());
        holder.tvPDiscrp.setText("الوصف: " + data.get(position).getProduct_description());
        holder.tvPCategory.setText("القسم: " + data.get(position).getProduct_category());

        if ((data.get(position).getHand_pay()).equals("yes")){
            holder.tvHand.setText("الدفع باليد : مفعل");
        }else{
            holder.tvHand.setText("الدفع باليد : معطل");

        }


        String[] product_Images = data.get(position).getProduct_images().split("<IMAGE>");
        Glide.with(activity).load(product_Images[1]).into(holder.ivProductImage);


        //-------------------- edit product  -------------------------------
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EditActivity.class);
                intent.putExtra("id", data.get(position).getProduct_id());
                activity.startActivity(intent);
            }
        });

        //----------------------- offer -------------------------

        holder.ivOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offer_intent = new Intent(activity, OffersActivity.class);
                offer_intent.putExtra("product_id", data.get(position).getProduct_id() + "");
                activity.startActivity(offer_intent);
            }
        });


        //--------------------delete product ----------------------------------

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("تنبيه!")
                        .setMessage("هل تريد حذف هذا المنتج بشكل نهائي ؟!")
                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.dismiss();
                            }
                        })
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {

                                ToolUtils.showDialog(activity, "جاري حذف المنتج...");
                                db.collection("products").document(data.get(position).getProduct_id()).delete();
                                String[] imagePath = (data.get(position).getProduct_image_path()).split("<IMAGEPAHT>");
                                for (int d = 0; d < imagePath.length; ++d) {
                                    if (imagePath[d] != null && !imagePath[d].equals("null")) {
                                        String path = imagePath[d].replace("gs://shoppingjo-be29c.appspot.com/images/", "");
                                        DeleteImage(path);
                                    }
                                }
                            }
                        }).show();

            }
        });


        //allow or prevent pay by hand-----------------------------------
        holder.ivHandPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                hand = "";

                if ((data.get(position).getHand_pay()+"").equals("yes")){
                    state = "الدفع باليد : مفعل هل تريد تعطيله ؟ ";
                }else {
                    state = "الدفع باليد : معطل هل تريد تفعيله ؟ ";

                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("تفعيل / تعطيل الدفع باليد")
                        .setMessage(state)
                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.dismiss();
                            }
                        })
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialoginterface, int i) {

                                hand  = data.get(position).getHand_pay();

                                if (hand.equals("yes")){
                                    db.collection("products").document(data.get(position).getProduct_id())
                                            .update("hand_pay" , "no");
                                    Toasty.success(activity , "تم تعطيل الدفع باليد" , Toast.LENGTH_SHORT).show();
                                }else if (hand.equals("no")){
                                    db.collection("products").document(data.get(position).getProduct_id())
                                            .update("hand_pay" , "yes");
                                    Toasty.success(activity , "تم تفعيل الدفع باليد" , Toast.LENGTH_SHORT).show();

                                }

                            }
                        }).show();


            }
        });
        //-----------------------------------------------------------------


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivDelete, ivEdit, ivProductImage, ivOffer , ivHandPay;
        public TextView tvPName, tvPColors, tvPSize, tvPDiscrp, tvPPrice, tvPCategory , tvHand;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            tvPName = itemView.findViewById(R.id.tvPName);
            tvPColors = itemView.findViewById(R.id.tvPColors);
            tvPSize = itemView.findViewById(R.id.tvPSize);
            tvPDiscrp = itemView.findViewById(R.id.tvPDiscrp);
            tvPPrice = itemView.findViewById(R.id.tvPPrice);
            tvPCategory = itemView.findViewById(R.id.tvPCategory);
            ivOffer = itemView.findViewById(R.id.ivOffer);
            ivHandPay = itemView.findViewById(R.id.ivHandPay);
            tvHand = itemView.findViewById(R.id.tvHand);

        }
    }

    public void DeleteImage(String imagePaht) {

        //Create a reference to the file to delete
        StorageReference desertRef = storageRef.child(imagePaht);

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toasty.success(activity, "تم حذف المنتج", Toast.LENGTH_SHORT).show();
                ToolUtils.hideDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ToolUtils.hideDialog();
                Toasty.error(activity, "هناك خطأ ما !", Toast.LENGTH_SHORT).show();


            }
        });

    }
}