package com.josalla.store;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.UsersAdapter;
import com.josalla.store.model.Users;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AllUsersActivity extends AppCompatActivity {

    FirebaseFirestore db;

    ImageView ivBack;
    SearchView svSearch;
    RecyclerView rvUsers, rvSearchResult;

    ArrayList<Users> all_users = new ArrayList<>();
    ArrayList<Users> search_result = new ArrayList<>();

    UsersAdapter usersAdapter, usersSearchAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        db = FirebaseFirestore.getInstance();
        ivBack = findViewById(R.id.ivBack);
        svSearch = findViewById(R.id.svSearch);
        rvUsers = findViewById(R.id.rvUsers);
        rvSearchResult = findViewById(R.id.rvSearchResult);



        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("MAS", document.getId() + " => " + document.getData());

                                Users users = document.toObject(Users.class);
                                users.setUser_id(document.getId());
                                all_users.add(users);

                                usersAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.e("MAS", "Error getting documents.", task.getException());
                        }
                    }
                });

        usersAdapter = new UsersAdapter(this, all_users);

        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 1);
        rvUsers.setLayoutManager(manager);
        rvUsers.setAdapter(usersAdapter);


        //search

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {
                    rvUsers.setVisibility(View.GONE);
                    rvSearchResult.setVisibility(View.VISIBLE);


                    Query collRef = db.collection("users").orderBy("user_name").startAt(newText).endAt(newText + "\uf8ff");
                    collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.e("MAS", "Listen failed.", e);
                                return;
                            }
                            search_result.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                Users users = doc.toObject(Users.class);
                                users.setUser_id(doc.getId());
                                search_result.add(users);
                                usersSearchAdapter.notifyDataSetChanged();

                            }

                            if (search_result.size() == 0) {
                                Toasty.info(AllUsersActivity.this, "لايوجد نتائج", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                    usersSearchAdapter = new UsersAdapter(AllUsersActivity.this, search_result);
                    rvSearchResult.setLayoutManager(new GridLayoutManager(AllUsersActivity.this, 1));
                    rvSearchResult.setAdapter(usersSearchAdapter);


                } else {
                    rvUsers.setVisibility(View.VISIBLE);
                    rvSearchResult.setVisibility(View.GONE);
                }


                return false;

            }

        });


        // finish
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
