package com.application.wowwao1.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Adapters.InfoAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.InfoItem;
import com.application.wowwao1.Models.InfoPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nct119 on 11/8/17.
 */

public class InfoFragment extends Fragment {

    private static final int INFO_CODE = 100;
    private RecyclerView rvInfo;
    private LinearLayoutManager layoutManager;
    private ArrayList<InfoItem> arrayList = new ArrayList<>();
    private InfoAdapter adapter;

    private String url;
    private ArrayList<String> params;
    private ArrayList<String> values;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        initViews(rootView);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(getContext());
        if (isInternetAvailable) {
            infoPagesList();
        } else {
            intent = new Intent(getContext(), NoConnectionActivity.class);
            startActivityForResult(intent, INFO_CODE);
        }

        return rootView;
    }

    private void infoPagesList() {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("getStaticPagesList");

        new ParseJSON(getContext(), url, params, values, InfoPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        InfoPojo resultObj = (InfoPojo) obj;
                        arrayList.clear();
                        arrayList.addAll(resultObj.getData());
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initViews(View rootView) {
        rvInfo = (RecyclerView) rootView.findViewById(R.id.rvInfo);
        layoutManager = new LinearLayoutManager(getContext());
        rvInfo.setLayoutManager(layoutManager);
        adapter = new InfoAdapter(getContext(), arrayList);
        rvInfo.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == INFO_CODE) {
                infoPagesList();
            }
        }
    }
}
