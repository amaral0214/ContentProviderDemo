package com.example.contentproviderdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private List<DummyItem> items;
    private boolean isEditMode;
    private List<DummyItem> selectItems;
    private AlertDialog dialog;

    SearchResultAdapter(Context context, List<DummyItem> items, AlertDialog dialog) {
        this.context = context;
        this.items = items;
        this.dialog = dialog;
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
                        removeSelectedItem(position);
                    } else {
                        viewHolder.checkBox.setChecked(true);
                        addSelectedItem(position);
                    }
                    if (getSelectedItemsCount() > 0) {
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);//delete
                    } else {
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);//delete
                    }
                    if (getSelectedItemsCount() == 1) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);//edit
                    } else {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);//edit
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
        selectItems.add(getItem(position));
    }

    public void removeSelectedItem(int position) {
        selectItems.remove(getItem(position).id);
    }

    public void clearSelectedItems() {
        selectItems.clear();
    }

    public List<DummyItem> getSelectedItems() {
        return selectItems;
    }

    public int getSelectedItemsCount() {
        return selectItems.size();
    }

    public int getId(int position) {
        return getItem(position).id;
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
