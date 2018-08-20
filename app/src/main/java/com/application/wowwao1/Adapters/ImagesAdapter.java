package com.application.wowwao1.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.application.wowwao1.Models.FeedsImageItem;
import com.application.wowwao1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.View_Holder> {

    private ArrayList<FeedsImageItem> arrayList;
    private Context context;

    public ImagesAdapter(Context context, ArrayList<FeedsImageItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_images, parent, false);
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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private ImageView imgPost;

        public View_Holder(View itemView) {
            super(itemView);
            imgPost = (ImageView) itemView.findViewById(R.id.imgPost);
        }
    }
}
