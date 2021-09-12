package com.josalla.store.Adapters;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.josalla.store.ProductDetailsActivity;
import com.josalla.store.R;
import com.josalla.store.model.Products;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class SliderOfferAdapter extends SliderViewAdapter<SliderOfferAdapter.SliderAdapterVH> {

    private Activity activity;
    ArrayList<String> data;
    FirebaseFirestore db;
    ArrayList<String> id = new ArrayList<>();
    ArrayList<Products> prductts = new ArrayList<>();


    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageRef;

    static boolean hasOffer;
    static String prduct_id;

    public SliderOfferAdapter(Activity activity, ArrayList<String> data, ArrayList<Products> products) {
        this.activity = activity;
        this.data = data;
        this.prductts = products;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://josalla.appspot.com/images");

        viewHolder.textViewDescription.setText("This is slider item " + position);

        Glide.with(activity).load(data.get(position) + "").into(viewHolder.imageViewBackground);

        db.collection("offers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            id.clear();

                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                if ((doc.get("prduct_Id1") + "").length() != 0) {
                                    id.add((doc.get("prduct_Id1") + ""));
                                }
                                //---------------------------------------------

                                if ((doc.get("prduct_Id2") + "").length() != 0) {
                                    id.add((doc.get("prduct_Id2") + ""));
                                }
                                //---------------------------------------------

                                if ((doc.get("prduct_Id3") + "").length() != 0) {
                                    id.add((doc.get("prduct_Id3") + ""));
                                }
                                //---------------------------------------------

                                if ((doc.get("prduct_Id4") + "").length() != 0) {
                                    id.add((doc.get("prduct_Id4") + ""));
                                }
                                //---------------------------------------------

                                if ((doc.get("prduct_Id5") + "").length() != 0) {
                                    id.add((doc.get("prduct_Id5") + ""));
                                }
                                //---------------------------------------------
                                if (id.size() == 0) {
                                    hasOffer = false;
                                } else {
                                    hasOffer = true;
                                }

                            }
                        }

                    }
                });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                db.collection("users")
                        .document(auth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot doc) {
                                if ((doc.get("user_type") + "").equals("admin")) {


                                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                                    dialog.setTitle("تنبيه!")
                                            .setCancelable(false)
                                            .setMessage("هل تريد حذف هذا العرض بشكل نهائي ؟!")
                                            .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialoginterface, int i) {
                                                    dialoginterface.dismiss();

                                                }
                                            })
                                            .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialoginterface, int i) {

                                                    if (hasOffer) {
                                                        switch (position) {
                                                            case 0:
                                                                db.collection("offers").document("offers").update("offer_image1", "");
                                                                db.collection("offers").document("offers").update("prduct_Id1", "");
                                                                db.collection("offers").document("offers")
                                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot doc) {
                                                                        DeleteImage(doc.get("image_path1") + "");
                                                                    }
                                                                });


                                                                break;
                                                            case 1:
                                                                db.collection("offers").document("offers").update("offer_image2", "");
                                                                db.collection("offers").document("offers").update("prduct_Id2", "");
                                                                db.collection("offers").document("offers")
                                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot doc) {
                                                                        DeleteImage(doc.get("image_path2") + "");
                                                                    }
                                                                });
                                                                break;
                                                            case 2:
                                                                db.collection("offers").document("offers").update("offer_image3", "");
                                                                db.collection("offers").document("offers").update("prduct_Id3", "");
                                                                db.collection("offers").document("offers")
                                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot doc) {
                                                                        DeleteImage(doc.get("image_path3") + "");
                                                                    }
                                                                });
                                                                break;
                                                            case 3:
                                                                db.collection("offers").document("offers").update("offer_image4", "");
                                                                db.collection("offers").document("offers").update("prduct_Id4", "");
                                                                db.collection("offers").document("offers")
                                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot doc) {
                                                                        DeleteImage(doc.get("image_path4") + "");
                                                                    }
                                                                });
                                                                break;
                                                            case 4:
                                                                db.collection("offers").document("offers").update("offer_image5", "");
                                                                db.collection("offers").document("offers").update("prduct_Id5", "");
                                                                db.collection("offers").document("offers")
                                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot doc) {
                                                                        DeleteImage(doc.get("image_path5") + "");
                                                                    }
                                                                });
                                                                break;
                                                        }
                                                        activity.recreate();
                                                        notifyDataSetChanged();


                                                    }

                                                }
                                            }).show();
                                }

                            }
                        });


                return true;
            }


        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prduct_id = "";
                if (hasOffer) {
                    switch (position) {
                        case 0:
                            prduct_id = id.get(0);
                            StartActivity(activity, prduct_id, position);
                            break;
                        case 1:
                            prduct_id = id.get(1);
                            StartActivity(activity, prduct_id, position);

                            break;
                        case 2:
                            prduct_id = id.get(2);
                            StartActivity(activity, prduct_id, position);

                            break;
                        case 3:
                            prduct_id = id.get(3);
                            StartActivity(activity, prduct_id, position);

                            break;
                        case 4:
                            prduct_id = id.get(4);
                            StartActivity(activity, prduct_id, position);

                            break;
                    }


                }
            }
        });


    }

    @Override
    public int getCount() {
        return data.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.imageViewBackground);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            this.itemView = itemView;
        }

    }

    public void StartActivity(Activity activity, String intent_id, int position) {

        Intent intent = new Intent(activity, ProductDetailsActivity.class);
        db.collection("products").document(intent_id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) {
                    intent.putExtra("id", intent_id);
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
                    Toasty.error(activity, "انتهى العرض", Toast.LENGTH_SHORT).show();

                }
            }
        });


//        intent.putExtra("id", intent_id);
//        intent.putExtra("name", prductts.get(position).getProduct_name());
//        intent.putExtra("category", prductts.get(position).getProduct_category());
//        intent.putExtra("images", prductts.get(position).getProduct_images());
//        intent.putExtra("price", prductts.get(position).getProduct_price());
//        intent.putExtra("description", prductts.get(position).getProduct_description());
//        intent.putExtra("colors", prductts.get(position).getProduct_colors());
//        intent.putExtra("sizes", prductts.get(position).getProduct_sizes());
//        intent.putExtra("rate", prductts.get(position).getProduct_rate());

    }

    public void DeleteImage(String imagePaht) {


        String path = imagePaht.replace("images/", "");

        //Create a reference to the file to delete
        StorageReference desertRef = storageRef.child(path);

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toasty.success(activity, "تم حذف العرض", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Toasty.error(activity, "هناك خطأ ما !", Toast.LENGTH_SHORT).show();


            }
        });

    }

}