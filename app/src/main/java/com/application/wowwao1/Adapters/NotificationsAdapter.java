package com.application.wowwao1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.wowwao1.Activities.HomeActivity;
import com.application.wowwao1.Activities.UserProfileActivity;
import com.application.wowwao1.Fragments.FriendRequestFragment;
import com.application.wowwao1.Fragments.HomeFragment;
import com.application.wowwao1.Models.NotificationItem;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.View_Holder> {

    private ArrayList<NotificationItem> arrayList;
    private Context context;
    private Intent intent;

    public NotificationsAdapter(Context context, ArrayList<NotificationItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notifications, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        final NotificationItem items = arrayList.get(position);

        Picasso.with(context)
                .load(items.getUserData().getProfileImg())
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(holder.imgProfile);

        holder.txtUsername.setText(items.getUserData().getFirstName() + " " + items.getUserData().getLastName());
        //holder.txtTime.setText();
        holder.txtDescription.setText(items.getNotiMessage());

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("receiver_id", arrayList.get(position).getUserData().getId());
                context.startActivity(intent);
            }
        });

        holder.layoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.getNotiType().equalsIgnoreCase("request-received")) {
                    FriendRequestFragment.fromNotification = true;
                    FriendRequestFragment fragment = new FriendRequestFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", items.getUserData().getId());
                    fragment.setArguments(bundle);
                    ((HomeActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.containerView, fragment)
                            .addToBackStack(context.getString(R.string.nav_friend_requests))
                            .commit();
                } else if (items.getNotiType().equalsIgnoreCase("request-accepted")) {
                    intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("receiver_id", arrayList.get(position).getUserData().getId());
                    context.startActivity(intent);
                } else if (items.getNotiType().equalsIgnoreCase("post-liked")) {

                    Intent i = new Intent(((HomeActivity) context),HomeActivity.class);
                    ((HomeActivity) context).startActivity(i);

                } else if (items.getNotiType().equalsIgnoreCase("post-commented")) {

                    Intent i = new Intent(((HomeActivity) context),HomeActivity.class);
                    ((HomeActivity) context).startActivity(i);

                } else if (items.getNotiType().equalsIgnoreCase("user-followed")) {
                    intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("receiver_id", arrayList.get(position).getUserData().getId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private LinearLayout layoutReq, layoutNotification;
        private CircleImageView imgProfile;
        private TextView txtUsername, txtTime, txtDescription, txtAccept, txtReject;

        public View_Holder(View itemView) {
            super(itemView);
            layoutReq = (LinearLayout) itemView.findViewById(R.id.layoutReq);
            layoutNotification = (LinearLayout) itemView.findViewById(R.id.layoutNotification);

            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);

            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtAccept = (TextView) itemView.findViewById(R.id.txtAccept);
            txtReject = (TextView) itemView.findViewById(R.id.txtReject);
        }
    }
}
