package com.application.wowwao1.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Activities.ConversationActivity;
import com.application.wowwao1.Activities.HomeActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Activities.UserProfileActivity;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.FriendsItem;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.View_Holder> {

    private static final int ADAPTER_FRIENDS_CODE = 1000;
    private ArrayList<FriendsItem> arrayList;
    private Context context;

    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;

    public FriendsAdapter(Context context, ArrayList<FriendsItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        userId = PrefsUtil.with(context).readString("userId");
        cd = new ConnectionCheck();
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_friends, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        /*logic for hide right side border view when record is odd*/
        int result = (position + 1) % 2;
        if (result == 0) {
            holder.layoutRightView.setVisibility(View.GONE);
        } else {
            holder.layoutRightView.setVisibility(View.VISIBLE);
        }

        final FriendsItem items = arrayList.get(position);

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

        holder.txtSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationActivity.newMsg = false;
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra("username", items.getFirstName() + " " + items.getLastName());
                intent.putExtra("receiverImg", items.getProfileImg());
                intent.putExtra("receiver_id", items.getId());
                context.startActivity(intent);
            }
        });

        holder.layoutUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage(R.string.unfriend_alert_msg);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        isInternetAvailable = cd.isNetworkConnected(context);
                        if (isInternetAvailable) {
                            unFriend(userId, arrayList.get(position).getId(), position);
                        } else {
                            intent = new Intent(context, NoConnectionActivity.class);
                            ((HomeActivity) context).startActivityForResult(intent, ADAPTER_FRIENDS_CODE);
                        }

                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
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

    public class View_Holder extends RecyclerView.ViewHolder {

        private LinearLayout layoutRightView, layoutUnFriend;
        private CircleImageView imgProfile;
        private TextView txtUsername, txtGender, txtAge, txtSendMsg;

        public View_Holder(View itemView) {
            super(itemView);
            layoutRightView = (LinearLayout) itemView.findViewById(R.id.layoutRightView);
            layoutUnFriend = (LinearLayout) itemView.findViewById(R.id.layoutUnFriend);

            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);

            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtGender = (TextView) itemView.findViewById(R.id.txtGender);
            txtAge = (TextView) itemView.findViewById(R.id.txtAge);
            txtSendMsg = (TextView) itemView.findViewById(R.id.txtSendMsg);
        }
    }

    /*UnFriend method*/
    private void unFriend(String senderId, String receiverId, final int position) {
        /*action:userRequest
        sender_id:13
        receiver_id:16
        request_action:Unfriend*/
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userRequest");

        params.add("request_action");
        values.add("Unfriend");

        params.add("sender_id");
        values.add(senderId);

        params.add("receiver_id");
        values.add(receiverId);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        arrayList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
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
