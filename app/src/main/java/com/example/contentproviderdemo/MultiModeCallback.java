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

public class MultiModeCallback implements AbsListView.MultiChoiceModeListener {
    private SearchResultAdapter mAdapter;
    private AlertDialog dialog;

    public MultiModeCallback(AlertDialog dialog, SearchResultAdapter mAdapter) {
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
        mAdapter.notifyDataSetChanged();

        if (dialog != null) {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
        }
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
