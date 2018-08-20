package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Application.ApplicationController;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.FeedsImageItem;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.application.wowwao1.viewpagerindicator.MyPagerAdapter;
import com.application.wowwao1.viewpagerindicator.ViewPagerIndicator;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SharePostActivity extends AppCompatActivity {

    private static final int SHARE_CODE = 100;
    public static boolean editSharedPost = false;
    private Toolbar toolbar;
    private TextView title_text, txtSharedUsername, txtSharedDate, txtSharedDescription;
    private Context mContext;
    private EditText edtWriteSomething;
    private Button btnShare, btnCancel;
    private CircleImageView imgSharedProfile;
    private ImageView imgSharedPost, imgShared1, imgShared3;
    private LinearLayout layoutSharedSingleImage, layoutSharedMultiImage, layoutSharedViewMore;
    private RelativeLayout layoutSharedImg3;
    private ImageView imgPublic, imgPrivate;
    private Intent intent;

    private String url, userId, post_id, profileImg, username, post_date, post_time, post_text, shared_post_text, post_type;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private ArrayList<FeedsImageItem> arrayList = new ArrayList<>();

    private boolean isInternetAvailable;
    private ConnectionCheck cd;

    private RadioButton radioPublic, radioPrivate;
    private FrameLayout frameShareImage;
    private ViewPager viewPagerShare;
    private ViewPagerIndicator viewPagerIndicatorShare;
    private int imgHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_post);
        initViews();

        imgHeight = (((int) (getDispWidth() - convertDpToPixel(52f, mContext))) / 4) * 3;
        Log.e("adapter_home", "imgHeight -------------------> " + imgHeight);

        arrayList = new ArrayList<>(((ApplicationController) getApplication()).arrayList);
        intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        profileImg = intent.getStringExtra("profileImg");
        username = intent.getStringExtra("username");
        post_date = intent.getStringExtra("post_date");
        post_time = intent.getStringExtra("post_time");
        post_text = intent.getStringExtra("post_text");
        shared_post_text = intent.getStringExtra("shared_post_text");
        post_type = intent.getStringExtra("post_type");

        Log.e("SharePost", "post_id : " + post_id);
        Log.e("SharePost", "profileImg : " + profileImg);
        Log.e("SharePost", "username : " + username);
        Log.e("SharePost", "post_date : " + post_date);
        Log.e("SharePost", "post_time : " + post_time);
        Log.e("SharePost", "post_text : " + post_text);
        Log.e("SharePost", "shared_post_text : " + shared_post_text);
        Log.e("SharePost", "post_type : " + post_type);

        setIntentData();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared_post_text = edtWriteSomething.getText().toString().trim();

                if (radioPublic.isChecked()) {
                    post_type = radioPublic.getText().toString();
                } else if (radioPrivate.isChecked()) {
                    post_type = radioPrivate.getText().toString();
                }

                cd = new ConnectionCheck();
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    if (editSharedPost) {
                        editSharedPostCall(post_id, shared_post_text, post_type);
                    } else {
                        sharePost(post_id, shared_post_text, post_type);
                    }
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, SHARE_CODE);
                }

            }
        });

        /*layoutSharedViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ApplicationController) getApplication()).arrayList = arrayList;
                intent = new Intent(mContext, DisplayPhotosActivity.class);
                startActivity(intent);
            }
        });*/

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        mContext = SharePostActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");
        setupToolbar();
        /*layoutSharedSingleImage = (LinearLayout) findViewById(R.id.layoutSharedSingleImage);
        layoutSharedMultiImage = (LinearLayout) findViewById(R.id.layoutSharedMultiImage);
        layoutSharedViewMore = (LinearLayout) findViewById(R.id.layoutSharedViewMore);
        layoutSharedImg3 = (RelativeLayout) findViewById(R.id.layoutSharedImg3);*/

        imgSharedProfile = (CircleImageView) findViewById(R.id.imgSharedProfile);

        /*imgSharedPost = (ImageView) findViewById(R.id.imgSharedPost);
        imgShared1 = (ImageView) findViewById(R.id.imgShared1);
        imgShared3 = (ImageView) findViewById(R.id.imgShared3);*/

        txtSharedUsername = (TextView) findViewById(R.id.txtSharedUsername);
        txtSharedDate = (TextView) findViewById(R.id.txtSharedDate);
        txtSharedDescription = (TextView) findViewById(R.id.txtSharedDescription);

        edtWriteSomething = (EditText) findViewById(R.id.edtWriteSomething);

        btnShare = (Button) findViewById(R.id.btnShare);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        radioPrivate = (RadioButton) findViewById(R.id.radioPrivate);
        radioPublic = (RadioButton) findViewById(R.id.radioPublic);
        imgPublic = (ImageView) findViewById(R.id.imgPublic);
        imgPrivate = (ImageView) findViewById(R.id.imgPrivate);

        frameShareImage = (FrameLayout) findViewById(R.id.frameShareImage);
        viewPagerShare = (ViewPager) findViewById(R.id.viewPagerShare);
        viewPagerIndicatorShare = (ViewPagerIndicator) findViewById(R.id.viewPagerIndicatorShare);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title_text = (TextView) findViewById(R.id.title_text);
        setSupportActionBar(toolbar);
        title_text.setText(R.string.share_post_title);
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

    private void setIntentData() {
        Picasso.with(mContext)
                .load(profileImg)
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(imgSharedProfile);

        txtSharedUsername.setText(username);
        txtSharedDate.setText(post_date + " | " + post_time);

        if (post_type.equals(R.string.new_post_private)) {
            radioPublic.setChecked(false);
            radioPrivate.setChecked(true);
            imgPublic.setImageResource(R.drawable.public_2);
            imgPrivate.setImageResource(R.drawable.private_1);
        } else if (post_type.equals(R.string.new_post_public)) {
            radioPublic.setChecked(true);
            radioPrivate.setChecked(false);
            imgPublic.setImageResource(R.drawable.public_1);
            imgPrivate.setImageResource(R.drawable.private_2);
        }

        if (post_text.length() != 0) {
            txtSharedDescription.setVisibility(View.VISIBLE);
            //txtSharedDescription.setText(post_text);
            txtSharedDescription.setText(StringEscapeUtils.unescapeJava(post_text));
        } else {
            txtSharedDescription.setVisibility(View.GONE);
        }

        if (editSharedPost) {
            edtWriteSomething.setText(post_text);
        } else {
            edtWriteSomething.setText("");
        }

        if (arrayList.size() > 0) {
            frameShareImage.setVisibility(View.VISIBLE);
            frameShareImage.getLayoutParams().height = imgHeight;
            MyPagerAdapter myPagerAdapter = new MyPagerAdapter(mContext, arrayList);
            viewPagerShare.setAdapter(myPagerAdapter);
            myPagerAdapter.notifyDataSetChanged();
            viewPagerIndicatorShare.setupWithViewPager(viewPagerShare);
            viewPagerIndicatorShare.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {

        }

        /*if (arrayList.size() > 0) {
            if (arrayList.size() == 1) {
                imgSharedPost.setVisibility(View.VISIBLE);
                layoutSharedMultiImage.setVisibility(View.GONE);
                layoutSharedViewMore.setVisibility(View.GONE);

                Picasso.with(mContext)
                        .load(arrayList.get(0).getPath())
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(imgSharedPost);
            } else if (arrayList.size() == 2) {
                imgSharedPost.setVisibility(View.GONE);
                layoutSharedMultiImage.setVisibility(View.VISIBLE);
                layoutSharedViewMore.setVisibility(View.GONE);
                imgShared1.setVisibility(View.VISIBLE);
                layoutSharedImg3.setVisibility(View.VISIBLE);

                Picasso.with(mContext)
                        .load(arrayList.get(0).getPath())
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(imgShared1);

                Picasso.with(mContext)
                        .load(arrayList.get(1).getPath())
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(imgShared3);
            } else if (arrayList.size() > 2) {
                imgSharedPost.setVisibility(View.GONE);
                layoutSharedMultiImage.setVisibility(View.VISIBLE);
                layoutSharedViewMore.setVisibility(View.VISIBLE);
                imgShared1.setVisibility(View.VISIBLE);
                layoutSharedImg3.setVisibility(View.VISIBLE);

                Picasso.with(mContext)
                        .load(arrayList.get(0).getPath())
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(imgShared1);

                Picasso.with(mContext)
                        .load(arrayList.get(1).getPath())
                        .error(R.drawable.progress_animation)
                        .placeholder(R.drawable.progress_animation)
                        .into(imgShared3);
            }
        } else {
            imgSharedPost.setVisibility(View.GONE);
            layoutSharedMultiImage.setVisibility(View.GONE);
            layoutSharedViewMore.setVisibility(View.GONE);
            imgShared1.setVisibility(View.GONE);
            layoutSharedImg3.setVisibility(View.GONE);
        }*/
    }

    private void sharePost(String post_id, String shared_post_text, String post_type) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userSharePost");

        params.add("user_id");
        values.add(userId);

        params.add("post_id");
        values.add(post_id);

        params.add("share_post_text");
        values.add(shared_post_text);

        params.add("post_type");
        values.add(post_type);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    private void editSharedPostCall(String post_id, String shared_post_text, String post_type) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("editSharedPost");

        params.add("user_id");
        values.add(userId);

        params.add("post_id");
        values.add(post_id);

        params.add("share_post_text");
        values.add(shared_post_text);

        params.add("post_type");
        values.add(post_type);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SHARE_CODE) {
                if (editSharedPost) {
                    editSharedPostCall(post_id, shared_post_text, post_type);
                } else {
                    sharePost(post_id, shared_post_text, post_type);
                }
            }
        }
    }

    public int getDispWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
