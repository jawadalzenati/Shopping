package com.josalla.store.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.josalla.store.FullImageScreenActivity;
import com.josalla.store.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderProductAdapter extends SliderViewAdapter<SliderProductAdapter.SliderAdapterVH> {

    private Activity activity;
    ArrayList<String> data;

    public SliderProductAdapter(Activity activity, ArrayList<String> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.produc_image_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        viewHolder.textViewDescription.setText("This is slider item " + position);
        Glide.with(activity).load(data.get(position) + "").into(viewHolder.imageViewBackground);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, FullImageScreenActivity.class);
                intent.putStringArrayListExtra("images", data);
                activity.startActivity(intent);

            }
        });


    }

    @Override
    public int getCount() {
        return data.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.imageViewBackground);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            this.itemView = itemView;
        }
    }
}