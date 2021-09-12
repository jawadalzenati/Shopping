package com.josalla.store.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josalla.store.R;
import com.josalla.store.model.UsersShear;

import java.util.ArrayList;

public class UsersShearsAdapter extends RecyclerView.Adapter<UsersShearsAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<UsersShear> data;

    public UsersShearsAdapter(Activity activity, ArrayList<UsersShear> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public UsersShearsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.user_searh, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UsersShearsAdapter.MyViewHolder holder, final int position) {


        if (data.get(position).getShear_state().equals("yet")){
            holder.tv.setText("مبلغ معلق بقمية: "+data.get(position).getUser_shear() +" $ ");
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.tv);


        }
    }
}