package com.application.wowwao1.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.wowwao1.AsyncTask.DownloadFileFromURL;
import com.application.wowwao1.Models.ConversationItem;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.CircleImageView;
import com.application.wowwao1.Utils.PrefsUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.View_Holder> {

    private ArrayList<ConversationItem> arrayList;
    private Context context;
    private String senderImg, receiverImg, userId;
    private File file;

    public ConversationAdapter(Context context, ArrayList<ConversationItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        userId = PrefsUtil.with(context).readString("userId");
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversation, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        final ConversationItem items = arrayList.get(position);

        /* find extension code */
        String someFilepath, extension = "";
        if (!arrayList.get(position).getAttachment().equalsIgnoreCase("")) {
            someFilepath = arrayList.get(position).getAttachment().replaceAll(" ", "%20");
            if (!someFilepath.equalsIgnoreCase("")) {
                extension = someFilepath.substring(someFilepath.lastIndexOf("."));
            }
        }
        /* ******************* */

        if (userId.equals(items.getSenderId())) {

            holder.layoutSender.setVisibility(View.VISIBLE);
            holder.layoutReceiver.setVisibility(View.GONE);

            Picasso.with(context)
                    .load(senderImg)
                    .error(R.drawable.no_user)
                    .placeholder(R.drawable.no_user)
                    .into(holder.imgSenderProfile);

            if (items.getAttachment().equals("")) {
                holder.txtSenderMsg.setVisibility(View.VISIBLE);
                holder.imgSenderPost.setVisibility(View.GONE);
                holder.txtSenderMsg.setText(items.getMessage());
            } else {
                holder.txtSenderMsg.setVisibility(View.GONE);
                holder.imgSenderPost.setVisibility(View.VISIBLE);

                if (extension.equals(".jpg") || extension.equals(".png") || extension.equals(".jpeg")) {
                    Picasso.with(context)
                            .load(items.getAttachment())
                            .error(R.drawable.progress_animation)
                            .placeholder(R.drawable.progress_animation)
                            .into(holder.imgSenderPost);
                } else {
                    holder.imgSenderPost.setImageResource(R.drawable.ic_document);
                }
            }
            holder.txtSenderTime.setText(items.getMsgTime());
        } else {
            holder.layoutSender.setVisibility(View.GONE);
            holder.layoutReceiver.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(receiverImg)
                    .error(R.drawable.no_user)
                    .placeholder(R.drawable.no_user)
                    .into(holder.imgReceiverProfile);

            if (items.getAttachment().equals("")) {
                holder.txtReceiverMsg.setVisibility(View.VISIBLE);
                holder.imgReceiverPost.setVisibility(View.GONE);
                holder.txtReceiverMsg.setText(items.getMessage());
            } else {
                holder.txtReceiverMsg.setVisibility(View.GONE);
                holder.imgReceiverPost.setVisibility(View.VISIBLE);

                if (extension.equals(".jpg") || extension.equals(".png") || extension.equals(".jpeg")) {
                    Picasso.with(context)
                            .load(items.getAttachment())
                            .error(R.drawable.progress_animation)
                            .placeholder(R.drawable.progress_animation)
                            .into(holder.imgReceiverPost);
                } else {
                    holder.imgReceiverPost.setImageResource(R.drawable.ic_document);
                }
            }
            holder.txtReceiverTime.setText(items.getMsgTime());
        }

        holder.imgSenderPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL(context);
                downloadFileFromURL.execute(items.getAttachment(), URLUtil.guessFileName(items.getAttachment(), null, null), "Document");
            }
        });

        holder.imgReceiverPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL(context);
                downloadFileFromURL.execute(items.getAttachment(), URLUtil.guessFileName(items.getAttachment(), null, null), "Document");
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private LinearLayout layoutReceiver, layoutSender;
        private CircleImageView imgReceiverProfile, imgSenderProfile;
        private ImageView imgReceiverPost, imgSenderPost;
        private TextView txtReceiverMsg, txtSenderMsg, txtReceiverTime, txtSenderTime;

        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/frieght_sans.otf");

        public View_Holder(View itemView) {
            super(itemView);
            layoutReceiver = (LinearLayout) itemView.findViewById(R.id.layoutReceiver);
            layoutSender = (LinearLayout) itemView.findViewById(R.id.layoutSender);

            imgReceiverProfile = (CircleImageView) itemView.findViewById(R.id.imgReceiverProfile);
            imgSenderProfile = (CircleImageView) itemView.findViewById(R.id.imgSenderProfile);

            imgReceiverPost = (ImageView) itemView.findViewById(R.id.imgReceiverPost);
            imgSenderPost = (ImageView) itemView.findViewById(R.id.imgSenderPost);

            txtReceiverMsg = (TextView) itemView.findViewById(R.id.txtReceiverMsg);
            txtSenderMsg = (TextView) itemView.findViewById(R.id.txtSenderMsg);
            txtReceiverTime = (TextView) itemView.findViewById(R.id.txtReceiverTime);
            txtSenderTime = (TextView) itemView.findViewById(R.id.txtSenderTime);

            txtReceiverMsg.setTypeface(typeface);
            txtSenderMsg.setTypeface(typeface);
            txtReceiverTime.setTypeface(typeface);
            txtSenderTime.setTypeface(typeface);
        }
    }

    public void setImages(String senderImg, String receiverImg) {
        this.senderImg = senderImg;
        this.receiverImg = receiverImg;
    }
}
