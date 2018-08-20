package com.application.wowwao1.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Activities.ConversationActivity;
import com.application.wowwao1.Activities.HomeActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.MsgListItem;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.View_Holder> {

    private static final int ADAPTER_MESSAGE_CODE = 1000;
    private ArrayList<MsgListItem> arrayList;
    private Context context;

    private ArrayList<String> params;
    private ArrayList<String> values;
    private String url, userId;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;

    int total_unread_msg = 0;

    public MessagesAdapter(Context context, ArrayList<MsgListItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        userId = PrefsUtil.with(context).readString("userId");
        cd = new ConnectionCheck();
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_messages, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        final MsgListItem items = arrayList.get(position);

        Picasso.with(context)
                .load(items.getUserData().getProfileImg())
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(holder.imgProfile);

        holder.txtUsername.setText(items.getUserData().getFirstName() + " " + items.getUserData().getLastName());
        holder.txtDescription.setText(items.getMessage());
        holder.txtTime.setText(items.getMsgDate() + " | " + items.getMsgTime());
        holder.txtUnread.setText(items.getReadCount());

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage(R.string.delete_msg_alert_msg);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        isInternetAvailable = cd.isNetworkConnected(context);
                        if (isInternetAvailable) {
                            deleteMsg(userId, arrayList.get(position).getUserData().getId(), position);
                        } else {
                            intent = new Intent(context, NoConnectionActivity.class);
                            ((HomeActivity) context).startActivityForResult(intent, ADAPTER_MESSAGE_CODE);
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

        holder.layoutMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int total_unread = Integer.parseInt(PrefsUtil.with(context).readString("count_message"));
                if (total_unread > 0) {
                    total_unread -= Integer.parseInt(arrayList.get(position).getReadCount());
                    PrefsUtil.with(context).write("count_message", "" + total_unread);
                }

                ConversationActivity.newMsg = false;
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra("username", items.getUserData().getFirstName() + " " + items.getUserData().getLastName());
                intent.putExtra("receiverImg", items.getUserData().getProfileImg());
                intent.putExtra("receiver_id", items.getUserData().getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private CircleImageView imgProfile;
        private ImageView imgDelete;
        private TextView txtUsername, txtUnread, txtTime, txtDescription;
        private LinearLayout layoutMsg;

        public View_Holder(View itemView) {
            super(itemView);
            layoutMsg = (LinearLayout) itemView.findViewById(R.id.layoutMsg);
            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);
            imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);

            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtUnread = (TextView) itemView.findViewById(R.id.txtUnread);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
        }
    }

    private void deleteMsg(String sender_id, String receiver_id, final int position) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userMessageDelete");

        params.add("sender_id");
        values.add(sender_id);

        params.add("receiver_id");
        values.add(receiver_id);

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
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
