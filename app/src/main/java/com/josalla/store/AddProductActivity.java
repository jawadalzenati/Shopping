package com.josalla.store;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.josalla.store.Adapters.SliderAdapter;
import com.josalla.store.model.Products;
import com.josalla.store.utils.ToolUtils;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AddProductActivity extends AppCompatActivity {
    int PICK_IMAGE_REQUEST = 111;

    ImageView ivBack, ivAdd, onImage;
    TextView tvAdd;
    EditText txtProductName, txtProductDescription, txtProductColor, txtProductPrice, txtProductSize;
    Spinner spCategory, spSubCategory;
    Button btnAddNewProduct, btnReAdd;

    EditText txtDiscrop1 , txtDiscrop2, txtDiscrop3 ,txtDiscrop4 ,txtDiscrop5;


    CheckBox cbHandPay;

    SliderView ivProductImage;

    String SUBCATEGORY = "الفئة الفرعية";

    FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://shoppingjo-be29c.appspot.com/images");

    ArrayList<Uri> uris = new ArrayList<>();
    ArrayList<byte[]> images = new ArrayList<>();
    InputStream inputStream;
    ArrayList<String> sampleImages = new ArrayList<>();
    ArrayList<String> product_category = new ArrayList<>();
    ArrayList<String> sub_category = new ArrayList<>();

    public String categoryName;
    public String categorySub;
    public String hand_pay = "no";

    String AllImagePath = "";
    String ImageUrl;
    UploadTask uploadTask;
    SliderAdapter adapter;
    int counter = 0;

    static String image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        db = FirebaseFirestore.getInstance();

        ivBack = findViewById(R.id.ivBack);
        ivProductImage = findViewById(R.id.ivProductImage);
        ivAdd = findViewById(R.id.ivAdd);
        tvAdd = findViewById(R.id.tvAdd);

        cbHandPay = findViewById(R.id.cbHandPay);

        btnAddNewProduct = findViewById(R.id.btnAddNewProduct);
        btnReAdd = findViewById(R.id.btnReAdd);
        onImage = findViewById(R.id.onImage);
        onImage.setVisibility(View.GONE);
        ivProductImage.stopNestedScroll();

        txtProductName = findViewById(R.id.txtProductName);
        txtProductDescription = findViewById(R.id.txtProductDescription);
        txtProductColor = findViewById(R.id.txtProductColor);
        spCategory = findViewById(R.id.spCategory);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtProductSize = findViewById(R.id.txtProductSize);
        spSubCategory = findViewById(R.id.spSubCategory);

        txtDiscrop1 = findViewById(R.id.txtDiscrop1);
        txtDiscrop2 = findViewById(R.id.txtDiscrop2);
        txtDiscrop3 = findViewById(R.id.txtDiscrop3);
        txtDiscrop4 = findViewById(R.id.txtDiscrop4);
        txtDiscrop5 = findViewById(R.id.txtDiscrop5);


        //get All category Name------------------------------------------------------------------------
        final String SelectCategory = "أختر الفئة";

        product_category.add(SelectCategory);
        ToolUtils.showDialog(this, "جاري التحميل...");
        db.collection("category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> doc = task.getResult().getDocuments();
                        if (!doc.isEmpty()) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot decument : task.getResult()) {
                                    product_category.add(decument.get("category_name") + "");
                                    ToolUtils.hideDialog();
                                }
                            } else {
                                ToolUtils.hideDialog();
                                Toasty.error(AddProductActivity.this, "فشل في الحصول على الاقسام", Toast.LENGTH_SHORT, true).show();
                            }
                        } else {
                            ToolUtils.hideDialog();
                            finish();
                            Toasty.info(AddProductActivity.this, "عليك اضافة اقسام اولا", Toast.LENGTH_LONG, true).show();

                        }

                    }
                });

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, product_category);
        spCategory.setAdapter(categoriesAdapter);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryName = parent.getItemAtPosition(position).toString();

                if (!categoryName.equals(SelectCategory)) {
                    //------------------ get sub category for the main category ---------------------------
                    final String subCategory = SUBCATEGORY;
                    sub_category.add(subCategory);
                    ToolUtils.showDialog(AddProductActivity.this, "جاري التحميل...");
                    db.collection("sub_category").whereEqualTo("category_main", categoryName)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot decument : task.getResult()) {
                                            sub_category.add(decument.get("category_name") + "");
                                            ToolUtils.hideDialog();
                                        }
                                    } else {
                                        ToolUtils.hideDialog();
                                        Toasty.error(AddProductActivity.this, "فشل في الحصول على الاقسام", Toast.LENGTH_SHORT, true).show();
                                    }

                                }
                            });

                    ArrayAdapter<String> categoriesAdapter2 = new ArrayAdapter<String>(AddProductActivity.this, android.R.layout.simple_spinner_item, sub_category);
                    spSubCategory.setAdapter(categoriesAdapter2);
                }   //-------------------------------------------------------------------------------------
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        spSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySub = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //---------------------------------------------------------------
        ivProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetImage(AddProductActivity.this);
            }
        });

        //---------------------------------------------------------------
        btnReAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });

        //-----------------------------upload image and add products ----------------------------------
        cbHandPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hand_pay = "yes";
                } else {
                    hand_pay = "no";
                }
            }
        });
        btnAddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (images.size() != 0) {
                    if (txtProductName.getText().toString().length() != 0 && txtProductDescription.getText().toString().length() != 0
                            && txtProductColor.getText().toString().length() != 0 && !categoryName.equals(SelectCategory) &&
                            txtProductSize.getText().toString().length() != 0 && !categorySub.equals(SUBCATEGORY) ) {

                        ToolUtils.showDialog(AddProductActivity.this, "جاري التحميل...");

                        for (int i = 0; i < images.size(); ++i) {

                            final StorageReference childRef = storageRef.child(System.currentTimeMillis() + images.get(i).length + "_mastaca.jpg");
                            ImageUrl = "";
                            uploadTask = null;

                            uploadTask = childRef.putBytes(images.get(i));
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                                    ImageUrl = childRef.getPath().replaceFirst("/", "");
                                    Log.e("MAS_IMAGES", ImageUrl + "");

                                    image_path = childRef + "<IMAGEPAHT>" + image_path;

                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(ImageUrl);
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {


                                            AllImagePath = AllImagePath + "<IMAGE>" + uri.toString();
                                            Log.e("MAS_IMAGES", AllImagePath);
                                            ImageUrl = "";


                                            counter += 1;
                                            Log.e("MasCounter", counter + "");
                                            if (counter == images.size()) {
                                                AddProductToDataBase(AddProductActivity.this, txtProductName.getText().toString(), txtProductDescription.getText().toString()
                                                        , categoryName, AllImagePath, txtProductPrice.getText().toString(), txtProductColor.getText().toString(), txtProductSize.getText().toString()
                                                        , txtDiscrop1.getText()+"", txtDiscrop2.getText()+"", txtDiscrop3.getText()+"", txtDiscrop4.getText()+"", txtDiscrop5.getText()+"");
                                            }

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ToolUtils.hideDialog();
                                    Toasty.error(AddProductActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT, true).show();
                                }
                            });
                        }


                    } else {
                        if (categoryName.equals(SelectCategory)) {
                            ToolUtils.hideDialog();
                            Toasty.error(AddProductActivity.this, getString(R.string.AddCategory), Toast.LENGTH_SHORT, true).show();
                        } else {
                            ToolUtils.hideDialog();
                            Toasty.error(AddProductActivity.this, getString(R.string.allReqiuerd), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                } else {
                    Toasty.error(AddProductActivity.this, getString(R.string.addImage), Toast.LENGTH_SHORT, true).show();
                }


            }
        });
        //------------------------add image to preview---------------------------------------


        adapter = new SliderAdapter(AddProductActivity.this, sampleImages);
        ivProductImage.setSliderAdapter(adapter);
        adapter.notifyDataSetChanged();

        //ivProductImage.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        ivProductImage.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        ivProductImage.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);


        //------------------------------------------------------------------
        //--- back to home
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            uris.clear();
            images.clear();

            List<Bitmap> bitmaps = new ArrayList<>();
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); ++i) {

                    Uri Path = clipData.getItemAt(i).getUri();

                    try {
                        inputStream = getContentResolver().openInputStream(Path);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        bitmaps.add(bitmap);

                        //----  to reduce image size  -----------------------
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                        byte[] image = baos.toByteArray();
                        //----  to reduce image size  -----------------------
                        images.add(image);
                        Log.e("MAS_Images", "new Size= " + ((image.length) / 1024) + " KB");


                        uris.add(Path);
                        sampleImages.add(uris.get(i) + "");


                        ivAdd.setVisibility(View.GONE);
                        tvAdd.setVisibility(View.GONE);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Uri Path = data.getData();
                try {
                    inputStream = getContentResolver().openInputStream(Path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.add(bitmap);
                    uris.add(Path);

                    //----  to reduce image size  -----------------------
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                    //----  to reduce image size  -----------------------
                    byte[] image = baos.toByteArray();
                    images.add(image);
                    Log.e("MAS_Images", "new Size= " + ((image.length) / 1024) + "KB");


                    onImage.setVisibility(View.VISIBLE);
                    onImage.setImageBitmap(bitmap);
                    ivAdd.setVisibility(View.GONE);
                    tvAdd.setVisibility(View.GONE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static void GetImage(final Activity activity) {
        final int PICK_IMAGE_REQUEST = 111;
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        //your code when get permission


                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // your conde when you did not get perimission
                        Toasty.error(activity, activity.getString(R.string.PermissionFaild), Toast.LENGTH_LONG, true).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        //if button clicked agine assk permission
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    public void AddProductToDataBase(final Activity activity, String name, String description, String category, String images, String price, String colors, String sizes, String discrip1, String discrip2, String discrip3, String discrip4, String discrip5) {


        //Products(String product_name, String product_description, String product_category, String product_images, String product_price, String product_colors, String product_sizes)

        Products products = new Products(name + "", description + "", category + ""
                , images + "", price + "", colors + "", sizes + "", image_path + ""
                , "0", "0", categorySub + "", hand_pay + "", discrip1 + "", discrip2 + "", discrip3 + "", discrip4 + "", discrip5 + "" );
        // Add a new document with a generated ID
        db.collection("products")
                .add(products)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {


                        Toasty.success(activity, getString(R.string.Success), Toast.LENGTH_SHORT, true).show();
                        ToolUtils.hideDialog();
                        activity.finish();
                        activity.startActivity(new Intent(activity, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ToolUtils.hideDialog();
                        Toasty.error(activity, getString(R.string.fail), Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
