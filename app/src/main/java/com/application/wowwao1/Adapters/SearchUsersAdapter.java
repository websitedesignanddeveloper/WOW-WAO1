package com.application.wowwao1.Adapters;

import android.annotation.SuppressLint;
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

import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Activities.SearchActivity;
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

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.View_Holder> {

    private static final int ADAPTER_SEARCH_CODE = 1000;
    private ArrayList<UserData> arrayList;
    private Context context;
    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;

    public SearchUsersAdapter(Context context, ArrayList<UserData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

        userId = PrefsUtil.with(context).readString("userId");
        cd = new ConnectionCheck();
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search_users, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        final UserData items = arrayList.get(position);

        Picasso.with(context)
                .load(items.getProfileImg())
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(holder.imgProfile);

        holder.txtUsername.setText(items.getFirstName() + " " + items.getLastName());
        String followStatus = items.getIs_follow();

        if (followStatus.equalsIgnoreCase("y")) {
            holder.txtFollowStatus.setText("Unfollow");
            holder.txtFollowStatus.setTextColor(R.color.colorPrimary);
        } else {
            holder.txtFollowStatus.setText("Follow");
            holder.txtFollowStatus.setTextColor(R.color.descriptionColor);
        }

        String is_friend = items.getIs_friend();

        if (is_friend.equalsIgnoreCase("n")) {
            holder.layoutAdd.setVisibility(View.VISIBLE);
            holder.imgAdd.setImageResource(R.drawable.add_friend);
            holder.layoutAdd.setBackground(context.getResources().getDrawable(R.drawable.navigation_bottom_icon));
        } else if (is_friend.equalsIgnoreCase("s")) {
            holder.layoutAdd.setVisibility(View.VISIBLE);
            holder.imgAdd.setImageResource(R.drawable.request_sent);
            holder.layoutAdd.setBackground(context.getResources().getDrawable(R.drawable.navigation_bottom_icon));
        } else if (is_friend.equalsIgnoreCase("y")) {
            holder.layoutAdd.setVisibility(View.VISIBLE);
            holder.imgAdd.setImageResource(R.drawable.remove_friend);
            holder.layoutAdd.setBackground(context.getResources().getDrawable(R.drawable.navigation_red_bottom_icon));
        } else if (is_friend.equalsIgnoreCase("r")) {
            holder.layoutAdd.setVisibility(View.VISIBLE);
            holder.imgAdd.setImageResource(R.drawable.request_received);
            holder.layoutAdd.setBackground(context.getResources().getDrawable(R.drawable.navigation_bottom_icon));
        }

        holder.txtFollowStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(context);
                if (isInternetAvailable) {
                    if (items.getIs_follow().equals("y")) {
                        followOperation(items.getId(), "Unfollow", position);
                    } else {
                        followOperation(items.getId(), "Follow", position);
                    }
                } else {
                    intent = new Intent(context, NoConnectionActivity.class);
                    ((SearchActivity) context).startActivityForResult(intent, ADAPTER_SEARCH_CODE);
                }
            }
        });

        holder.layoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(context);
                if (isInternetAvailable) {
                    if (items.getIs_friend().equals("n")) {
                        friendReqOperation(items.getId(), "Add", position, holder);
                    } else if (items.getIs_friend().equals("y")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(false);
                        builder.setMessage(R.string.unfriend_alert_msg);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", then he is allowed to exit from application
                                dialog.cancel();
                                friendReqOperation(items.getId(), "Unfriend", position, holder);
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
                } else {
                    intent = new Intent(context, NoConnectionActivity.class);
                    ((SearchActivity) context).startActivityForResult(intent, ADAPTER_SEARCH_CODE);
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

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private CircleImageView imgProfile;
        private ImageView imgAdd;
        private TextView txtUsername, txtFollowStatus;
        private LinearLayout layoutAdd;

        public View_Holder(View itemView) {
            super(itemView);
            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);
            imgAdd = (ImageView) itemView.findViewById(R.id.imgAdd);

            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtFollowStatus = (TextView) itemView.findViewById(R.id.txtFollowStatus);

            layoutAdd = (LinearLayout) itemView.findViewById(R.id.layoutAdd);
        }
    }

    private void followOperation(String receiver_id, final String follow_action, final int position) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userFollow");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        params.add("follow_action");
        values.add(follow_action);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        if (follow_action.equalsIgnoreCase("follow")) {
                            arrayList.get(position).setIs_follow("y");
                            notifyDataSetChanged();
                        } else {
                            arrayList.get(position).setIs_follow("n");
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    private void friendReqOperation(String receiver_id, final String request_action, final int position, final View_Holder holder) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userRequest");

        params.add("sender_id");
        values.add(userId);

        params.add("receiver_id");
        values.add(receiver_id);

        params.add("request_action");
        values.add(request_action);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        if (request_action.equalsIgnoreCase("add")) {
                            arrayList.get(position).setIs_friend("s");
                            arrayList.get(position).setIs_follow("y");
                            holder.layoutAdd.setBackground(context.getResources().getDrawable(R.drawable.navigation_bottom_icon));
                            notifyDataSetChanged();
                        } else if (request_action.equalsIgnoreCase("Unfriend")) {
                            arrayList.get(position).setIs_friend("n");
                            holder.layoutAdd.setBackground(context.getResources().getDrawable(R.drawable.navigation_bottom_icon));
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }
}
