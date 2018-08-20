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

import com.application.wowwao1.Activities.CommentsDialogActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Activities.ReplyDialogActivity;
import com.application.wowwao1.Activities.UserProfileActivity;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommentLIstItem;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.View_Holder> {

    private static final int ADAPTER_COMMENTS_CODE = 1000;
    private ArrayList<CommentLIstItem> arrayList;
    private Context context;

    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;


    private String post_id, post_user_id;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;
    private int total_records;

    public CommentsAdapter(Context context, ArrayList<CommentLIstItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        userId = PrefsUtil.with(context).readString("userId");
        cd = new ConnectionCheck();
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comments, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        final CommentLIstItem items = arrayList.get(position);

        Picasso.with(context)
                .load(items.getUserData().getProfileImg())
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(holder.imgProfile);

        holder.txtUsername.setText(items.getUserData().getFirstName() + " " + items.getUserData().getLastName());
        holder.txtDate.setText(items.getCmntDate() + " | " + items.getCmntTime());
        holder.txtDescription.setText(items.getComment());
        holder.txtReplay.setText("Reply (" + items.getTotalReply() + ")");

        if (arrayList.get(position).getReply().size() > 0) {
            holder.layoutReply.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(items.getReply().get(0).getUserData().getProfileImg())
                    .error(R.drawable.no_user)
                    .placeholder(R.drawable.no_user)
                    .into(holder.imgProfile1);

            holder.txtUsername1.setText(items.getReply().get(0).getUserData().getFirstName() + " " + items.getReply().get(0).getUserData().getLastName());
            holder.txtDate1.setText(items.getReply().get(0).getReplyDate() + " | " + items.getReply().get(0).getReplyTime());
            holder.txtDescription1.setText(items.getReply().get(0).getReply());
        } else {
            holder.layoutReply.setVisibility(View.GONE);
        }

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage(R.string.delete_comment_alert_msg);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        isInternetAvailable = cd.isNetworkConnected(context);
                        if (isInternetAvailable) {
                            deleteComment(arrayList.get(position).getId(), position);
                        } else {
                            intent = new Intent(context, NoConnectionActivity.class);
                            ((CommentsDialogActivity) context).startActivityForResult(intent, ADAPTER_COMMENTS_CODE);
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

        holder.imgDelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage(R.string.delete_reply_alert_msg);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        isInternetAvailable = cd.isNetworkConnected(context);
                        if (isInternetAvailable) {
                            deleteReplyComment(arrayList.get(position).getReply().get(0).getId(), holder, items.getTotalReply());
                        } else {
                            intent = new Intent(context, NoConnectionActivity.class);
                            ((CommentsDialogActivity) context).startActivityForResult(intent, ADAPTER_COMMENTS_CODE);
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

        holder.txtReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(context);
                if (isInternetAvailable) {
                    Intent intent = new Intent(context, ReplyDialogActivity.class);
                    intent.putExtra("post_id", post_id);
                    intent.putExtra("post_user_id", post_user_id);
                    intent.putExtra("cmnt_id", arrayList.get(position).getId());
                    intent.putExtra("cmnt_user_id", arrayList.get(position).getUserData().getId());
                    context.startActivity(intent);
                } else {
                    intent = new Intent(context, NoConnectionActivity.class);
                    ((CommentsDialogActivity) context).startActivityForResult(intent, ADAPTER_COMMENTS_CODE);
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

        holder.imgProfile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("receiver_id", arrayList.get(position).getReply().get(0).getUserData().getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setPostId(String post_id, String post_user_id) {
        this.post_id = post_id;
        this.post_user_id = post_user_id;
    }

    public void totalRecordCounter(int total_records) {
        this.total_records = total_records;
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private LinearLayout layoutReply;
        private CircleImageView imgProfile, imgProfile1;
        private TextView txtUsername, txtDate, txtDescription, txtReplay, txtUsername1, txtDate1, txtDescription1;
        private ImageView imgDelete, imgDelete1;

        public View_Holder(View itemView) {
            super(itemView);
            layoutReply = (LinearLayout) itemView.findViewById(R.id.layoutReply);
            imgProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);
            imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);

            txtUsername = (TextView) itemView.findViewById(R.id.txtUsername);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtReplay = (TextView) itemView.findViewById(R.id.txtReplay);

            imgProfile1 = (CircleImageView) itemView.findViewById(R.id.imgProfile1);
            imgDelete1 = (ImageView) itemView.findViewById(R.id.imgDelete1);

            txtUsername1 = (TextView) itemView.findViewById(R.id.txtUsername1);
            txtDate1 = (TextView) itemView.findViewById(R.id.txtDate1);
            txtDescription1 = (TextView) itemView.findViewById(R.id.txtDescription1);
        }
    }

    private void deleteComment(String cmnt_id, final int position) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("deleteComment");

        params.add("user_id");
        values.add(userId);

        params.add("cmnt_id");
        values.add(cmnt_id);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        arrayList.remove(position);
                        notifyDataSetChanged();
                        if (total_records > 0) {
                            total_records -= 1;
                            ((CommentsDialogActivity) context).totalRecordsCounter(total_records);
                        }
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

    private void deleteReplyComment(final String reply_id, final View_Holder holder, final String totalReply) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("deleteReply");

        params.add("user_id");
        values.add(userId);

        params.add("reply_id");
        values.add(reply_id);

        new ParseJSON(context, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(context, resultObj.getMessage(), Toast.LENGTH_SHORT).show();

                        int result = Integer.parseInt(totalReply);
                        if (result > 0) {
                            result -= 1;
                            holder.txtReplay.setText(context.getString(R.string.reply_title) + " (" + result + ")");
                        }
                        holder.layoutReply.setVisibility(View.GONE);
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
