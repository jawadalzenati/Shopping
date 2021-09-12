package com.josalla.store.Fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.NotificationAdapter;
import com.josalla.store.R;
import com.josalla.store.model.Notification;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    ConstraintLayout empty;
    RecyclerView rvNotification;

    FirebaseFirestore db;
    FirebaseAuth mAuth;



    ArrayList<Notification> all_notifi = new ArrayList<>();
    NotificationAdapter notificationAdapter;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View notifi = inflater.inflate(R.layout.fragment_notification, container, false);

        empty = notifi.findViewById(R.id.empty);
        rvNotification = notifi.findViewById(R.id.rvNotification);



        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                String user_type = doc.get("user_type") + "";

                if (user_type.equals("admin")) {

                    Query collRef = db.collection("notification").whereEqualTo("notifi_tag", "admin");
                    collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.e("MAS", "Listen failed.", e);
                                return;
                            }
                            all_notifi.clear();

                            for (QueryDocumentSnapshot doc : value) {
                                Notification notification = doc.toObject(Notification.class);
                                notification.setNotifi_id(doc.getId());
                                all_notifi.add(notification);
                                Collections.reverse(all_notifi);
                                notificationAdapter.notifyDataSetChanged();
                            }
                            if (all_notifi.size() > 0) {
                                empty.setVisibility(View.GONE);
                            }


                        }
                    });

                    notificationAdapter = new NotificationAdapter(getActivity(), all_notifi);
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());

                    rvNotification.setLayoutManager(manager);
                    rvNotification.setAdapter(notificationAdapter);

                } else {
                    Query collRef = db.collection("notification").whereEqualTo("notifi_user", mAuth.getCurrentUser().getUid());
                    collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.e("MAS", "Listen failed.", e);
                                return;
                            }
                            all_notifi.clear();

                            for (QueryDocumentSnapshot doc : value) {
                                if ((doc.get("notifi_title") + "").contains("طلب جديد")) {
                                    continue;
                                } else {
                                    Notification notification = doc.toObject(Notification.class);
                                    notification.setNotifi_id(doc.getId());
                                    all_notifi.add(notification);
                                    Collections.reverse(all_notifi);
                                    notificationAdapter.notifyDataSetChanged();
                                }
                            }
                            if (all_notifi.size() > 0) {
                                empty.setVisibility(View.GONE);
                            }


                        }
                    });

                    notificationAdapter = new NotificationAdapter(getActivity(), all_notifi);
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());

                    rvNotification.setLayoutManager(manager);
                    rvNotification.setAdapter(notificationAdapter);
                }


            }
        });


        return notifi;
    }

}
