package com.application.wowwao1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Activities.ReplyDialogActivity;
import com.application.wowwao1.Activities.UserProfileActivity;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.ReplyCommentListItem;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.View_Holder> {

    private static final int ADAPTER_REPLY_CODE = 1000;
    private ArrayList<ReplyCommentListItem> arrayList;
    private Context context;

    private ArrayList<String> params;
    private ArrayList<String> values;
    private String url;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;
    private int total_records;

    public RepliesAdapter(Context context, ArrayList<ReplyCommentListItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        cd = new ConnectionCheck();
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_replies, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        ReplyCommentListItem items = arrayList.get(position);

        Picasso.with(context)
                .load(items.getUserData().getProfileImg())
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(holder.imgProfile);

        holder.txtUsername.setText(items.getUserData().getFirstName() + " " + items.getUserData().getLastName());
        holder.txtDate.setText(items.getReplyDate() + " | " + items.getReplyTime());
        holder.txtDescription.setText(items.getReply());

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(context);
                if (isInternetAvailable) {
                    deleteReply(arrayList.get(position).getId(), position);
                } else {
                    intent = new Intent(context, NoConnectionActivity.class);
                    ((ReplyDialogActivity)context).startActivityForResult(intent, ADAPTER_REPLY_CODE);
                }

            }
        });

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("receiver_id", arrayList.get(position).getUserData().getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void totalRecordCounter(int total_records) {
        this.total_records = total_records;
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private CircleImageView imgProfile;
        private TextView txtUsername, txtDate, txtDescription;
        private ImageView imgDelete;

        public View_Holder(View itemView) {
            super(itemView);
            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);
            imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);

            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
        }
    }

    private void deleteReply(String reply_id, final int position) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("deleteReply");

        params.add("reply_id");
        values.add(reply_id);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        if (total_records > 0) {
                            total_records -= 1;
                            ((ReplyDialogActivity) context).totalRecordsCounter(total_records);
                        }
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
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
