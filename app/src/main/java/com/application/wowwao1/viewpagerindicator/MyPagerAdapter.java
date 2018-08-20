package com.application.wowwao1.viewpagerindicator;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.application.wowwao1.Models.FeedsImageItem;
import com.application.wowwao1.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by nct119 on 31/1/18.
 */

public class MyPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<FeedsImageItem> arrayList;
    private boolean doNotifyDataSetChangedOnce = false;

    public MyPagerAdapter(Context context, ArrayList<FeedsImageItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {

        if (doNotifyDataSetChangedOnce) {
            doNotifyDataSetChangedOnce = false;
            notifyDataSetChanged();
        }
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        doNotifyDataSetChangedOnce = true;
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        doNotifyDataSetChangedOnce = true;
        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.adapter_pager_image, container, false);
        ImageView imgPager = (ImageView) itemView.findViewById(R.id.imgPager);
        final ProgressBar prgImg = (ProgressBar) itemView.findViewById(R.id.prgImg);
        try {
            Picasso.with(context)
                    .load(arrayList.get(position).getPath())
                    .into(imgPager, new Callback() {
                        @Override
                        public void onSuccess() {
                            prgImg.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position);
    }
}
