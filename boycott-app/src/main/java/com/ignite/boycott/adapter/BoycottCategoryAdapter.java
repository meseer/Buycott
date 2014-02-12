package com.ignite.boycott.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ignite.boycott.io.model.BoycottList;
import com.ignite.boycott.io.model.Category;
import com.ignite.boycott.io.model.Maker;

/**
 * Created by meseer on 12.02.14.
 * This class is not thread safe!
 */
public class BoycottCategoryAdapter extends BaseAdapter {
    private BoycottList list;
    private LayoutInflater mInflater;

    public BoycottCategoryAdapter(Context context, BoycottList list) {
        this.list = list;
        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (list == null) return 0;
        return list.getData().getCategories().length;
    }

    @Override
    public Category getItem(int position) {
        if (list == null) return null;
        return list.getCategory(position);
    }

    @Override
    public long getItemId(int position) {
        Category c = list.getCategory(position);
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i) == c) return i;
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            view = mInflater.inflate(android.R.layout.simple_list_item_1, null);
        } else {
            view = convertView;
        }

        Category rowItem = (Category) getItem(position);
        ((TextView) view.findViewById(android.R.id.text1)).setText(rowItem.getTitle());

        return view;
    }

    public BoycottList swapBoycottList(BoycottList newBoycottList) {
        if (newBoycottList == list)
            return null;

        BoycottList oldBoycottList = list;

        list = newBoycottList;
        if (newBoycottList != null) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }
        return oldBoycottList;
    }
}
