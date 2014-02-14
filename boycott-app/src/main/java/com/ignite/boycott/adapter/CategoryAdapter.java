package com.ignite.boycott.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ignite.boycott.R;
import com.ignite.boycott.io.model.Category;
import com.ignite.boycott.io.model.Maker;
import com.squareup.picasso.Picasso;

/**
 * Created by meseer on 12.02.14.
 * This class is not thread safe!
 */
public class CategoryAdapter extends BaseAdapter {
    private final Context context;
    private Category list;
    private LayoutInflater mInflater;
    private String mFilter;
    private Category mFilteredList;

    public CategoryAdapter(Context context, Category list) {
        this.list = list;
        this.context = context;
        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mFilter = null;
        mFilteredList = list;
    }

    @Override
    public int getCount() {
        Category filteredList = getFilteredList();
        if (filteredList == null) return 0;
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        //use map position => item here
        Category filteredList = getFilteredList();
        if (filteredList == null) return null;
        return filteredList.getMaker(position);
    }

    @Override
    public long getItemId(int position) {
        Maker m = getFilteredList().getMaker(position);
        return list.getPosition(m);
    }

    private Category getFilteredList() {
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
            view = mInflater.inflate(R.layout.maker_details_item, null);
        } else {
            view = convertView;
        }

        Maker rowItem = (Maker) getItem(position);
        ((TextView) view.findViewById(R.id.text)).setText(rowItem.getBrand());
        ((TextView) view.findViewById(R.id.text1)).setText(rowItem.getOwner());
        if (!TextUtils.isEmpty(rowItem.getLogoURL())) {
            Picasso.with(context).load(rowItem.getLogoURL())
                    .resize(300, 50)
                    .centerInside()
                    .placeholder(R.drawable.ic_launcher)
                    .into((ImageView) view.findViewById(R.id.image));
        }

        return view;
    }

    public void setFilter(String s) {
        if (!TextUtils.equals(s, mFilter)) {
            mFilter = s;
            mFilteredList = null;
            notifyDataSetChanged();
        }
    }
}
