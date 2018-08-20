package com.application.wowwao1.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Adapters.FriendsAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.FriendsItem;
import com.application.wowwao1.Models.FriendsPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nct119 on 11/8/17.
 */

public class FriendsFragment extends Fragment {

    private static final int FRIENDS_CODE = 100;
    private RecyclerView rvFriends;
    private GridLayoutManager layoutManager;
    private FriendsAdapter adapter;
    private ArrayList<FriendsItem> arrayList = new ArrayList<>();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        initViews(rootView);
        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(getContext());
        if (isInternetAvailable) {
            friendsList(true);
        } else {
            intent = new Intent(getContext(), NoConnectionActivity.class);
            startActivityForResult(intent, FRIENDS_CODE);
        }

        rvFriends.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                friendsList(false);
                            }
                        }
                    }
                }
            }
        });
        return rootView;
    }

    private void initViews(View rootView) {
        userId = PrefsUtil.with(getContext()).readString("userId");
        layoutNoData = (LinearLayout) rootView.findViewById(R.id.layoutNoData);
        rvFriends = (RecyclerView) rootView.findViewById(R.id.rvFriends);
        layoutManager = new GridLayoutManager(getContext(), 2);
        rvFriends.setLayoutManager(layoutManager);
        adapter = new FriendsAdapter(getContext(), arrayList);
        rvFriends.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void friendsList(boolean refreshFlag) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userFriendList");

        params.add("user_id");
        values.add(userId);

        params.add("page");
        values.add(String.valueOf(page));

        if (refreshFlag) {
            arrayList.clear();
            adapter.notifyDataSetChanged();
            page = 1;
        }

        new ParseJSON(getContext(), url, params, values, FriendsPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        FriendsPojo resultObj = (FriendsPojo) obj;
                        total_records = resultObj.getTotalRecords();
                        if (total_records > 0) {
                            rvFriends.setVisibility(View.VISIBLE);
                            layoutNoData.setVisibility(View.GONE);
                            arrayList.addAll(resultObj.getData());
                            adapter.notifyDataSetChanged();
                            loading = true;
                        } else {
                            rvFriends.setVisibility(View.GONE);
                            layoutNoData.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    rvFriends.setVisibility(View.GONE);
                    layoutNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FRIENDS_CODE) {
                friendsList(true);
            }
        }
    }
}

