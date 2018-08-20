package com.application.wowwao1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.application.wowwao1.R;

import java.util.ArrayList;

/**
 * Created by WT-Kiran on 07-06-2018.
 */

public class LanguageAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> list = new ArrayList<>();
    LayoutInflater layoutInflater;

    public LanguageAdapter(Context context, ArrayList<String> list)
    {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = layoutInflater.inflate(R.layout.language_item,null);

        TextView tv = (TextView) v.findViewById(R.id.lang);

        tv.setText(list.get(i).toString());

        return v;
    }
}
