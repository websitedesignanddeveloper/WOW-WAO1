package com.application.wowwao1.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.application.wowwao1.Adapters.ImagesAdapter;
import com.application.wowwao1.Application.ApplicationController;
import com.application.wowwao1.Models.FeedsImageItem;
import com.application.wowwao1.R;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DisplayPhotosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title_text;
    private RecyclerView rvImages;
    private LinearLayoutManager layoutManager;
    private ArrayList<FeedsImageItem> arrayList = new ArrayList<>();
    private ImagesAdapter adapter;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photos);
        initViews();
        arrayList = new ArrayList<>(((ApplicationController) getApplication()).arrayList);
        adapter = new ImagesAdapter(mContext, arrayList);
        rvImages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initViews() {
        mContext = DisplayPhotosActivity.this;
        setupToolbar();

        rvImages = (RecyclerView) findViewById(R.id.rvImages);
        layoutManager = new LinearLayoutManager(mContext);
        rvImages.setLayoutManager(layoutManager);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title_text = (TextView) findViewById(R.id.title_text);
        setSupportActionBar(toolbar);
        title_text.setText(R.string.photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
