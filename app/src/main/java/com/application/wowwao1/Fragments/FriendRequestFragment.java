package com.application.wowwao1.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.application.wowwao1.Activities.HomeActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Adapters.FriendRequestAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.FriendReqListPojo;
import com.application.wowwao1.Models.UserData;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nct119 on 11/8/17.
 */

public class FriendRequestFragment extends Fragment {

    private static final int FRIEND_REQ_CODE = 100;
    public static boolean fromNotification = false;
    private RecyclerView rvFriendReq;
    private GridLayoutManager layoutManager;
    private ArrayList<UserData> arrayList = new ArrayList<>();
    private FriendRequestAdapter adapter;
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
        View rootView = inflater.inflate(R.layout.fragment_friend_request, container, false);
        initViews(rootView);

        if (fromNotification) {
            Bundle bundle = getArguments();
            ((HomeActivity) getActivity()).setToolbarTitle(getString(R.string.nav_friend_requests));
            userId = bundle.getString("userId");
            Log.e("FriendRequestFragment", "userId : " + userId);
        }

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(getContext());
        if (isInternetAvailable) {
            friendRequestList(true);
        } else {
            intent = new Intent(getContext(), NoConnectionActivity.class);
            startActivityForResult(intent, FRIEND_REQ_CODE);
        }


        rvFriendReq.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                friendRequestList(false);
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
        rvFriendReq = (RecyclerView) rootView.findViewById(R.id.rvFriendReq);
        layoutManager = new GridLayoutManager(getContext(), 2);
        rvFriendReq.setLayoutManager(layoutManager);
        adapter = new FriendRequestAdapter(getContext(), arrayList);
        rvFriendReq.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void friendRequestList(boolean refreshFlag) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userFriendRequestList");

        params.add("user_id");
        values.add(userId);

        params.add("page");
        values.add(String.valueOf(page));

        if (refreshFlag) {
            arrayList.clear();
            adapter.notifyDataSetChanged();
            page = 1;
        }

        new ParseJSON(getContext(), url, params, values, FriendReqListPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        FriendReqListPojo resultObj = (FriendReqListPojo) obj;
                        total_records = resultObj.getTotalRecords();
                        if (total_records > 0) {
                            rvFriendReq.setVisibility(View.VISIBLE);
                            layoutNoData.setVisibility(View.GONE);
                            arrayList.addAll(resultObj.getUserData());
                            adapter.setTotalRecords(total_records);
                            adapter.notifyDataSetChanged();
                            loading = true;
                        } else {
                            rvFriendReq.setVisibility(View.GONE);
                            layoutNoData.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    rvFriendReq.setVisibility(View.GONE);
                    layoutNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FRIEND_REQ_CODE) {
                friendRequestList(true);
            }
        }
    }


}
