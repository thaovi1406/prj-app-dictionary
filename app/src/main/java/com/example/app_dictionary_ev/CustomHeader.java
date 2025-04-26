package com.example.app_dictionary_ev;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomHeader extends ConstraintLayout {
    private TextView tvTitle;
    private ImageView ivBack;
    public CustomHeader(Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }
    private void init(Context context) {
        Log.d("CustomHeader", "init() called");

//        LayoutInflater.from(context).inflate(R.layout.custom_header, this,true);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.nav_bar_header, this, true);
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);

        if (tvTitle != null) {
            Log.d("CustomHeader", "TextView found");
        } else {
            Log.e("CustomHeader", "TextView NOT found!");
        }
        if (ivBack != null) {
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof AppCompatActivity){
                        ((AppCompatActivity) context).onBackPressed();
                    }
                }
            });
        }
    }
    public void setTitle(String title){
//        tvTitle.setText(title);
        if (tvTitle != null) {
            tvTitle.setText(title);
        } else {
            Log.e("CustomHeader", "tvTitle is null");
        }
    }
    public void setBackListener(View.OnClickListener listener){
//        ivBack.setOnClickListener(listener);
        if (ivBack != null) {
            ivBack.setOnClickListener(listener);
        } else {
            Log.e("CustomHeader", "ivBack is null");
        }
    }
}