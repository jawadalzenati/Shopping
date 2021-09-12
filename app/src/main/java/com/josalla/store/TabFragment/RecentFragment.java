package com.josalla.store.TabFragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.RecentAdapter;
import com.josalla.store.Adapters.SliderOfferAdapter;
import com.josalla.store.R;
import com.josalla.store.dialogs.StartDialog;
import com.josalla.store.model.Products;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment {

    RecyclerView rvRecent;
    ArrayList<Products> all_products = new ArrayList<>();

    ArrayList<String> images = new ArrayList<>();



    FirebaseFirestore db;
    RecentAdapter recentAdapter;
    SliderOfferAdapter adapter;


    int counter = 0;

    StartDialog startDialog;

    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View recent = inflater.inflate(R.layout.fragment_recent, container, false);


        rvRecent = recent.findViewById(R.id.rvRecent);
        db = FirebaseFirestore.getInstance();



        rvRecent.setFocusable(false);

        Query collRef = db.collection("products").limit(30);
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value
                    , @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MAS_ERR_RE", e.getMessage());
                }
                if (!value.isEmpty()) {
                    all_products.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Products products = doc.toObject(Products.class);
                        products.setProduct_id(doc.getId());
                        all_products.add(products);
                        Collections.shuffle(all_products);
                        recentAdapter.notifyDataSetChanged();


                    }
                } else {

                }

            }
        });

        recentAdapter = new RecentAdapter(getActivity(), all_products);
        rvRecent.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvRecent.setAdapter(recentAdapter);

        //------------------------------ get offers ------------------------------------------------------
        db.collection("offers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            images.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                if ((doc.get("offer_image1") + "").length() != 0) {
                                    images.add((doc.get("offer_image1") + ""));
                                }
                                //---------------------------------------------

                                if ((doc.get("offer_image2") + "").length() != 0) {
                                    images.add((doc.get("offer_image2") + ""));
                                }
                                //---------------------------------------------

                                if ((doc.get("offer_image3") + "").length() != 0) {
                                    images.add((doc.get("offer_image3") + ""));
                                }
                                //---------------------------------------------

                                if ((doc.get("offer_image4") + "").length() != 0) {
                                    images.add((doc.get("offer_image4") + ""));
                                }
                                //---------------------------------------------

                                if ((doc.get("offer_image5") + "").length() != 0) {
                                    images.add((doc.get("offer_image5") + ""));
                                }
                                //---------------------------------------------
                                adapter.notifyDataSetChanged();

                                if (images.size() == 0) {
                                    images.add((doc.get("image_par") + ""));
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }

                    }
                });


        //slide show ----------------------------------------------------------------------------------------------------------------------------
        SliderView sliderView = recent.findViewById(R.id.imageSlider);
        adapter = new SliderOfferAdapter(getActivity(), images, all_products);
        sliderView.setSliderAdapter(adapter);
        //sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setScrollTimeInSec(5); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        //slide show ----------------------------------------------------------------------------------------------------------------------------


        return recent;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
