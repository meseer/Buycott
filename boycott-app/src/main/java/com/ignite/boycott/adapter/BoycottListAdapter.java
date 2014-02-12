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
import com.ignite.boycott.io.model.Maker;

/**
 * Created by meseer on 12.02.14.
 * This class is not thread safe!
 */
public class BoycottListAdapter extends BaseAdapter {
    private BoycottList list;
    private LayoutInflater mInflater;
    private String mFilter;
    private BoycottList mFilteredList;

    public BoycottListAdapter(Context context, BoycottList list) {
        this.list = list;
        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mFilter = null;
        mFilteredList = list;
    }

    @Override
    public int getCount() {
        BoycottList filteredList = getFilteredList();
        if (filteredList == null) return 0;
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        //use map position => item here
        BoycottList filteredList = getFilteredList();
        if (filteredList == null) return null;
        return filteredList.getMaker(position);
    }

    @Override
    public long getItemId(int position) {
        Maker m = getFilteredList().getMaker(position);
        return list.getPosition(m);
    }

    private BoycottList getFilteredList() {
        if (list != null && mFilteredList == null) {
            if (TextUtils.isEmpty(mFilter)) {
                mFilteredList = list;
            } else {
                mFilteredList = list.filterMakers(new MakerContainsPredicate(mFilter));
            }
        }
        return mFilteredList;
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

    public void setFilter(String s) {
        if (!TextUtils.equals(s, mFilter)) {
            mFilter = s;
            mFilteredList = null;
            notifyDataSetChanged();
        }
    }
}
