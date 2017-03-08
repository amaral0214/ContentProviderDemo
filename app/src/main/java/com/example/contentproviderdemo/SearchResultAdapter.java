package com.example.contentproviderdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.contentproviderdemo.dummy.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank.bi on 3/6/2017.
 */
public class SearchResultAdapter extends BaseAdapter {
    private Context context;
    private List<? extends DummyItem> items;
    private boolean isEditMode;
    private List<String> selectItems;

    SearchResultAdapter(Context context, List<? extends DummyItem> items) {
        this.context = context;
        this.items = items;
        isEditMode = false;
        selectItems = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public DummyItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.search_result_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.content.setText(getItem(position).content);
        if (isEditMode) {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.checkBox.isChecked()) {
                        viewHolder.checkBox.setChecked(false);
                    } else {
                        viewHolder.checkBox.setChecked(true);
                    }
                }
            });
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        return convertView;
    }

    public boolean getEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public void addSelectedItem(int position) {
        selectItems.add(getItem(position).id);
    }

    public void removeSelectedItem(int position) {
        selectItems.remove(getItem(position).id);
    }

    public void clearSelectedItems() {
        selectItems.clear();
    }

    public List<String> getSelectedItems() {
        return selectItems;
    }

    public int getSelectedItemsCount() {
        return selectItems.size();
    }

    static class ViewHolder {
        LinearLayout ll;
        CheckBox checkBox;
        TextView content;

        ViewHolder(View v) {
            ll = (LinearLayout) v;
            checkBox = (CheckBox) v.findViewById(R.id.search_result_checkbox);
            content = (TextView) v.findViewById(R.id.search_result_content);
        }
    }
}
