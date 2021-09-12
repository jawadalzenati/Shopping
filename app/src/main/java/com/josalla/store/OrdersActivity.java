package com.josalla.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.Adapters.AllOrdersAdapter;
import com.josalla.store.Adapters.OrdersAdapter;
import com.josalla.store.model.Orders;

import java.util.ArrayList;
import java.util.Collections;

public class OrdersActivity extends AppCompatActivity {

    ImageView ivBack;

    RecyclerView rvCompleteOrders, rvShippedOrders, rvWaitingOrders, rvDismissed;
    ArrayList<Orders> orders_complete = new ArrayList<>();
    ArrayList<Orders> orders_waiting = new ArrayList<>();
    ArrayList<Orders> orders_shipped = new ArrayList<>();
    ArrayList<Orders> orders_dismissed = new ArrayList<>();

    AllOrdersAdapter completedAdapter;
    AllOrdersAdapter waitingAdapter;
    AllOrdersAdapter shippedAdapter;
    OrdersAdapter dismissedAdapter;

    ConstraintLayout empty;
    FirebaseFirestore db;
    TextView tvComplete, tvWaiting, tvShipped, tvDismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ivBack = findViewById(R.id.ivBack);
        rvCompleteOrders = findViewById(R.id.rvCompleteOrders);
        rvShippedOrders = findViewById(R.id.rvShippedOrders);
        rvWaitingOrders = findViewById(R.id.rvWaitingOrders);
        rvDismissed = findViewById(R.id.rvDismissed);


        tvComplete = findViewById(R.id.tvComplete);
        tvWaiting = findViewById(R.id.tvWaiting);
        tvShipped = findViewById(R.id.tvShipped);
        tvDismiss = findViewById(R.id.tvDismiss);


        empty = findViewById(R.id.empty);

        db = FirebaseFirestore.getInstance();


        CollectionReference collRef = db.collection("admin_orders");
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

                    if ((currentTime - orderTime) > 259200000 && ((doc.get("order_state") + "").equals("في انتظار الشحن"))) {
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

                    if (orders_waiting.size() == 0) {
                        empty.setVisibility(View.VISIBLE);
                    } else {
                        empty.setVisibility(View.GONE);

                    }

                }


            }
        });

        //----------------------
        completedAdapter = new AllOrdersAdapter(this, orders_complete);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rvCompleteOrders.setLayoutManager(manager);
        rvCompleteOrders.setAdapter(completedAdapter);
        //---------------------------------------
        waitingAdapter = new AllOrdersAdapter(this, orders_waiting);
        rvWaitingOrders.setLayoutManager(new LinearLayoutManager(this));
        rvWaitingOrders.setAdapter(waitingAdapter);

        //---------------------------------------
        shippedAdapter = new AllOrdersAdapter(this, orders_shipped);
        rvShippedOrders.setLayoutManager(new LinearLayoutManager(this));
        rvShippedOrders.setAdapter(shippedAdapter);

        //----------------------------------------
        dismissedAdapter = new OrdersAdapter(this, orders_dismissed);
        rvDismissed.setLayoutManager(new LinearLayoutManager(this));
        rvDismissed.setAdapter(dismissedAdapter);


        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orders_complete.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);

                }
                tvComplete.setBackground(getDrawable(R.drawable.selected));
                tvWaiting.setBackground(getDrawable(R.drawable.un_selected));
                tvShipped.setBackground(getDrawable(R.drawable.un_selected));
                tvDismiss.setBackground(getDrawable(R.drawable.un_selected));


                rvCompleteOrders.setVisibility(View.VISIBLE);
                rvShippedOrders.setVisibility(View.GONE);
                rvWaitingOrders.setVisibility(View.GONE);
                rvDismissed.setVisibility(View.GONE);

            }
        });


        tvWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orders_waiting.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);

                }
                tvComplete.setBackground(getDrawable(R.drawable.un_selected));
                tvWaiting.setBackground(getDrawable(R.drawable.selected));
                tvShipped.setBackground(getDrawable(R.drawable.un_selected));
                tvDismiss.setBackground(getDrawable(R.drawable.un_selected));

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
                tvComplete.setBackground(getDrawable(R.drawable.un_selected));
                tvWaiting.setBackground(getDrawable(R.drawable.un_selected));
                tvShipped.setBackground(getDrawable(R.drawable.selected));
                tvDismiss.setBackground(getDrawable(R.drawable.un_selected));

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
                tvComplete.setBackground(getDrawable(R.drawable.un_selected));
                tvWaiting.setBackground(getDrawable(R.drawable.un_selected));
                tvShipped.setBackground(getDrawable(R.drawable.un_selected));
                tvDismiss.setBackground(getDrawable(R.drawable.selected));

                rvCompleteOrders.setVisibility(View.GONE);
                rvShippedOrders.setVisibility(View.GONE);
                rvWaitingOrders.setVisibility(View.GONE);
                rvDismissed.setVisibility(View.VISIBLE);
            }
        });


        //------------------------- finish ----------------
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

