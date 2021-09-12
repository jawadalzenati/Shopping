package com.josalla.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.josalla.store.utils.SendNotification;
import com.josalla.store.utils.ToolUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;

public class OffersActivity extends AppCompatActivity {
    ImageView ivBack, ivOfferImage, ivAdd;
    TextView tvAdd;
    EditText txtNewPrice;
    Button btnAddOffer;

    Spinner spOfferSlide;

    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    InputStream inputStream;
    byte[] image;

    String slidNumber;
    FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://shoppingjo-be29c.appspot.com/images");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);


        ivBack = findViewById(R.id.ivBack);
        ivOfferImage = findViewById(R.id.ivOfferImage);
        ivAdd = findViewById(R.id.ivAdd);
        tvAdd = findViewById(R.id.tvAdd);
        txtNewPrice = findViewById(R.id.txtNewPrice);
        btnAddOffer = findViewById(R.id.btnAddOffer);

        spOfferSlide = findViewById(R.id.spOfferSlide);

        db = FirebaseFirestore.getInstance();


        Intent intent = getIntent();

        String prductId = intent.getStringExtra("product_id");


        spOfferSlide.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slidNumber = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //---


        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPrice = txtNewPrice.getText().toString();
                if (newPrice.length() == 0) {
                    txtNewPrice.setError("مطلوب");
                } else {
                    if (slidNumber.equals("اختر رقم العرض")) {
                        Toast.makeText(OffersActivity.this, "اختر رقم العرض", Toast.LENGTH_SHORT).show();
                    } else

                        //------------ upload offer images ---------------------
                        if (filePath != null) {
                            ToolUtils.showDialog(OffersActivity.this, "جاري اضافة عرض...");

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
                                            String imagePath = uri.toString();

                                            db.collection("offers").document("offers")
                                                    .update("offer_image" + slidNumber, imagePath);

                                            db.collection("offers").document("offers")
                                                    .update("prduct_Id" + slidNumber, prductId);

                                            db.collection("offers").document("offers")
                                                    .update("image_path" + slidNumber, ImageUrl);

                                            db.collection("products").document(prductId)
                                                    .update("product_price", newPrice);


                                            ToolUtils.hideDialog();
                                            SendNotification.send("متجر شوبنج جو", "لقد قمنا بأضافة عرض جديد هل تود تفقد العرض؟");
                                            Toasty.success(OffersActivity.this, "تم اضافة العرض بنجاح", Toast.LENGTH_SHORT, true).show();
                                            finish();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toasty.success(OffersActivity.this, getString(R.string.ImageAdded), Toast.LENGTH_SHORT, true).show();
                                    ToolUtils.hideDialog();

                                }
                            });


                        } else {
                            Toasty.error(OffersActivity.this, getString(R.string.addImage), Toast.LENGTH_SHORT, true).show();

                        }


                }


            }
        });


        //------ add offer image -----------------------------
        ivOfferImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(OffersActivity.this)
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
                                Toast.makeText(OffersActivity.this, "Allow  camera to continue..", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                //if button clicked agine assk permission


                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });


        //---------- finish ---------------
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


            ivOfferImage.setImageBitmap(selectedImage);
            ivAdd.setVisibility(View.GONE);
            tvAdd.setVisibility(View.GONE);
        }
    }
}
