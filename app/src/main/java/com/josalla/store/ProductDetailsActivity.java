package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.ProdcutsAdapter;
import com.josalla.store.Adapters.SliderProductAdapter;
import com.josalla.store.dialogs.AddToCartDialog;
import com.josalla.store.dialogs.CompleteOrderDialog;
import com.josalla.store.model.Favorite;
import com.josalla.store.model.Products;
import com.josalla.store.utils.ToolUtils;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ProductDetailsActivity extends AppCompatActivity {
    ImageView ivBack, ivHome, ivAddToFav, ivRemoveFav, ivAddToCart, ivClothesSize;
    Button btnCompleteOrder;
    SliderView ivProductImage;
    TextView tvProductPrice, tvShare, tvProductDescrip, tvProductDescription, tvProductSize, tvProductColor, tvAllClothesSize, tvNoRate, tvAllRate;
    RecyclerView rvOthersProducts;
    RatingBar rbRate;

    TextView tvDiscrp1 ,tvDiscrp2,tvDiscrp3,tvDiscrp4,tvDiscrp5;





    FirebaseFirestore db;
    FirebaseAuth mAuth;

    ConstraintLayout constraintLayout;
    ArrayList<Products> all_products = new ArrayList<>();
    ProdcutsAdapter prodcutsAdapter;
    ArrayList<String> product_images = new ArrayList<>();

    SliderProductAdapter sliderProductAdapter;
    CompleteOrderDialog completeOrderDialog;
    AddToCartDialog addToCartDialog;

    String FirstImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ivBack = findViewById(R.id.ivBack);
        ivHome = findViewById(R.id.ivHome);

        ivRemoveFav = findViewById(R.id.ivRemoveFav);
        ivAddToFav = findViewById(R.id.ivAddToFav);
        btnCompleteOrder = findViewById(R.id.btnCompleteOrder);
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvShare = findViewById(R.id.tvShare);
        tvProductDescrip = findViewById(R.id.tvProductDescrip);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductSize = findViewById(R.id.tvProductSize);
        tvProductColor = findViewById(R.id.tvProductColor);
        rvOthersProducts = findViewById(R.id.rvOthersProducts);
        tvAllClothesSize = findViewById(R.id.tvAllClothesSize);
        rbRate = findViewById(R.id.rbRate);
        tvNoRate = findViewById(R.id.tvNoRate);
        tvAllRate = findViewById(R.id.tvAllRate);
        constraintLayout = findViewById(R.id.constraintLayout);
        ivClothesSize = findViewById(R.id.ivClothesSize);

        tvDiscrp1 = findViewById(R.id.tvDiscrp1);
        tvDiscrp2 = findViewById(R.id.tvDiscrp2);
        tvDiscrp3 = findViewById(R.id.tvDiscrp3);
        tvDiscrp4 = findViewById(R.id.tvDiscrp4);
        tvDiscrp5 = findViewById(R.id.tvDiscrp5);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        ivAddToFav.setVisibility(View.GONE);
        ivRemoveFav.setVisibility(View.GONE);




        Intent intent = getIntent();

              /*
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
         */

        String product_id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String category = intent.getStringExtra("category");
        String images = intent.getStringExtra("images");
        String price = intent.getStringExtra("price");
        String description = intent.getStringExtra("description");
        String colors = intent.getStringExtra("colors");
        String sizes = intent.getStringExtra("sizes");
        String hand_pay = intent.getStringExtra("handPay");
        String discrip1 = intent.getStringExtra("discrip1");
        String discrip2 = intent.getStringExtra("discrip2");
        String discrip3 = intent.getStringExtra("discrip3");
        String discrip4 = intent.getStringExtra("discrip4");
        String discrip5 = intent.getStringExtra("discrip5");

        // discrip 1----
        if (discrip1.length()!=0){
            tvDiscrp1.setText(discrip1);
        }else {
            tvDiscrp1.setVisibility(View.GONE);
        }

        // discrip 2----
        if (discrip2.length()!=0){
            tvDiscrp2.setText(discrip2);
        }else {
            tvDiscrp2.setVisibility(View.GONE);
        }

        // discrip 3----
        if (discrip3.length()!=0){
            tvDiscrp3.setText(discrip3);
        }else {
            tvDiscrp3.setVisibility(View.GONE);
        }

        // discrip 4----
        if (discrip4.length()!=0){
            tvDiscrp4.setText(discrip4);
        }else {
            tvDiscrp4.setVisibility(View.GONE);
        }

        // discrip 5----
        if (discrip5.length()!=0){
            tvDiscrp5.setText(discrip5);
        }else {
            tvDiscrp5.setVisibility(View.GONE);
        }


        ivAddToCart = findViewById(R.id.ivAddToCart);

        db.collection("sizes").document("sizes")
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                Glide.with(ProductDetailsActivity.this).load(doc.get("size")).into(ivClothesSize);
            }
        });


        if (intent.getStringExtra("rate").equals("0")) {
            tvNoRate.setVisibility(View.VISIBLE);
            rbRate.setVisibility(View.GONE);
        } else {
            rbRate.setRating(Float.parseFloat(intent.getStringExtra("rate") + ""));

        }

        tvProductPrice.setText(price + " $ ");
        tvProductDescrip.setText(description);
        tvProductDescription.setText(category);
        tvProductSize.setText(sizes);
        tvProductColor.setText(colors);


        String[] AllImages = images.split("<IMAGE>");
        FirstImage = AllImages[1];

        for (int i = 1; i < AllImages.length; ++i) {
            product_images.add(AllImages[i]);

        }

        sliderProductAdapter = new SliderProductAdapter(ProductDetailsActivity.this, product_images);
        ivProductImage.setSliderAdapter(sliderProductAdapter);
        sliderProductAdapter.notifyDataSetChanged();
      //  ivProductImage.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        ivProductImage.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        ivProductImage.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);


