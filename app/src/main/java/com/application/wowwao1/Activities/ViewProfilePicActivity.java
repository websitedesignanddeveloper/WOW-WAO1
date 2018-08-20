package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.application.wowwao1.R;
import com.squareup.picasso.Picasso;

public class ViewProfilePicActivity extends AppCompatActivity {

    public static boolean isProfile = false;
    private ImageView imgProfile,imgCover;
    private Context mContext;
    private Intent intent;
    private String img_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_pic);
        initViews();
        intent = getIntent();
        img_path = intent.getStringExtra("img_path");

        if (isProfile) {
            imgProfile.setVisibility(View.VISIBLE);
            imgCover.setVisibility(View.GONE);
            Picasso.with(mContext)
                    .load(img_path)
                    .error(R.drawable.progress_animation)
                    .placeholder(R.drawable.progress_animation)
                    .into(imgProfile);
        } else {
            imgProfile.setVisibility(View.GONE);
            imgCover.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(img_path)
                    .error(R.drawable.progress_animation)
                    .placeholder(R.drawable.progress_animation)
                    .into(imgCover);
        }

    }

    private void initViews() {
        mContext = ViewProfilePicActivity.this;
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        imgCover = (ImageView) findViewById(R.id.imgCover);
    }
}
