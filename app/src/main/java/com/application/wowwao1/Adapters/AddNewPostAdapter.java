package com.application.wowwao1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.wowwao1.Activities.AddNewPostActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.FeedsImageItem;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddNewPostAdapter extends RecyclerView.Adapter<AddNewPostAdapter.View_Holder> {

    private static final int DELETE_IMAGE_CODE = 1000;
    private ArrayList<FeedsImageItem> arrayList;
    private Context context;

    private String url;
    private ArrayList<String> params;
    private ArrayList<String> values;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;

    public AddNewPostAdapter(Context context, ArrayList<FeedsImageItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

        cd = new ConnectionCheck();
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_add_new_post, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        Picasso.with(context)
                .load(arrayList.get(position).getPath())
                .fit()
                .error(R.drawable.progress_animation)
                .placeholder(R.drawable.progress_animation)
                .into(holder.imgPost);

        holder.layoutDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String img_id = arrayList.get(position).getId();

                isInternetAvailable = cd.isNetworkConnected(context);
                if (isInternetAvailable) {
                    if (!img_id.equals("")) {
                        Log.e("AddNewPostAdapter", "img_id : " + arrayList.get(position).getId());
                        deletePostImage(img_id, position);
                    } else {
                        arrayList.remove(position);
                        notifyDataSetChanged();
                    }
                } else {
                    intent = new Intent(context, NoConnectionActivity.class);
                    ((AddNewPostActivity)context).startActivityForResult(intent, DELETE_IMAGE_CODE);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private LinearLayout layoutDeleteImage;
        private ImageView imgPost;

        public View_Holder(View itemView) {
            super(itemView);
            layoutDeleteImage = (LinearLayout) itemView.findViewById(R.id.layoutDeleteImage);

            imgPost = (ImageView) itemView.findViewById(R.id.imgPost);
        }
    }

    private void deletePostImage(String img_id, final int position) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userDeletePostImage");

        params.add("img_id");
        values.add(img_id);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        arrayList.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