//get products by category --------------------------------------------

        Query collRef = db.collection("products").whereEqualTo("product_category", category);
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value
                    , @Nullable FirebaseFirestoreException e) {

                if (!value.isEmpty()) {
                    all_products.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Products products = doc.toObject(Products.class);
                        products.setProduct_id(doc.getId());
                        all_products.add(products);
                        prodcutsAdapter.notifyDataSetChanged();

                        //------show and hide fav igon -----
                        db.collection("favorite").whereEqualTo("favorite_product_id", product_id)
                                .whereEqualTo("favorite_user_id", mAuth.getCurrentUser().getUid() + "")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot doc) {
                                if (!doc.isEmpty()) {

                                    ivAddToFav.setVisibility(View.GONE);
                                    ivRemoveFav.setVisibility(View.VISIBLE);

                                } else {
                                    ivAddToFav.setVisibility(View.VISIBLE);
                                    ivRemoveFav.setVisibility(View.GONE);

                                }

                            }
                        });
                    }
                } else {

                }
            }
        });


        prodcutsAdapter = new ProdcutsAdapter(this, all_products);
        rvOthersProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvOthersProducts.setAdapter(prodcutsAdapter);

        //show cloth size dialog -------------------------------------------


        tvAllClothesSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AllSizeActivity.class));
            }
        });


        //------------back to home ---------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //-------------------------------- complete order ----------------------------------

        completeOrderDialog = new CompleteOrderDialog(this, product_id + "", FirstImage + "", description + "", price + "", colors + "", sizes + "", hand_pay + "", name);

        btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolUtils.showDialog(ProductDetailsActivity.this, "جاري الأرسال");


                db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {


                        if (("" + doc.get("allowToOrder")).equals("no")) {
                            Snackbar snackbar = Snackbar
                                    .make(constraintLayout, "تم حظرك من اضافة اي طلب جديد", Snackbar.LENGTH_LONG)
                                    .setAction("تواصل معنا", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            db.collection("contacts").document("contacts")
                                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public void onSuccess(DocumentSnapshot doc) {
                                                    Uri uri = Uri.parse("smsto:" + doc.get("whatsapp"));
                                                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                                                    i.setPackage("com.whatsapp");
                                                    startActivity(Intent.createChooser(i, "استفسار"));
                                                }
                                            });
                                        }
                                    });

                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();
                            ToolUtils.hideDialog();
                        } else {
                            completeOrderDialog.show();
                            ToolUtils.hideDialog();


                        }

                    }
                });


            }
        });


        //--------------- add product to fav -----------------------------------


        //1- add to fav
        ivAddToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                Favorite favorite = new Favorite(product_id, user.getUid());

                String docId = product_id + user.getUid();


                db.collection("favorite").document(docId)
                        .set(favorite).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ivAddToFav.setVisibility(View.GONE);
                        ivRemoveFav.setVisibility(View.VISIBLE);
                        Toasty.success(ProductDetailsActivity.this, "تمت الاضافة الى المفضلة", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //----- remove from fav
        ivRemoveFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = mAuth.getCurrentUser();
                String docId = product_id + user.getUid();


                db.collection("favorite").document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ivAddToFav.setVisibility(View.VISIBLE);
                        ivRemoveFav.setVisibility(View.GONE);
                        Toasty.success(ProductDetailsActivity.this, "تمت الازالة من المفضلة", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //------------------------------------------- all rate -------------------------------

        tvAllRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String product_id = intent.getStringExtra("id");
                Intent rate_intent = new Intent(getApplicationContext(), AllRateActivity.class);
                rate_intent.putExtra("rate_product_id", product_id);
                startActivity(rate_intent);
            }
        });

        //-------------------------------------share -----------------

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String shareText = "مرحبا .. أنا استخدم متجر شوبنج جو ووجدت كثير من المنتجات التي اعجبتني ";
                String shareTextPart2 = "يمكنك ايضا تحميل التطبيق والاستمتاع به من هنا..";
                String googlePlayLink = "https://play.google.com/store/apps/details?id=" + getPackageName();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "FOODYBITE");
                intent.putExtra(Intent.EXTRA_TEXT, shareText + shareTextPart2 + googlePlayLink);
                startActivity(Intent.createChooser(intent, "أختر شيئ"));

            }
        });
        //--------------------------------- back to home -------------------------
        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


        //add to cart ----------------------------------
        ivAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //

                addToCartDialog = new AddToCartDialog(ProductDetailsActivity.this, product_id + "", FirstImage + "", price + "",
                        description + "", colors + "", sizes + "", name + "");
                addToCartDialog.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (completeOrderDialog.isShowing()) {
            completeOrderDialog.dismiss();
        }
    }

}
