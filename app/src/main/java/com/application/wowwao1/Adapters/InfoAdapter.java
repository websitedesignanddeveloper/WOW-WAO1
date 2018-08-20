package com.application.wowwao1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.wowwao1.Activities.InfoDetailsActivity;
import com.application.wowwao1.Models.InfoItem;
import com.application.wowwao1.R;

import java.util.ArrayList;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.View_Holder> {

    private ArrayList<InfoItem> arrayList;
    private Context context;

    public InfoAdapter(Context context, ArrayList<InfoItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_info, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        holder.txtName.setText(arrayList.get(position).getName());

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InfoDetailsActivity.class);
                intent.putExtra("st_id", arrayList.get(position).getId());
                intent.putExtra("name", arrayList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private TextView txtName;

        public View_Holder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
        }
    }
}
