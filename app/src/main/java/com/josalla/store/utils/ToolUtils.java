package com.josalla.store.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.josalla.store.R;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ToolUtils {

    public static ProgressDialog progressDialog;
    static FirebaseFirestore db;


    static String PAYPAL_CLIEND_ID;
    //AQhWLscH9uM7QkXM7SQyBHFNbXehjc8EnJoN5qlVK0nC80ZJlDIMU-FpJdNiKb3NBIo5DvbTX4uO_1MR
    //AW_pxx4_GDj8JKRlfW4PrUi7ORPVEhGY7wSS6QX_4q9Hs5fxYzvlU29rDrd2OWmLkbDKblnvbvgWv4pZ
    public static final String PAYPAL_CLIENT_ID = "AQhWLscH9uM7QkXM7SQyBHFNbXehjc8EnJoN5qlVK0nC80ZJlDIMU-FpJdNiKb3NBIo5DvbTX4uO_1MR";


    public static void showDialog(Context context, String text) {
        progressDialog = new ProgressDialog(context, R.style.ProgressDialog);
        progressDialog.setMessage(text);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


    public static boolean IsConected(Context context) {
        boolean state = false;
        try {
            InetAddress address = InetAddress.getByName("www.google.ps");
            state = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            state = false;
        }
        return state;
    }


    public static void SendNotificationToAdmin(String title, String text) {
        db = FirebaseFirestore.getInstance();

        Query collRef = db.collection("users").whereEqualTo("user_type", "admin");
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MAS", "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                    SendNotification.send(title + "", text + "", doc.get("user_accessToken") + "");
                }

            }

        });


    }


}


