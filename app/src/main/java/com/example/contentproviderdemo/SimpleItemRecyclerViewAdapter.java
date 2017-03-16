package com.example.contentproviderdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.contentproviderdemo.dummy.DummyItem;

import java.util.List;

/**
 * Created by frank.bi on 3/8/2017.
 */

public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    static final int TYPE_HEADER = 0;
    static final int TYPE_FOOTER = 1;
    static final int TYPE_NORMAL = 2;

    private Context context;
    private final List<DummyItem> mValues;
    private View mHeaderView, mFooterView;

    public View getHeaderView() {
        return mHeaderView;
    }

    void setHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
        notifyItemInserted(0);
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View mFooterView) {
        this.mFooterView = mFooterView;
        notifyItemInserted(getItemCount() - 1);
    }

    SimpleItemRecyclerViewAdapter(Context context, List<DummyItem> items) {
        this.context = context;
        mValues = items;
    }

    @Override
    public int getItemViewType(int position) {
//            return super.getItemViewType(position);
        if (mHeaderView == null && mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new ViewHolder(mHeaderView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new ViewHolder(mFooterView);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PageDirection) context).toItemDetailPage(holder.mItem.id);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) {
            return mValues.size();
        } else if (mHeaderView == null || mFooterView == null) {
            return mValues.size() + 1;
        } else {
            return mValues.size() + 2;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mIdView;
        TextView mContentView;
        DummyItem mItem;

        ViewHolder(View view) {
            super(view);
            if (view == mHeaderView || view == mFooterView) {
                return;
            }
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}