package com.application.wowwao1.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Activities.EditProfileActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Activities.ViewProfilePicActivity;
import com.application.wowwao1.Adapters.HomeAdapter;
import com.application.wowwao1.AsyncTask.ImageUploadParseJSON;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.FeedsItem;
import com.application.wowwao1.Models.FeedsPojo;
import com.application.wowwao1.Models.MyProfilePojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.Utils.TimeZoneUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;

/**
 * Created by nct119 on 10/8/17.
 */

public class MyProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final int PROFILE_CODE = 1000;
    private static final int EDIT_PROFILE_CODE = 1001;
    public static boolean pageRefresh = false;
    private RecyclerView rvMyPost;
    private LinearLayoutManager layoutManager;
    private HomeAdapter adapter;
    private ArrayList<FeedsItem> arrayList = new ArrayList<>();
    private LinearLayout layoutNoData, layoutCamera;

    private CircleImageView imgProfile;
    private ImageView imgCover;
    private TextView txtUsername, txtEmail, txtGender, txtLocation, txtPhone, txtDate, txtTotalFollowers;
    private String strEmail, strGender, strLocation, strPhone, strDate, profileImg, coverImg, countryID, stateID, cityID, countryCode, first_name, last_name, total_followers;
    private ImageView imgGender;
    private FloatingActionButton fabChangeCover, fabEdit;
    private Intent intent;

    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private ArrayList<String> imgParams;
    private ArrayList<File> imgValues;

    /*pagination vars start*/
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    int page = 1;
    int total_records = 0;
    /*pagination vars end*/

    /*Image picker variable*/
    private Uri mCropImageUri;
    int coverRatioX = 9, coverRatioY = 4, profileRatioX = 1, profileRationY = 1;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private boolean isProfileCamera;

    /* timezone */
    private String timezone = "";
    private TimeZoneUtils timeZoneUtils = new TimeZoneUtils();
    /* ******** */

    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        initViews(rootView);

        timezone = timeZoneUtils.getTimeZone();
        Log.e("MyProfile", "TimeZone : " + timezone);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(getContext());

       /* if (isInternetAvailable) {
            profileDetails();
            myPost(true);
        } else {
            intent = new Intent(getContext(), NoConnectionActivity.class);
            startActivityForResult(intent, PROFILE_CODE);
        }*/

        swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);


                            if (isInternetAvailable) {
                                profileDetails();
                                myPost(true);
                            } else {
                                intent = new Intent(getContext(), NoConnectionActivity.class);
                                startActivityForResult(intent, PROFILE_CODE);
                            }
                        }
                    }
        );

        fabChangeCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProfileCamera = false;
                if (CropImage.isExplicitCameraPermissionRequired(getActivity())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    //CropImage.startPickImageActivity(getActivity());
                    startActivityForResult(CropImage.getPickImageChooserIntent(getActivity()), PICK_IMAGE_CHOOSER_REQUEST_CODE);
                }
            }
        });

        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProfileCamera = true;
                if (CropImage.isExplicitCameraPermissionRequired(getActivity())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    //CropImage.startPickImageActivity(getActivity());
                    startActivityForResult(CropImage.getPickImageChooserIntent(getActivity()), PICK_IMAGE_CHOOSER_REQUEST_CODE);
                }
            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(getActivity());
                if (isInternetAvailable) {
                    intent = new Intent(getContext(), EditProfileActivity.class);
                    intent.putExtra("first_name", first_name);
                    intent.putExtra("last_name", last_name);
                    intent.putExtra("profile_img", profileImg);
                    intent.putExtra("gender", strGender);
                    intent.putExtra("dob", strDate);
                    intent.putExtra("countryCode", countryCode);
                    intent.putExtra("phone_no", strPhone);
                    intent.putExtra("countryID", countryID);
                    intent.putExtra("stateID", stateID);
                    intent.putExtra("cityID", cityID);
                    startActivityForResult(intent, PROFILE_CODE);
                } else {
                    intent = new Intent(getContext(), NoConnectionActivity.class);
                    startActivityForResult(intent, EDIT_PROFILE_CODE);
                }
            }
        });

        rvMyPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            if (arrayList.size() < total_records) {
                                page++;
                                myPost(false);
                            }
                        }
                    }
                }
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewProfilePicActivity.isProfile = true;
                intent = new Intent(getContext(), ViewProfilePicActivity.class);
                intent.putExtra("img_path", profileImg);
                startActivity(intent);
            }
        });

        imgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewProfilePicActivity.isProfile = false;
                intent = new Intent(getContext(), ViewProfilePicActivity.class);
                intent.putExtra("img_path", coverImg);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void initViews(View rootView) {
        pageRefresh = false;

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiper);

        userId = PrefsUtil.with(getContext()).readString("userId");
        imgProfile = (CircleImageView) rootView.findViewById(R.id.imgProfile);
        imgCover = (ImageView) rootView.findViewById(R.id.imgCover);
        imgGender = (ImageView) rootView.findViewById(R.id.imgGender);

        layoutNoData = (LinearLayout) rootView.findViewById(R.id.layoutNoData);
        layoutCamera = (LinearLayout) rootView.findViewById(R.id.layoutCamera);

        txtUsername = (TextView) rootView.findViewById(R.id.txtUsername);
        txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
        txtGender = (TextView) rootView.findViewById(R.id.txtGender);
        txtLocation = (TextView) rootView.findViewById(R.id.txtLocation);
        txtPhone = (TextView) rootView.findViewById(R.id.txtPhone);
        txtDate = (TextView) rootView.findViewById(R.id.txtDate);
        txtTotalFollowers = (TextView) rootView.findViewById(R.id.txtTotalFollowers);

        fabChangeCover = (FloatingActionButton) rootView.findViewById(R.id.fabChangeCover);
        fabEdit = (FloatingActionButton) rootView.findViewById(R.id.fabEdit);


        rvMyPost = (RecyclerView) rootView.findViewById(R.id.rvMyPost);
        layoutManager = new LinearLayoutManager(getContext());
        rvMyPost.setLayoutManager(layoutManager);
        rvMyPost.setHasFixedSize(false);
        rvMyPost.setNestedScrollingEnabled(false);
        adapter = new HomeAdapter(getActivity(), arrayList);
        rvMyPost.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onRefresh() {
        if (isInternetAvailable) {
            profileDetails();
            myPost(true);
        } else {
            intent = new Intent(getContext(), NoConnectionActivity.class);
            startActivityForResult(intent, PROFILE_CODE);
        }
    }

    private void profileDetails() {
        url = WebServiceUrl.baseUrl;

        swipeRefreshLayout.setRefreshing(true);

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userProfile");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(userId);

        new ParseJSON(getContext(), url, params, values, MyProfilePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        MyProfilePojo resultObj = (MyProfilePojo) obj;

                        first_name = resultObj.getData().getFirstName();
                        last_name = resultObj.getData().getLastName();
                        strEmail = resultObj.getData().getEmail();
                        strGender = resultObj.getData().getGender();
                        strDate = resultObj.getData().getDob();
                        strPhone = resultObj.getData().getPhoneNo();
                        profileImg = resultObj.getData().getProfileImg();
                        PrefsUtil.with(getContext()).write("profile_img", profileImg);
                        coverImg = resultObj.getData().getCoverImg();
                        PrefsUtil.with(getContext()).write("cover_img", coverImg);
                        countryID = resultObj.getData().getCountryID();
                        stateID = resultObj.getData().getStateID();
                        cityID = resultObj.getData().getCityID();
                        countryCode = resultObj.getData().getPhone_code();
                        total_followers = resultObj.getData().getTotal_follower();

                        Picasso.with(getContext())
                                .load(profileImg)
                                .error(R.drawable.no_user)
                                .placeholder(R.drawable.no_user)
                                .into(imgProfile);

                        Picasso.with(getContext())
                                .load(coverImg)
                                .into(imgCover);

                        if (!strEmail.equals("")) {
                            txtEmail.setText(strEmail);
                        } else {
                            txtEmail.setText(getResources().getString(R.string.not_available));
                        }

                        txtUsername.setText(first_name + " " + last_name);

                        if (strGender.equalsIgnoreCase(String.valueOf(R.string.edit_male))) {
                            imgGender.setImageResource(R.drawable.male);
                            txtGender.setText(strGender);
                        } else if (strGender.equalsIgnoreCase(String.valueOf(R.string.edit_female))) {
                            imgGender.setImageResource(R.drawable.female);
                            txtGender.setText(strGender);
                        } else {
                            txtGender.setText(R.string.gender);
                        }

                        if (resultObj.getData().getCity().equals("") && resultObj.getData().getState().equals("") && resultObj.getData().getCountry().equals("")) {
                            txtLocation.setText(R.string.location);
                        } else {
                            txtLocation.setText(resultObj.getData().getCity() + ",  " + resultObj.getData().getState() + ", " + resultObj.getData().getCountry());
                        }

                        if (!strPhone.equalsIgnoreCase("")) {
                            txtPhone.setText(countryCode + "" + strPhone);
                        } else {
                            txtPhone.setText(R.string.contact_no);
                        }

                        if (!strDate.equalsIgnoreCase("")) {
                            txtDate.setText(strDate);
                        } else {
                            txtDate.setText(R.string.dob);
                        }

                        if (!total_followers.equalsIgnoreCase("")) {
                            txtTotalFollowers.setText(total_followers);
                        } else {
                            txtTotalFollowers.setText("0");
                        }
                        swipeRefreshLayout.setRefreshing(false);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
    }

    private void myPost(boolean refreshFlag) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userPost");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(userId);

        params.add("timezone");
        values.add(timezone);

        params.add("page");
        values.add(String.valueOf(page));

        if (refreshFlag) {
            arrayList.clear();
            adapter.notifyDataSetChanged();
            page = 1;
        }

        new ParseJSON(getContext(), url, params, values, FeedsPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        FeedsPojo resultObj = (FeedsPojo) obj;
                        total_records = resultObj.getTotalRecords();
                        if (total_records > 0) {
                            rvMyPost.setVisibility(View.VISIBLE);
                            layoutNoData.setVisibility(View.GONE);
                            arrayList.addAll(resultObj.getData());
                            adapter.setFromFragment("MyProfileFragment");
                            adapter.notifyDataSetChanged();
                            loading = true;
                        } else {
                            rvMyPost.setVisibility(View.GONE);
                            layoutNoData.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    rvMyPost.setVisibility(View.GONE);
                    layoutNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(getActivity());
            } else {
                Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*mCurrentFragment.setImageUri(mCropImageUri);*/
                if (isProfileCamera) {
                    CropImage.activity(mCropImageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(profileRatioX, profileRationY)
                            .start(getContext(), this);
                } else {
                    CropImage.activity(mCropImageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(coverRatioX, coverRatioY)
                            .start(getContext(), this);
                }
            } else {
                Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getActivity(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                /*mCurrentFragment.setImageUri(imageUri);*/
                if (isProfileCamera) {
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(profileRatioX, profileRationY)
                            .start(getContext(), this);
                } else {
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(coverRatioX, coverRatioY)
                            .start(getContext(), this);
                }
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.e("RESULT URI IS : ", result.getUri().toString());
                if (isProfileCamera) {
                    profileImg = result.getUri().toString();
                    uploadProfileImage(profileImg);
                } else {
                    coverImg = result.getUri().toString();
                    uploadCoverImage(coverImg);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == PROFILE_CODE) {
                profileDetails();
                myPost(true);
            }
        }
    }

    private void uploadCoverImage(final String image_upload) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();
        imgParams = new ArrayList<>();
        imgValues = new ArrayList<>();

        params.add("action");
        values.add("userEditPicture");

        params.add("user_id");
        values.add(userId);

        params.add("picture_type");
        values.add("Cover");

        imgParams.add("image_upload");
        imgValues.add(new File(image_upload.replace("file://", "")));

        new ImageUploadParseJSON(getActivity(), url, params, values, imgParams, imgValues, CommonPojo.class, new ImageUploadParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(getContext(), resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        PrefsUtil.with(getActivity()).write("cover_img", image_upload);
                        Picasso.with(getActivity())
                                .load(image_upload)
                                .into(imgCover);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void uploadProfileImage(final String image_upload) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();
        imgParams = new ArrayList<>();
        imgValues = new ArrayList<>();

        params.add("action");
        values.add("userEditPicture");

        params.add("user_id");
        values.add(userId);

        params.add("picture_type");
        values.add("Profile");

        imgParams.add("image_upload");
        imgValues.add(new File(image_upload.replace("file://", "")));

        new ImageUploadParseJSON(getActivity(), url, params, values, imgParams, imgValues, CommonPojo.class, new ImageUploadParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(getActivity(), resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        PrefsUtil.with(getActivity()).write("profile_img", image_upload);
                        Picasso.with(getActivity())
                                .load(profileImg)
                                .into(imgProfile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pageRefresh) {
            cd = new ConnectionCheck();
            isInternetAvailable = cd.isNetworkConnected(getContext());
            if (isInternetAvailable) {
                myPost(true);
            } else {
                intent = new Intent(getContext(), NoConnectionActivity.class);
                startActivityForResult(intent, PROFILE_CODE);
            }
        }
    }
}
