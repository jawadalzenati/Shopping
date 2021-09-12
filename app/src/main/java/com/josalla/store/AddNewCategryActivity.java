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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.josalla.store.model.Category;
import com.josalla.store.utils.ToolUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;

public class AddNewCategryActivity extends AppCompatActivity {
    ImageView ivBack;
    ImageView ivCategoryImage;
    ImageView ivAdd;
    TextView tvAdd;
    Button btnAddNewCategory;
    EditText txtCategoryName;


    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;

    InputStream inputStream;
    Bitmap selectedImage;
    byte[] image;
    FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://shoppingjo-be29c.appspot.com/images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_categry);

        ivBack = findViewById(R.id.ivBack);
        ivCategoryImage = findViewById(R.id.ivCategoryImage);
        tvAdd = findViewById(R.id.tvAdd);
        ivAdd = findViewById(R.id.ivAdd);
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory);
        txtCategoryName = findViewById(R.id.txtCategoryName);

        db = FirebaseFirestore.getInstance();


        //========================================================================================================================

        btnAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filePath != null) {
                    if (txtCategoryName.getText().toString().length() != 0) {
                        ToolUtils.showDialog(AddNewCategryActivity.this, "جاري الحفظ...");

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

                                        Category category = new Category(txtCategoryName.getText() + "", ImagePath);
                                        // Add a new document with a generated ID
                                        db.collection("category")
                                                .add(category)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.e("MAs", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                        Toasty.success(AddNewCategryActivity.this, getString(R.string.Success), Toast.LENGTH_SHORT, true).show();
                                                        ToolUtils.hideDialog();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("MAS", "Error adding document", e);
                                                        Toast.makeText(AddNewCategryActivity.this, "No", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.success(AddNewCategryActivity.this, getString(R.string.ImageAdded), Toast.LENGTH_SHORT, true).show();

                            }
                        });


                    } else {
                        txtCategoryName.setError(getString(R.string.requerd));
                    }

                } else {
                    Toasty.error(AddNewCategryActivity.this, getString(R.string.addImage), Toast.LENGTH_SHORT, true).show();

                }

            }
        });

        //---------------------------------------------------------


        //select image
        ivCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(AddNewCategryActivity.this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                // your conde when you did not get perimission
                                Toast.makeText(AddNewCategryActivity.this, "Allow  camera to continue..", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                //if button clicked agine assk permission


                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });

        //back to home --------------------------------------------------------------------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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

