package com.josalla.store.Fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.CategoryAdapter;
import com.josalla.store.R;
import com.josalla.store.model.Category;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    RecyclerView rvCategory;
    ArrayList<Category> categories = new ArrayList<>();
    CategoryAdapter categoryAdapter;

    FirebaseFirestore db;


    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View categorys = inflater.inflate(R.layout.fragment_category, container, false);

        rvCategory = categorys.findViewById(R.id.rvCategory);



        db = FirebaseFirestore.getInstance();


        CollectionReference collRef = db.collection("category");
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {


                if (!value.isEmpty()) {
                    categories.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Category category = doc.toObject(Category.class);
                        category.setCategory_id(doc.getId());
                        categories.add(category);
                        categoryAdapter.notifyDataSetChanged();

                    }
                    if (categories.size()>0){

                    }


                }
                else {

                }
            }
        });

        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        rvCategory.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvCategory.setAdapter(categoryAdapter);


        return categorys;
    }


}
