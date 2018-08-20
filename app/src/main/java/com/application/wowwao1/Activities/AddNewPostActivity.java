package com.application.wowwao1.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Adapters.AddNewPostAdapter;
import com.application.wowwao1.Application.ApplicationController;
import com.application.wowwao1.AsyncTask.ImageUploadParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.FeedsImageItem;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.KeyboardUtils;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.Utils.TimeZoneUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddNewPostActivity extends AppCompatActivity {

    private static final int ADD_POST_CODE = 100;
    private Toolbar toolbar;
    private TextView title_text;
    private RadioGroup rgPostType;
    private RadioButton radioPublic, radioPrivate;
    private ImageView imgCamera, imgPublic, imgPrivate;
    private String strPostType = "", strDescription, oldPostType, post_id;
    private EditText edtDescription;
    private Intent intent;

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private AddNewPostAdapter adapter;
    private ArrayList<FeedsImageItem> arrayList = new ArrayList<>();

    private Button btnPost;
    private Context mContext;


    /*Image picker variable*/
    private Uri mCropImageUri;

    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private ArrayList<String> imgParams;
    private ArrayList<File> imgvalues;
    public static boolean isEditPost = false;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;

    private int aspectRationX = 4;
    private int aspectRationY = 3;

    /* timezone */
    private String timezone = "";
    private TimeZoneUtils timeZoneUtils = new TimeZoneUtils();
    /* ******** */

    private ArrayList<FeedsImageItem> arrayListNewImg = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);
        initViews();

        timezone = timeZoneUtils.getTimeZone();
        Log.e("FriendsPost", "TimeZone : " + timezone);

        new KeyboardUtils().setupUI(findViewById(R.id.activity_add_new_post), AddNewPostActivity.this);
        cd = new ConnectionCheck();

        intent = getIntent();
        if (isEditPost) {
            title_text.setText(R.string.edit_post_title);
            arrayList.clear();
            arrayList = new ArrayList<>(((ApplicationController) getApplication()).arrayList);
            adapter = new AddNewPostAdapter(mContext, arrayList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            strDescription = intent.getStringExtra("post_text");
            oldPostType = intent.getStringExtra("postType");
            post_id = intent.getStringExtra("post_id");

            Log.e("Edit Post", "post_text : " + strDescription);
            Log.e("Edit Post", "oldPostType : " + oldPostType);
            Log.e("Edit Post", "post_id : " + post_id);

            if (strDescription.length() != 0) {
                edtDescription.setText(strDescription);
            }

            if (oldPostType.equals("Private")) {
                radioPublic.setChecked(false);
                radioPrivate.setChecked(true);
                imgPublic.setImageResource(R.drawable.public_2);
                imgPrivate.setImageResource(R.drawable.private_1);
            } else if (oldPostType.equals("Public")) {
                radioPublic.setChecked(true);
                radioPrivate.setChecked(false);
                imgPublic.setImageResource(R.drawable.public_1);
                imgPrivate.setImageResource(R.drawable.private_2);
            }
        } else {
            title_text.setText(R.string.new_post_title);
        }

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CropImage.isExplicitCameraPermissionRequired(mContext)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    CropImage.startPickImageActivity(AddNewPostActivity.this);
                }
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strDescription = StringEscapeUtils.escapeJava(edtDescription.getText().toString().trim());
                if (radioPublic.isChecked()) {
                    strPostType = radioPublic.getText().toString();
                } else if (radioPrivate.isChecked()) {
                    strPostType = radioPrivate.getText().toString();
                }

                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    if (isEditPost) {
                        if (strDescription.length() > 0 || arrayList.size() > 0) {
                            editPost(arrayList, strDescription, strPostType, oldPostType, post_id);
                        } else {
                            Toast.makeText(mContext, R.string.post_cannot_be_empty, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (strDescription.length() > 0 || arrayList.size() > 0) {
                            newPost(arrayList, strDescription, strPostType);
                        } else {
                            Toast.makeText(mContext, R.string.post_cannot_be_empty, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, ADD_POST_CODE);
                }
            }
        });

        radioPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioPublic.setChecked(true);
                radioPrivate.setChecked(false);
                imgPublic.setImageResource(R.drawable.public_1);
                imgPrivate.setImageResource(R.drawable.private_2);
                strPostType = radioPublic.getText().toString().trim();
            }
        });

        radioPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioPublic.setChecked(false);
                radioPrivate.setChecked(true);
                imgPublic.setImageResource(R.drawable.public_2);
                imgPrivate.setImageResource(R.drawable.private_1);
                strPostType = radioPrivate.getText().toString().trim();
            }
        });
    }

    /*private String getGenderType() {
        radioPostType = (RadioButton) rgPostType.findViewById(rgPostType.getCheckedRadioButtonId());
        if (radioPostType.isChecked()) {
            strPostType = radioPostType.getText().toString().trim();
            if (strPostType.equalsIgnoreCase("Public")) {
                imgPublic.setImageResource(R.drawable.public_1);
                imgPrivate.setImageResource(R.drawable.private_2);
            } else if (strPostType.equalsIgnoreCase("Private")) {
                imgPublic.setImageResource(R.drawable.public_2);
                imgPrivate.setImageResource(R.drawable.private_1);
            }
        }
        return strPostType;
    }*/

    private void initViews() {
        mContext = AddNewPostActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");
        setupToolbar();
        rgPostType = (RadioGroup) findViewById(R.id.rgPostType);
        radioPrivate = (RadioButton) findViewById(R.id.radioPrivate);
        radioPublic = (RadioButton) findViewById(R.id.radioPublic);

        edtDescription = (EditText) findViewById(R.id.edtDescription);

        btnPost = (Button) findViewById(R.id.btnPost);

        imgCamera = (ImageView) findViewById(R.id.imgCamera);
        imgPublic = (ImageView) findViewById(R.id.imgPublic);
        imgPrivate = (ImageView) findViewById(R.id.imgPrivate);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AddNewPostAdapter(mContext, arrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title_text = (TextView) findViewById(R.id.title_text);
        setSupportActionBar(toolbar);
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

    private void newPost(ArrayList<FeedsImageItem> arrayList, String strDescription, String strPostType) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        imgParams = new ArrayList<>();
        imgvalues = new ArrayList<>();

        params.add("action");
        values.add("addPost");

        params.add("user_id");
        values.add(userId);

        params.add("post_type");
        values.add(strPostType);

        params.add("post_text");
        values.add(strDescription);

        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                imgParams.add("post_image[" + (i + 1) + "]");
                imgvalues.add(new File(arrayList.get(i).getPath().replace("file://", "")));
                Log.e("post_image [" + (i + 1) + "] :", arrayList.get(i).getPath().replace("file://", ""));
            }
        }

        new ImageUploadParseJSON(mContext, url, params, values, imgParams, imgvalues, CommonPojo.class, new ImageUploadParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        intent = new Intent(mContext, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void editPost(ArrayList<FeedsImageItem> arrayList, String strDescription, String strPostType, String oldPostType, String post_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        imgParams = new ArrayList<>();
        imgvalues = new ArrayList<>();

        params.add("action");
        values.add("editPost");

        params.add("user_id");
        values.add(userId);

        params.add("post_type");
        values.add(strPostType);

        params.add("post_text");
        values.add(strDescription);

        params.add("postType");
        values.add(oldPostType);

        params.add("post_id");
        values.add(post_id);

        if (arrayListNewImg.size() > 0) {
            for (int i = 0; i < arrayListNewImg.size(); i++) {
                imgParams.add("post_image[" + (i + 1) + "]");
                imgvalues.add(new File(arrayListNewImg.get(i).getPath().replace("file://", "")));
                Log.e("post_image [" + (i + 1) + "] :", arrayListNewImg.get(i).getPath().replace("file://", ""));
            }
        }

        new ImageUploadParseJSON(mContext, url, params, values, imgParams, imgvalues, CommonPojo.class, new ImageUploadParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        intent = new Intent(mContext, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, R.string.cancel_permissions, Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*mCurrentFragment.setImageUri(mCropImageUri);*/
                CropImage.activity(mCropImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(aspectRationX, aspectRationY)
                        .start(this);
            } else {
                Toast.makeText(this, R.string.cancel_permissions, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                /*mCurrentFragment.setImageUri(imageUri);*/
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(aspectRationX, aspectRationY)
                        .start(this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.e("RESULT URI IS : ", result.getUri().toString());
                if (isEditPost) {
                    arrayList.add(new FeedsImageItem("", result.getUri().toString()));
                    adapter.notifyDataSetChanged();
                    arrayListNewImg.add(new FeedsImageItem("", result.getUri().toString()));
                } else {
                    arrayList.add(new FeedsImageItem("", result.getUri().toString()));
                    adapter.notifyDataSetChanged();
                }
                //Uri resultUri = result.getUri();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_POST_CODE) {
                if (isEditPost) {
                    if (arrayList.size() > 0 || strDescription.length() > 0) {
                        editPost(arrayList, strDescription, strPostType, oldPostType, post_id);
                    }
                } else {
                    if (arrayList.size() > 0 || strDescription.length() > 0) {
                        newPost(arrayList, strDescription, strPostType);
                    }
                }
            }
        }
    }
}
