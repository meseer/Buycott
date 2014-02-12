package com.ignite.boycott.adapter;

import android.app.Activity;
import android.content.Context;
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
 */
public class BoycottListAdapter extends BaseAdapter {
    private BoycottList list;
    private LayoutInflater mInflater;

    public BoycottListAdapter(Context context, BoycottList list) {
        this.list = list;
        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (list == null) return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) return null;
        return list.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            view = mInflater.inflate(android.R.layout.simple_list_item_2, null);
        } else {
            view = convertView;
        }

        Maker rowItem = (Maker) getItem(position);
        ((TextView) view.findViewById(android.R.id.text2)).setText(rowItem.getOwner());
        ((TextView) view.findViewById(android.R.id.text1)).setText(rowItem.getBrand());

        return view;
    }

    public BoycottList swapBoycotList(BoycottList newBoycottList) {
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
