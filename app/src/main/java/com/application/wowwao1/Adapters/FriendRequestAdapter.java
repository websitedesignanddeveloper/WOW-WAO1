package com.application.wowwao1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Activities.HomeActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Activities.UserProfileActivity;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.UserData;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.View_Holder> {

    private static final int ADAPTER_FRIEND_REQ_CODE = 1000;
    private ArrayList<UserData> arrayList;
    private Context context;

    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private Intent intent;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private int total_records = 0;

    public FriendRequestAdapter(Context context, ArrayList<UserData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

        userId = PrefsUtil.with(context).readString("userId");
        cd = new ConnectionCheck();
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friend_request, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        /*logic for hide right side border view when record is odd*/
        final int result = (position + 1) % 2;
        if (result == 0) {
            holder.layoutRightView.setVisibility(View.GONE);
        } else {
            holder.layoutRightView.setVisibility(View.VISIBLE);
        }
        /* ******************************************************* */
        final UserData items = arrayList.get(position);
        Picasso.with(context)
                .load(items.getProfileImg())
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(holder.imgProfile);

        holder.txtUsername.setText(items.getFirstName() + " " + items.getLastName());

        if (!items.getGender().equalsIgnoreCase("")) {
            holder.txtGender.setText(items.getGender());
        } else {
            holder.txtGender.setText(R.string.not_available);
        }

        if (!items.getAge().equalsIgnoreCase("")) {
            holder.txtAge.setText(items.getAge());
        } else {
            holder.txtAge.setText(R.string.not_available);
        }

        holder.txtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(context);
                if (isInternetAvailable) {
                    requestAccept(userId, items.getId(), position);
                } else {
                    intent = new Intent(context, NoConnectionActivity.class);
                    ((HomeActivity) context).startActivityForResult(intent, ADAPTER_FRIEND_REQ_CODE);
                }
            }
        });

        holder.txtReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(context);
                if (isInternetAvailable) {
                    requestReject(userId, items.getId(), position);
                } else {
                    intent = new Intent(context, NoConnectionActivity.class);
                    ((HomeActivity) context).startActivityForResult(intent, ADAPTER_FRIEND_REQ_CODE);
                }
            }
        });

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("receiver_id", arrayList.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("receiver_id", arrayList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setTotalRecords(int total_records) {
        this.total_records = total_records;
    }


    public class View_Holder extends RecyclerView.ViewHolder {

        private LinearLayout layoutRightView;
        private CircleImageView imgProfile;
        private TextView txtUsername, txtGender, txtAge, txtAccept, txtReject;

        public View_Holder(View itemView) {
            super(itemView);
            layoutRightView = (LinearLayout) itemView.findViewById(R.id.layoutRightView);

            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);

            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtGender = (TextView) itemView.findViewById(R.id.txtGender);
            txtAge = (TextView) itemView.findViewById(R.id.txtAge);
            txtAccept = (TextView) itemView.findViewById(R.id.txtAccept);
            txtReject = (TextView) itemView.findViewById(R.id.txtReject);
        }
    }

    private void requestAccept(String userId, String receiver_id, final int position) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userRequest");

        params.add("request_action");
        values.add("Accept");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        if (total_records > 0) {
                            total_records -= 1;
                            PrefsUtil.with(context).write("count_request", "" + total_records);
                        }
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        arrayList.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestReject(String userId, String receiver_id, final int position) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userRequest");

        params.add("request_action");
        values.add("Reject");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        if (total_records > 0) {
                            total_records -= 1;
                            PrefsUtil.with(context).write("count_request", "" + total_records);
                        }
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        arrayList.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
