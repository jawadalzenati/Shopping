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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.OrdersAdapter;
import com.josalla.store.R;
import com.josalla.store.model.Orders;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {
    RecyclerView rvCompleteOrders, rvShippedOrders, rvWaitingOrders, rvDismissed;
    ArrayList<Orders> orders_complete = new ArrayList<>();
    ArrayList<Orders> orders_waiting = new ArrayList<>();
    ArrayList<Orders> orders_shipped = new ArrayList<>();
    ArrayList<Orders> orders_dismissed = new ArrayList<>();

    OrdersAdapter completedAdapter;
    OrdersAdapter waitingAdapter;
    OrdersAdapter shippedAdapter;
    OrdersAdapter dismissedAdapter;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    ConstraintLayout empty;

    TextView tvComplete, tvWaiting, tvShipped, tvDismiss;


    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        rvCompleteOrders = root.findViewById(R.id.rvCompleteOrders);
        rvShippedOrders = root.findViewById(R.id.rvShippedOrders);
        rvWaitingOrders = root.findViewById(R.id.rvWaitingOrders);
        rvDismissed = root.findViewById(R.id.rvDismissed);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        empty = root.findViewById(R.id.empty);

        tvComplete = root.findViewById(R.id.tvComplete);
        tvWaiting = root.findViewById(R.id.tvWaiting);
        tvShipped = root.findViewById(R.id.tvShipped);
        tvDismiss = root.findViewById(R.id.tvDismiss);


        // if (!mAuth.getCurrentUser().getUid().isEmpty()) {


        Query collRef = db.collection("orders").whereEqualTo("order_account", mAuth.getCurrentUser().getUid());
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.e("MAS", "Listen failed.", e);
                    return;
                }

                orders_complete.clear();
                orders_shipped.clear();
                orders_waiting.clear();
                orders_dismissed.clear();

                for (QueryDocumentSnapshot doc : value) {
                    Orders orders = doc.toObject(Orders.class);
                    orders.setOrder_id(doc.getId());

                    long orderTime = Long.parseLong(doc.get("order_number") + "");
                    long currentTime = System.currentTimeMillis();

                    if ((currentTime - orderTime) > 259200000 && (doc.get("order_state") + "").equals("في انتظار الشحن")) {
                        db.collection("orders").document(doc.getId() + "").update("order_state", "تم شحن الطلب");
                        db.collection("admin_orders").document(doc.getId() + "").update("order_state", "تم شحن الطلب");
                    }

                    if ((doc.get("order_state").equals("في انتظار الشحن"))) {
                        orders_waiting.add(orders);
                    } else if ((doc.get("order_state").equals("تم شحن الطلب"))) {
                        orders_shipped.add(orders);
                    } else if ((doc.get("order_state").equals("لم يعد متوفر في المخزون"))) {
                        orders_dismissed.add(orders);
                    } else {
                        orders_complete.add(orders);
                    }

                    Collections.reverse(orders_complete);
                    Collections.reverse(orders_waiting);
                    Collections.reverse(orders_shipped);
                    Collections.reverse(orders_dismissed);

                    completedAdapter.notifyDataSetChanged();
                    waitingAdapter.notifyDataSetChanged();
                    shippedAdapter.notifyDataSetChanged();
                    dismissedAdapter.notifyDataSetChanged();

                    if (orders_complete.size() == 0) {
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        empty.setVisibility(View.GONE);

                    }

                }


            }
        });


        //----------------------
        completedAdapter = new OrdersAdapter(getActivity(), orders_complete);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        rvCompleteOrders.setLayoutManager(manager);
        rvCompleteOrders.setAdapter(completedAdapter);
        //---------------------------------------
        waitingAdapter = new OrdersAdapter(getActivity(), orders_waiting);
        rvWaitingOrders.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvWaitingOrders.setAdapter(waitingAdapter);

        //---------------------------------------
        shippedAdapter = new OrdersAdapter(getActivity(), orders_shipped);
        rvShippedOrders.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvShippedOrders.setAdapter(shippedAdapter);

        //-----------------------------------------
        dismissedAdapter = new OrdersAdapter(getActivity(), orders_dismissed);
        rvDismissed.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDismissed.setAdapter(dismissedAdapter);


        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orders_complete.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);

                }
                tvComplete.setBackground(getActivity().getDrawable(R.drawable.selected));
                tvWaiting.setBackground(getActivity().getDrawable(R.drawable.un_selected));
                tvShipped.setBackground(getActivity().getDrawable(R.drawable.un_selected));
                tvDismiss.setBackground(getActivity().getDrawable(R.drawable.un_selected));

                rvCompleteOrders.setVisibility(View.VISIBLE);
                rvShippedOrders.setVisibility(View.GONE);
                rvWaitingOrders.setVisibility(View.GONE);
                rvDismissed.setVisibility(View.GONE);

            }
        });

        tvComplete.setBackground(getActivity().getDrawable(R.drawable.selected));
        tvWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orders_waiting.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);

                }
                tvComplete.setBackground(getActivity().getDrawable(R.drawable.un_selected));
                tvWaiting.setBackground(getActivity().getDrawable(R.drawable.selected));
                tvShipped.setBackground(getActivity().getDrawable(R.drawable.un_selected));
                tvDismiss.setBackground(getActivity().getDrawable(R.drawable.un_selected));

                rvCompleteOrders.setVisibility(View.GONE);
                rvShippedOrders.setVisibility(View.GONE);
                rvWaitingOrders.setVisibility(View.VISIBLE);
                rvDismissed.setVisibility(View.GONE);

            }
        });

        tvShipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orders_shipped.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);

                }
                tvComplete.setBackground(getActivity().getDrawable(R.drawable.un_selected));
                tvWaiting.setBackground(getActivity().getDrawable(R.drawable.un_selected));
                tvShipped.setBackground(getActivity().getDrawable(R.drawable.selected));
                tvDismiss.setBackground(getActivity().getDrawable(R.drawable.un_selected));


                rvCompleteOrders.setVisibility(View.GONE);
                rvShippedOrders.setVisibility(View.VISIBLE);
                rvWaitingOrders.setVisibility(View.GONE);
                rvDismissed.setVisibility(View.GONE);

            }
        });


        tvDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orders_dismissed.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);

                }
                tvComplete.setBackground(getActivity().getDrawable(R.drawable.un_selected));
                tvWaiting.setBackground(getActivity().getDrawable(R.drawable.un_selected));
                tvShipped.setBackground(getActivity().getDrawable(R.drawable.un_selected));
                tvDismiss.setBackground(getActivity().getDrawable(R.drawable.selected));

                rvCompleteOrders.setVisibility(View.GONE);
                rvShippedOrders.setVisibility(View.GONE);
                rvWaitingOrders.setVisibility(View.GONE);
                rvDismissed.setVisibility(View.VISIBLE);
            }
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        completedAdapter.notifyDataSetChanged();
        waitingAdapter.notifyDataSetChanged();
        shippedAdapter.notifyDataSetChanged();
        dismissedAdapter.notifyDataSetChanged();


    }
}
