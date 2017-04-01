package com.example.contentproviderdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

/**
 * Created by frank.bi on 3/8/2017.
 */

public class SearchResultMultiChoiceMode implements AbsListView.MultiChoiceModeListener {
    private SearchResultAdapter mAdapter;
    private AlertDialog dialog;

    public SearchResultMultiChoiceMode(SearchResultAdapter mAdapter, AlertDialog dialog) {
        this.mAdapter = mAdapter;
        this.dialog = dialog;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            mAdapter.addSelectedItem(position);
        } else {
            mAdapter.removeSelectedItem(position);
        }
        if (dialog != null) {
            if (mAdapter.getSelectedItemsCount() > 0) {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);//delete
            } else {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);//delete
            }
            if (mAdapter.getSelectedItemsCount() == 1) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);//edit
            } else {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);//edit
            }
        }
        mAdapter.notifyDataSetChanged();
        mode.invalidate();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mAdapter.setEditMode(true);

        //if return false, actionmode will invoke soon. Besides, onItemCheckedStateChanged doesn't work. I have added it's function content to adapter.
        //if run with action mode, remove below line(notifyDataSetChanged()) and return true
        mAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mAdapter.setEditMode(false);
        mAdapter.clearSelectedItems();
        mAdapter.notifyDataSetChanged();
    }
}
