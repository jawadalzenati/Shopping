package com.josalla.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.utils.ToolUtils;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class EditActivity extends AppCompatActivity {
    ImageView ivBack;
    ArrayList<String> product_category = new ArrayList<>();
    ArrayList<String> sub_category = new ArrayList<>();

    FirebaseFirestore db;
    public String categoryName;
    public String categorySub;

    EditText txtProductName, txtProductPrice, txtProductDescription, txtProductColor, txtProductSize;
    Spinner spCategory, spSubCategory;
    Button btnEditProduct;
    String SUBCATEGORY = "الفئة الفرعية";

    EditText txtDiscrop1 ,txtDiscrop2 ,txtDiscrop3,txtDiscrop4,txtDiscrop5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ivBack = findViewById(R.id.ivBack);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        spCategory = findViewById(R.id.spCategory);
        txtProductDescription = findViewById(R.id.txtProductDescription);
        txtProductColor = findViewById(R.id.txtProductColor);
        txtProductSize = findViewById(R.id.txtProductSize);
        btnEditProduct = findViewById(R.id.btnEditProduct);
        spSubCategory = findViewById(R.id.spSubCategory);

        txtDiscrop1 = findViewById(R.id.txtDiscrop1);
        txtDiscrop2 = findViewById(R.id.txtDiscrop2);
        txtDiscrop3 = findViewById(R.id.txtDiscrop3);
        txtDiscrop4 = findViewById(R.id.txtDiscrop4);
        txtDiscrop5 = findViewById(R.id.txtDiscrop5);



        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String prduct_Id = intent.getStringExtra("id");


        //get All category Name------------------------------------------------------------------------
        final String SelectCategory = "أختر الفئة";
        product_category.add(SelectCategory);
        ToolUtils.showDialog(this, "جاري التحميل...");
        db.collection("category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot decument : task.getResult()) {
                                product_category.add(decument.get("category_name") + "");
                                ToolUtils.hideDialog();
                            }
                        } else {
                            ToolUtils.hideDialog();
                            Toasty.error(EditActivity.this, "فشل في الحصول على الاقسام", Toast.LENGTH_SHORT, true).show();
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
                    ToolUtils.showDialog(EditActivity.this, "جاري التحميل...");
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
                                        Toasty.error(EditActivity.this, "فشل في الحصول على الاقسام", Toast.LENGTH_SHORT, true).show();
                                    }

                                }
                            });

                    ArrayAdapter<String> categoriesAdapter2 = new ArrayAdapter<String>(EditActivity.this, android.R.layout.simple_spinner_item, sub_category);
                    spSubCategory.setAdapter(categoriesAdapter2);
                }
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


        btnEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (categoryName.contains("أختر الفئة") || categorySub.equals(SUBCATEGORY)) {
                    Toasty.error(EditActivity.this, "يجب اعادة اختيار الفئة", Toast.LENGTH_SHORT).show();
                } else {
                    ToolUtils.showDialog(EditActivity.this, "جاري العمل ..");
                    if ((txtProductColor.getText().toString()).length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_colors", txtProductColor.getText() + "");
                    }
                    //----------------------------------------------------------
                    if ((txtProductDescription.getText().toString()).length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_description", txtProductDescription.getText() + "");
                    }
                    //---------------------------------
                    if ((txtProductName.getText().toString()).length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_name", txtProductName.getText() + "");
                    }
                    //---------------------------------------
                    if ((txtProductPrice.getText().toString()).length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_price", txtProductPrice.getText() + "");
                    }

                    if ((txtProductSize.getText().toString()).length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_sizes", txtProductSize.getText() + "");
                    }

                    if (categoryName.length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_category", categoryName + "");
                    }

                    //---1----
                    if (txtDiscrop1.getText().toString().length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_discrip1", txtDiscrop1.getText() + "");
                    }
                    //---2----
                    if (txtDiscrop2.getText().toString().length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_discrip2", txtDiscrop2.getText() + "");
                    }

                    //----3---
                    if (txtDiscrop3.getText().toString().length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_discrip3", txtDiscrop3.getText() + "");
                    }

                    //-----4--
                    if (txtDiscrop4.getText().toString().length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_discrip4", txtDiscrop4.getText() + "");
                    }

                    //----5---
                    if (txtDiscrop5.getText().toString().length() != 0) {
                        db.collection("products").document(prduct_Id).update("product_discrip5", txtDiscrop5.getText() + "");
                    }



                    finish();
                    Toasty.success(EditActivity.this, "تم التعديل بنجاح ", Toast.LENGTH_SHORT).show();

                    ToolUtils.hideDialog();
                }


            }
        });


        //-------------------------------------------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
