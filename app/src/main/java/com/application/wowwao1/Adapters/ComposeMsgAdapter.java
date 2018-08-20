package com.application.wowwao1.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.wowwao1.Activities.ConversationActivity;
import com.application.wowwao1.Models.UserData;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ComposeMsgAdapter extends RecyclerView.Adapter<ComposeMsgAdapter.View_Holder> {

    private ArrayList<UserData> arrayList;
    private Context context;

    public ComposeMsgAdapter(Context context, ArrayList<UserData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_compose_msg, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        Picasso.with(context)
                .load(arrayList.get(position).getProfileImg())
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(holder.imgProfile);
        holder.txtUsername.setText(Html.fromHtml(arrayList.get(position).getFirstName() + " " + arrayList.get(position).getLastName()));

        holder.layoutMainSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(Html.fromHtml(arrayList.get(position).getFirstName() + " " + arrayList.get(position).getLastName()));
                String receiver_id = arrayList.get(position).getId();
                String profile_img = arrayList.get(position).getProfileImg();
                ((ConversationActivity) context).setUserDataInToolbar(receiver_id, username, profile_img);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private LinearLayout layoutMainSuggestion;
        private CircleImageView imgProfile;
        private TextView txtUsername;

        public View_Holder(View itemView) {
            super(itemView);
            layoutMainSuggestion = (LinearLayout) itemView.findViewById(R.id.layoutMainSuggestion);
            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);
            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
        }
    }
}
