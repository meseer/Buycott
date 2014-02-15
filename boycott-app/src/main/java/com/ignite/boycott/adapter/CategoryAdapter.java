package com.ignite.boycott.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

import static com.ignite.boycott.util.DisplayUtils.dpToPx;

/**
 * Created by meseer on 12.02.14.
 * This class is not thread safe!
 */
public class CategoryAdapter extends BaseAdapter {
    private final Context context;
    private Category mCategory;
    private LayoutInflater mInflater;
    private String mFilter;
    private Category mFilteredCategory;

    public CategoryAdapter(Context context, Category mCategory) {
        this.mCategory = mCategory;
        this.context = context;
        mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mFilter = null;
        mFilteredCategory = mCategory;
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
        return mCategory.getPosition(m);
    }

    private Category getFilteredList() {
        if (mCategory != null && mFilteredCategory == null) {
            if (TextUtils.isEmpty(mFilter)) {
                mFilteredCategory = mCategory;
            } else {
                mFilteredCategory = mCategory.filterMakers(new MakerContainsPredicate(mFilter));
            }
        }
        return mFilteredCategory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.maker_list_item, null);
        } else {
            view = convertView;
        }

        Maker rowItem = (Maker) getItem(position);
        ((TextView) view.findViewById(R.id.text)).setText(rowItem.getBrand());
        ((TextView) view.findViewById(R.id.text1)).setText(rowItem.getOwner());
        if (!TextUtils.isEmpty(rowItem.getLogoURL())) {
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            DisplayMetrics m = context.getResources().getDisplayMetrics();
            Picasso.with(context).load(rowItem.getLogoURL())
                    .resize(dpToPx(300, m), dpToPx(50, m))
                    .centerInside()
                    .placeholder(R.drawable.ic_launcher)
                    .into(imageView);
        }

        return view;
    }

    public void setFilter(String s) {
        if (!TextUtils.equals(s, mFilter)) {
            mFilter = s;
            mFilteredCategory = null;
            notifyDataSetChanged();
        }
    }

    public void switchCategory(Category item, String filter) {
        mCategory = item;
        mFilteredCategory = null;
        mFilter = filter;
        notifyDataSetChanged();
    }
}
