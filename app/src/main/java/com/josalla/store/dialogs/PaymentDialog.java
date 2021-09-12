package com.josalla.store.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.josalla.store.R;


public class PaymentDialog extends BottomSheetDialog {
    Activity activity;
    Dialog dialog;
    ImageView check;

    MediaPlayer mediaPlayer;

    public PaymentDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        mediaPlayer = MediaPlayer.create(activity, R.raw.success);
        mediaPlayer.start();
        check = findViewById(R.id.check);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }
}
