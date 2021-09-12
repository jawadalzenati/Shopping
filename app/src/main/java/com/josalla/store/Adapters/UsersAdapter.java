package com.josalla.store.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.josalla.store.MapsActivity;
import com.josalla.store.R;
import com.josalla.store.model.Users;


import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<Users> data;
    FirebaseFirestore db;
    boolean state;

    public UsersAdapter(Activity activity, ArrayList<Users> data) {
        this.activity = activity;
        this.data = data;
    }

    //------------------------------------------------تركيب القالب ----------------------------------------------------------
    @NonNull
    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(activity).inflate(R.layout.user_itme, null, false);
        return new MyViewHolder(root);
    }

    //----------------------------------------------------------تركيب البيانات-----------------------------------------------
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.MyViewHolder holder, final int position) {

        db = FirebaseFirestore.getInstance();

        holder.tvName.setText("الأسم: " + data.get(position).getUser_name());
        holder.tvMobile.setText("رقم الهاتف: " + data.get(position).getUser_phone().replace("<>", " , "));
        holder.tvAccount.setText("نوع الحساب: " + data.get(position).getUser_type());

        if (data.get(position).getUser_location().length() == 0 && data.get(position).getUser_address().length() == 0) {
            holder.tvAddress.setText("لم يتم ادخال العنوان بعد");
            holder.ivMap.setVisibility(View.GONE);

        } else if ((data.get(position).getUser_address()).length() != 0) {
            holder.tvAddress.setText("العنوان: " + data.get(position).getUser_address());
            if ((data.get(position).getUser_location()).length() != 0 && !(data.get(position).getUser_location()).equals("<>")) {
                holder.ivMap.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tvAddress.setText("تم تحديد العنوان على الخريطة");
            holder.ivMap.setVisibility(View.VISIBLE);
        }


        // move to map
        holder.ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MapsActivity.class);
                intent.putExtra("location", data.get(position).getUser_location());
                activity.startActivity(intent);
            }
        });


        //show block image
        if (data.get(position).getAllowToOrder().equals("no")) {
            holder.ivBlocked.setVisibility(View.VISIBLE);

        } else {
            holder.ivBlocked.setVisibility(View.GONE);
        }


        holder.ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu popupMenu = new PopupMenu(activity, holder.ivOptions);

                popupMenu.getMenuInflater().inflate(R.menu.option, popupMenu.getMenu());

                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.mBlock:

                                dialog.setTitle("تنبيه!")
                                        .setMessage("هل تريد الاستمرار ؟")
                                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        })
                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                db.collection("users").document(data.get(position).getUser_id()).update("allowToOrder", "no");
                                                holder.ivBlocked.setVisibility(View.VISIBLE);


                                            }
                                        }).show();

                                break;

                            case R.id.mAdmin:
                                dialog.setTitle("تنبيه!")
                                        .setMessage("هل تريد الاستمرار ؟")
                                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        })
                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                db.collection("users").document(data.get(position).getUser_id()).update("user_type", "admin");
                                            }
                                        }).show();


                                break;

                            case R.id.mEdit:
                                dialog.setTitle("تنبيه!")
                                        .setMessage("هل تريد الاستمرار ؟")
                                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        })
                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                db.collection("users").document(data.get(position).getUser_id()).update("user_type", "assist");
                                            }
                                        }).show();

                                break;

                            case R.id.mRemove:
                                dialog.setTitle("تنبيه!")
                                        .setMessage("هل تريد الاستمرار ؟")
                                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        })
                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                db.collection("users").document(data.get(position).getUser_id()).update("user_type", "user");
                                            }
                                        }).show();
                                break;

                            case R.id.unBlock:
                                dialog.setTitle("تنبيه!")
                                        .setMessage("هل تريد الاستمرار ؟")
                                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        })
                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                db.collection("users").document(data.get(position).getUser_id()).update("allowToOrder", "yes");
                                                holder.ivBlocked.setVisibility(View.GONE);
                                            }
                                        }).show();
                                break;
                            case R.id.mSubAdmin:
                                dialog.setTitle("تنبيه!")
                                        .setMessage("هل تريد الاستمرار ؟")
                                        .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        })
                                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                db.collection("users").document(data.get(position).getUser_id()).update("user_type", "sub_admin");
                                                holder.ivBlocked.setVisibility(View.GONE);
                                            }
                                        }).show();
                                break;
                        }

                        return true;
                    }
                });
                popupMenu.show();
                //show block image
                if (data.get(position).getAllowToOrder().equals("no")) {
                    holder.ivBlocked.setVisibility(View.VISIBLE);

                } else {
                    holder.ivBlocked.setVisibility(View.GONE);
                }

                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //--------------------------------------------------------------------امساك العناصر التي في القالب--------------------------------------------
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout constraintLayout;
        public ImageView ivOptions, ivMap, ivBlocked;
        public TextView tvName, tvMobile, tvAddress, tvAccount;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            ivOptions = itemView.findViewById(R.id.ivOptions);
            ivMap = itemView.findViewById(R.id.ivMap);
            tvName = itemView.findViewById(R.id.tvName);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvAccount = itemView.findViewById(R.id.tvAccount);
            ivBlocked = itemView.findViewById(R.id.ivBlocked);

        }
    }

}