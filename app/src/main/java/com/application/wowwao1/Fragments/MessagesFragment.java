package com.application.wowwao1.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.application.wowwao1.Activities.ConversationActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Adapters.MessagesAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.MsgListItem;
import com.application.wowwao1.Models.MsgListPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.Utils.TimeZoneUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nct119 on 11/8/17.
 */

public class MessagesFragment extends Fragment {

    private static final int MESSAGES_CODE = 100;
    public static boolean refreshInResume = false;
    private RecyclerView rvMessages;
    private LinearLayoutManager layoutManager;
    private ArrayList<MsgListItem> arrayList = new ArrayList<>();
    private MessagesAdapter adapter;
    private FloatingActionButton fabComposeMsg;
    private Intent intent;
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

    /* timezone */
    private String timezone = "";
    private TimeZoneUtils timeZoneUtils = new TimeZoneUtils();
    /* ******** */

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        initViews(rootView);

        timezone = timeZoneUtils.getTimeZone();
        Log.e("Messages", "TimeZone : " + timezone);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(getContext());
        if (isInternetAvailable) {
            messageList(true);
        } else {
            intent = new Intent(getContext(), NoConnectionActivity.class);
            startActivityForResult(intent, MESSAGES_CODE);
        }


        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                messageList(false);
                            }
                        }
                    }
                }
            }
        });

        fabComposeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationActivity.newMsg = true;
                intent = new Intent(getContext(), ConversationActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void initViews(View rootView) {
        userId = PrefsUtil.with(getContext()).readString("userId");
        fabComposeMsg = (FloatingActionButton) rootView.findViewById(R.id.fabComposeMsg);
        layoutNoData = (LinearLayout) rootView.findViewById(R.id.layoutNoData);

        rvMessages = (RecyclerView) rootView.findViewById(R.id.rvMessages);
        layoutManager = new LinearLayoutManager(getContext());
        rvMessages.setLayoutManager(layoutManager);
        adapter = new MessagesAdapter(getContext(), arrayList);
        rvMessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void messageList(boolean refreshFlag) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userConversation");

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

        new ParseJSON(getContext(), url, params, values, MsgListPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        MsgListPojo resultObj = (MsgListPojo) obj;
                        total_records = resultObj.getTotalRecords();
                        if (total_records > 0) {
                            rvMessages.setVisibility(View.VISIBLE);
                            layoutNoData.setVisibility(View.GONE);
                            arrayList.addAll(resultObj.getData());
                            adapter.notifyDataSetChanged();
                            loading = true;
                        } else {
                            rvMessages.setVisibility(View.GONE);
                            layoutNoData.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    rvMessages.setVisibility(View.GONE);
                    layoutNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshInResume) {
            messageList(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MESSAGES_CODE) {
                messageList(true);
            }
        }
    }
}

