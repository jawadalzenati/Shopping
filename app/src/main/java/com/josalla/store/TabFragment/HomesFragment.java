package com.josalla.store.TabFragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.ProdcutsAdapter;
import com.josalla.store.Adapters.SubAdapter;
import com.josalla.store.R;
import com.josalla.store.model.Products;
import com.josalla.store.model.SubCategory;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomesFragment extends Fragment {
    RecyclerView rvLastAdded, rvAllProduct, rvAllCategory;

    ArrayList<Products> all_products = new ArrayList<>();
    ArrayList<Products> last_product = new ArrayList<>();
    ArrayList<SubCategory> subCategories = new ArrayList<>();


    ProdcutsAdapter lastProductAdapter;
    ProdcutsAdapter allProductAdapter;
    SubAdapter subAdapter;
    FirebaseFirestore db;
    final static String main_category = "المنزل";
    public HomesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_home_tools, container, false);

        rvAllProduct = fragment.findViewById(R.id.rvAllProduct);
        rvLastAdded = fragment.findViewById(R.id.rvLastAdded);
        rvAllCategory = fragment.findViewById(R.id.rvAllCategory);

        db = FirebaseFirestore.getInstance();
        // get all subCategory ------------------------------------------------------------------------------


        // get all category =========================================================
        Query sub = db.collection("sub_category").whereEqualTo("category_main", main_category);
        sub.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value
                    , @Nullable FirebaseFirestoreException e) {
                if (!value.isEmpty()) {
                    subCategories.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        SubCategory subs = doc.toObject(SubCategory.class);
                        subs.setCategory_id(doc.getId());
                        subCategories.add(subs);
                        subAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
        subAdapter = new SubAdapter(getActivity(), subCategories);
        rvAllCategory.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false));
        rvAllCategory.setAdapter(subAdapter);
        //================================================================================


        //----------------------- get last 10 product ----------------------------
        Query collRef = db.collection("products").whereEqualTo("product_category", main_category).limit(30);
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.e("MAS", "Listen failed.", e);
                    return;
                }
                last_product.clear();

                for (QueryDocumentSnapshot doc : value) {
                    if (last_product.size() < 10) {
                        Products products = doc.toObject(Products.class);
                        products.setProduct_id(doc.getId());
                        last_product.add(products);
                        lastProductAdapter.notifyDataSetChanged();
                    }
                }


            }
        });

        lastProductAdapter = new ProdcutsAdapter(getActivity(), last_product);
        rvLastAdded.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rvLastAdded.setAdapter(lastProductAdapter);
        //--------------------------- end get last 10 --------------------------------------


        // get all product that has fragment tag --------------------------------------------
        Query collRef2 = db.collection("products").whereEqualTo("product_category", main_category).limit(20);
        collRef2.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.e("MAS", "Listen failed.", e);
                    return;
                }
                all_products.clear();

                for (QueryDocumentSnapshot doc : value) {
                    Products products = doc.toObject(Products.class);
                    products.setProduct_id(doc.getId());
                    all_products.add(products);
                    allProductAdapter.notifyDataSetChanged();

                }


            }
        });

        allProductAdapter = new ProdcutsAdapter(getActivity(), all_products);
        rvAllProduct.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvAllProduct.setAdapter(allProductAdapter);
        // get all product that has fragment tag --------------------------------------------


        return fragment;
    }

}
