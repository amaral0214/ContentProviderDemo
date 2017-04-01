package com.example.contentproviderdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

/**
 * Created by frank.bi on 3/8/2017.
 */

public class SimpleItemMultiChoiceMode implements AbsListView.MultiChoiceModeListener {
    private SimpleItemRecyclerViewAdapter mAdapter;
    private AlertDialog dialog;

    public SimpleItemMultiChoiceMode(SimpleItemRecyclerViewAdapter mAdapter, AlertDialog dialog) {
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
        mAdapter.notifyDataSetChanged();
        mode.invalidate();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mAdapter.setEditMode(true);
        return true;
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
