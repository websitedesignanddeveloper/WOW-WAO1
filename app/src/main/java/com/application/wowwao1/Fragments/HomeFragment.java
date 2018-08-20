package com.application.wowwao1.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.application.wowwao1.Activities.AdDetailsActivity;
import com.application.wowwao1.Activities.AddNewPostActivity;
import com.application.wowwao1.Activities.EditProfileActivity;
import com.application.wowwao1.Activities.SearchActivity;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.AdsPojo;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by nct119 on 9/8/17.
 */

public class HomeFragment extends Fragment {

    private static final int EDIT_PROFILE_CODE = 1000;
    private LinearLayout layoutNearBy, layoutFollowers, layoutAddNewPost, layoutFriends, layoutProfile,
            viewNearBy, viewFollowers, viewFriends, viewProfile;
    private ImageView imgAd;
    private CircleImageView imgProfile;
    private Intent intent;
    private String url, ad_id, name, imageLink, ad_image;
    private ArrayList<String> params;
    private ArrayList<String> values;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(rootView);
        setHasOptionsMenu(true);

        Picasso.with(getContext())
                .load(PrefsUtil.with(getContext()).readString("profile_img"))
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(imgProfile);

        if (PrefsUtil.with(getContext()).readBoolean("newAccount")) {
            //PrefsUtil.with(getContext()).write("newAccount", false);
            intent = new Intent(getContext(), EditProfileActivity.class);
            intent.putExtra("first_name", PrefsUtil.with(getContext()).readString("first_name"));
            intent.putExtra("last_name", PrefsUtil.with(getContext()).readString("last_name"));
            intent.putExtra("profile_img", PrefsUtil.with(getContext()).readString("profile_img"));
            intent.putExtra("gender", PrefsUtil.with(getContext()).readString("gender"));
            intent.putExtra("dob", PrefsUtil.with(getContext()).readString("dob"));
            intent.putExtra("countryCode", PrefsUtil.with(getContext()).readString("phone_code"));
            intent.putExtra("phone_no", PrefsUtil.with(getContext()).readString("phone_no"));
            intent.putExtra("countryID", PrefsUtil.with(getContext()).readString("countryID"));
            intent.putExtra("stateID", PrefsUtil.with(getContext()).readString("stateID"));
            intent.putExtra("cityID", PrefsUtil.with(getContext()).readString("cityID"));
            startActivityForResult(intent, EDIT_PROFILE_CODE);
        } else {
            viewNearBy.setVisibility(View.VISIBLE);
            viewFollowers.setVisibility(View.GONE);
            viewFriends.setVisibility(View.GONE);
            viewProfile.setVisibility(View.GONE);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentHome, new NearByPostFragment())
                    .commit();
        }

        ad_id = PrefsUtil.with(getContext()).readString("ad_id");
        if (!ad_id.equals("")) {
            getAds(ad_id);
        } else {
            imgAd.setVisibility(View.GONE);
        }

        layoutNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNearBy.setVisibility(View.VISIBLE);
                viewFollowers.setVisibility(View.GONE);
                viewFriends.setVisibility(View.GONE);
                viewProfile.setVisibility(View.GONE);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentHome, new NearByPostFragment())
                        .commit();
            }
        });

        layoutFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNearBy.setVisibility(View.GONE);
                viewFollowers.setVisibility(View.VISIBLE);
                viewFriends.setVisibility(View.GONE);
                viewProfile.setVisibility(View.GONE);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentHome, new FollowersPostFragment())
                        .commit();
            }
        });

        layoutAddNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewPostActivity.isEditPost = false;
                intent = new Intent(getContext(), AddNewPostActivity.class);
                startActivity(intent);
            }
        });

        layoutFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNearBy.setVisibility(View.GONE);
                viewFollowers.setVisibility(View.GONE);
                viewFriends.setVisibility(View.VISIBLE);
                viewProfile.setVisibility(View.GONE);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentHome, new FriendsPostFragment())
                        .commit();
            }
        });

        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNearBy.setVisibility(View.GONE);
                viewFollowers.setVisibility(View.GONE);
                viewFriends.setVisibility(View.GONE);
                viewProfile.setVisibility(View.VISIBLE);


                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentHome, new MyProfileFragment())
                        .commit();
            }
        });

        imgAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdClickedCall(ad_id, name, imageLink);
            }
        });
        return rootView;
    }

    private void getAds(String id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("getAdvertisement");

        params.add("ad_id");
        values.add(id);

        new ParseJSON(getContext(), url, params, values, AdsPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        imgAd.setVisibility(View.VISIBLE);
                        AdsPojo resultObj = (AdsPojo) obj;
                        Picasso.with(getContext())
                                .load(resultObj.getData().getImage())
                                .into(imgAd);
                        ad_image = resultObj.getData().getImage();
                        ad_id = resultObj.getData().getId();
                        name = resultObj.getData().getName();
                        imageLink = resultObj.getData().getLink();
                        PrefsUtil.with(getContext()).write("ad_id", ad_id);
                        PrefsUtil.with(getContext()).write("ad_image", ad_image);
                        PrefsUtil.with(getContext()).write("ad_name", name);
                        PrefsUtil.with(getContext()).write("ad_imageLink", imageLink);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    imgAd.setVisibility(View.GONE);
                }
            }
        });
    }

    private void onAdClickedCall(String ad_id, final String name, final String imageLink) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("advertisementClick");

        params.add("ad_id");
        values.add(ad_id);

        new ParseJSON(getContext(), url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        intent = new Intent(getContext(), AdDetailsActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("link", imageLink);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initViews(View rootView) {
        imgProfile = (CircleImageView) rootView.findViewById(R.id.imgProfile);
        imgAd = (ImageView) rootView.findViewById(R.id.imgAd);

        layoutNearBy = (LinearLayout) rootView.findViewById(R.id.layoutNearBy);
        layoutFollowers = (LinearLayout) rootView.findViewById(R.id.layoutFollowers);
        layoutAddNewPost = (LinearLayout) rootView.findViewById(R.id.layoutAddNewPost);
        layoutFriends = (LinearLayout) rootView.findViewById(R.id.layoutFriends);
        layoutProfile = (LinearLayout) rootView.findViewById(R.id.layoutProfile);

        viewNearBy = (LinearLayout) rootView.findViewById(R.id.viewNearBy);
        viewFollowers = (LinearLayout) rootView.findViewById(R.id.viewFollowers);
        viewFriends = (LinearLayout) rootView.findViewById(R.id.viewFriends);
        viewProfile = (LinearLayout) rootView.findViewById(R.id.viewProfile);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_PROFILE_CODE) {
                viewNearBy.setVisibility(View.GONE);
                viewFollowers.setVisibility(View.GONE);
                viewFriends.setVisibility(View.GONE);
                viewProfile.setVisibility(View.VISIBLE);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentHome, new MyProfileFragment())
                        .commit();
            }
        }*/
    }
}
