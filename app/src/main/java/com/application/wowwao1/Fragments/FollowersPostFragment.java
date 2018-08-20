package com.application.wowwao1.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Adapters.HomeAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.FeedsItem;
import com.application.wowwao1.Models.FeedsPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.Utils.TimeZoneUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nct119 on 9/8/17.
 */

public class FollowersPostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final int FOLLOWERS_POST_CODE = 100;
    public static boolean pageRefresh = false;
    private RecyclerView rvFollowersPost;
    private LinearLayoutManager layoutManager;
    private HomeAdapter adapter;
    //private ArrayList<SampleItem> arrayList = new ArrayList<>();
    private ArrayList<FeedsItem> arrayList = new ArrayList<>();
    private LinearLayout layoutNoData;

    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;

    /*pagination vars start*/
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    int page = 1;
    int total_records = 0;
    /*pagination vars end*/

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;

    /* timezone */
    private String timezone = "";
    private TimeZoneUtils timeZoneUtils = new TimeZoneUtils();
    /* ******** */

    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_followers_post, container, false);
        initViews(rootView);

        timezone = timeZoneUtils.getTimeZone();
        Log.e("FollowersPost", "TimeZone : " + timezone);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(getContext());

        /*if (isInternetAvailable) {
            getFollowersPost(true);
        } else {
            intent = new Intent(getContext(), NoConnectionActivity.class);
            startActivityForResult(intent, FOLLOWERS_POST_CODE);
        }*/

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);


                                        if (isInternetAvailable) {
                                            getFollowersPost(true);
                                        } else {
                                            intent = new Intent(getContext(), NoConnectionActivity.class);
                                            startActivityForResult(intent, FOLLOWERS_POST_CODE);
                                        }
                                    }
                                }
        );

        rvFollowersPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                getFollowersPost(false);
                            }
                        }
                    }
                }
            }
        });
        return rootView;
    }

    private void initViews(View rootView) {
        pageRefresh = false;
        swipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.swiper);
        userId = PrefsUtil.with(getContext()).readString("userId");
        layoutNoData = (LinearLayout) rootView.findViewById(R.id.layoutNoData);
        rvFollowersPost = (RecyclerView) rootView.findViewById(R.id.rvFollowersPost);
        layoutManager = new LinearLayoutManager(getContext());
        rvFollowersPost.setLayoutManager(layoutManager);
        adapter = new HomeAdapter(getActivity(), arrayList);
        rvFollowersPost.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (isInternetAvailable) {
            getFollowersPost(true);
        } else {
            intent = new Intent(getContext(), NoConnectionActivity.class);
            startActivityForResult(intent, FOLLOWERS_POST_CODE);
        }
    }

    private void getFollowersPost(boolean refreshFlag) {
        url = WebServiceUrl.baseUrl;

        swipeRefreshLayout.setRefreshing(true);

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userFollowPost");

        params.add("user_id");
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
                            rvFollowersPost.setVisibility(View.VISIBLE);
                            layoutNoData.setVisibility(View.GONE);
                            arrayList.addAll(resultObj.getData());
                            adapter.setFromFragment("FollowersPostFragment");
                            adapter.notifyDataSetChanged();
                            loading = true;
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            rvFollowersPost.setVisibility(View.GONE);
                            layoutNoData.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    rvFollowersPost.setVisibility(View.GONE);
                    layoutNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FOLLOWERS_POST_CODE) {
                getFollowersPost(true);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pageRefresh) {
            cd = new ConnectionCheck();
            isInternetAvailable = cd.isNetworkConnected(getContext());
            if (isInternetAvailable) {
                getFollowersPost(true);
            } else {
                intent = new Intent(getContext(), NoConnectionActivity.class);
                startActivityForResult(intent, FOLLOWERS_POST_CODE);
            }
        }
    }
}
