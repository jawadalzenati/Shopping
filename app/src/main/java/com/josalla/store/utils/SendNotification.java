package com.josalla.store.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendNotification {
    public static void send(String title, String notificationMasseg, String token) {

        String url = "https://fcm.googleapis.com/fcm/send";

        JSONObject jsonObject = new JSONObject();
        try {
            // jsonObject.put("to", "/topics/all");
            jsonObject.put("to", token);

            jsonObject.put("collapse_key", "type_a");

            JSONObject notification = new JSONObject();
            notification.put("body", notificationMasseg);
            notification.put("title", title);
            notification.put("sound", "default");


            jsonObject.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put("body", "Body of Your Notification in Data");
            data.put("title", "Body of Your Notification in Data");
            data.put("key_1", "Value for key_1");

            jsonObject.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=AIzaSyDUawS3T3CstyRSdnd4zeslaLJW-h2QmFY");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public static void send(String title, String notificationMasseg) {

        String url = "https://fcm.googleapis.com/fcm/send";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", "/topics/all");

            jsonObject.put("collapse_key", "type_a");

            JSONObject notification = new JSONObject();
            notification.put("body", notificationMasseg);
            notification.put("title", title);
            notification.put("sound", "default");


            jsonObject.put("notification", notification);

            JSONObject data = new JSONObject();
            data.put("body", "Body of Your Notification in Data");
            data.put("title", "Body of Your Notification in Data");
            data.put("key_1", "Value for key_1");

            jsonObject.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=AIzaSyDUawS3T3CstyRSdnd4zeslaLJW-h2QmFY");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
