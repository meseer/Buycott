package com.ignite.boycott.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ignite.boycott.R;
import com.ignite.boycott.io.model.BoycottList;
import com.ignite.boycott.io.model.Category;

/**
 * Created by meseer on 12.02.14.
 * This class is not thread safe!
 */
public class BoycottListAdapter extends BaseAdapter {
    private final Context context;
    private BoycottList list;
    private LayoutInflater mInflater;
    private int itemId;

    public BoycottListAdapter(Context context, BoycottList list) {
        this.list = list;
        this.itemId = R.layout.category_item;
        this.context = context;
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
            view = mInflater.inflate(itemId, null);
        } else {
            view = convertView;
        }

        Category rowItem = (Category) getItem(position);
        ((TextView) view.findViewById(R.id.text)).setText(rowItem.getTitle());
        ((TextView) view.findViewById(R.id.text1)).setText(context.getString(R.string.total_brands, rowItem.size()));
        ((ImageView) view.findViewById(R.id.image)).setImageResource(android.R.drawable.ic_menu_compass);

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
