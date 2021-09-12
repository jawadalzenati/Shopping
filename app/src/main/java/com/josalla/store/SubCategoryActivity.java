package com.josalla.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
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
import com.josalla.store.model.SubCategory;
import com.josalla.store.utils.ToolUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SubCategoryActivity extends AppCompatActivity {

    ImageView ivBack, ivCategoryImage, ivAdd;
    TextView tvAdd;
    Spinner spMainCategory;
    EditText txtSubCategoryName;
    Button btnAddNewCategory;

    byte[] image;

    ArrayList<String> main_category = new ArrayList<>();

    FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://shoppingjo-be29c.appspot.com/images");
    public String mainCategory;
    String firstText = "أختر الفئة الرئيسية";

    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        ivBack = findViewById(R.id.ivBack);
        spMainCategory = findViewById(R.id.spMainCategory);
        txtSubCategoryName = findViewById(R.id.txtSubCategoryName);
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory);
        ivCategoryImage = findViewById(R.id.ivCategoryImage);
        ivAdd = findViewById(R.id.ivAdd);
        tvAdd = findViewById(R.id.tvAdd);
        db = FirebaseFirestore.getInstance();


        // add sub category ==================================
        btnAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String category = txtSubCategoryName.getText().toString();
                String main_category = mainCategory;

                if (filePath != null) {
                    if (category.length() == 0 || main_category.equals(firstText)) {
                        if (category.length() == 0)
                            txtSubCategoryName.setError("مطلوب");
                        if (main_category.equals(firstText))
                            Toasty.error(SubCategoryActivity.this, "اختر الفئة الرئيسية", Toast.LENGTH_SHORT).show();

                    } else {
                        ToolUtils.showDialog(SubCategoryActivity.this, "جاري الحفظ...");

                        final StorageReference childRef = storageRef.child(System.currentTimeMillis() + "_mastaca.jpg");
                        final UploadTask uploadTask = childRef.putBytes(image);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String ImageUrl = childRef.getPath().replaceFirst("/", "");

                                //get image url
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(ImageUrl);
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String ImagePath = uri.toString();

                                        SubCategory subCategory = new SubCategory(category + "", ImagePath + "", main_category + "");

                                        // Add a new document with a generated ID
                                        db.collection("sub_category")
                                                .add(subCategory)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.e("MAs", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                        Toasty.success(SubCategoryActivity.this, getString(R.string.Success), Toast.LENGTH_SHORT, true).show();
                                                        ToolUtils.hideDialog();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("MAS", "Error adding document", e);
                                                    }
                                                });

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.success(SubCategoryActivity.this, getString(R.string.ImageAdded), Toast.LENGTH_SHORT, true).show();

                            }
                        });


                    }


                } else {
                    Toasty.error(SubCategoryActivity.this, "الرجاء اضافة صورة", Toast.LENGTH_SHORT).show();
                }

            }
        });


        // add image
        ivCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(SubCategoryActivity.this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                startActivityForResult(Intent.createChooser(intent, "أختر صورة"), PICK_IMAGE_REQUEST);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                // your conde when you did not get perimission
                                Toast.makeText(SubCategoryActivity.this, "الرجاء منح الاذونات ", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                //if button clicked agine assk permission


                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });


        // loading the main category ================================
        final String SelectCategory = firstText;
        main_category.add(SelectCategory);
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
                                    main_category.add(decument.get("category_name") + "");
                                    ToolUtils.hideDialog();
                                }
                            } else {
                                ToolUtils.hideDialog();
                                Toasty.error(SubCategoryActivity.this, "فشل في الحصول على الاقسام", Toast.LENGTH_SHORT, true).show();
                            }
                        }else {
                            ToolUtils.hideDialog();
                            finish();
                            Toasty.info(SubCategoryActivity.this, "عليك اضافة اقسام رئيسية اولا", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });


        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, main_category);
        spMainCategory.setAdapter(categoriesAdapter);

        spMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //================================= finish ================================


        // get image


        // ivBack
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //get image

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            filePath = data.getData();

            try {
                inputStream = getContentResolver().openInputStream(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);

            //----  to reduce image size  -----------------------
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            image = baos.toByteArray();
            //----  to reduce image size  -----------------------


            ivCategoryImage.setImageBitmap(selectedImage);
            ivAdd.setVisibility(View.GONE);
            tvAdd.setVisibility(View.GONE);
        }
    }


}