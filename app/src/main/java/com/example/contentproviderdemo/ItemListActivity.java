package com.example.contentproviderdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.contentproviderdemo.dummy.DummyContent;

import java.util.List;
import java.util.zip.Inflater;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View recyclerView = findViewById(R.id.item_list);
        EditText customEditText = (EditText) findViewById(R.id.custom_edit_text);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
//        setHeaderAndFooter((RecyclerView) recyclerView);
        setSearchBar(customEditText);
        setNavigationView();

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    private void setHeaderAndFooter(@NonNull RecyclerView recyclerView) {
/*        EditText editText = new EditText(this);
        editText.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setTextAppearance(this, R.style.edit_text_style);
        editText.setHint(getString(R.string.edit_text_hint));
        ((SimpleItemRecyclerViewAdapter) recyclerView.getAdapter()).setHeaderView(editText);*/
        View view = getLayoutInflater().inflate(R.layout.custom_edit_text_layout, (ViewGroup) recyclerView.getRootView(), false);
        ((SimpleItemRecyclerViewAdapter) recyclerView.getAdapter()).setHeaderView(view);
    }

    private void setSearchBar(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = editText.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > editText.getWidth()
                        - editText.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    editText.setText("");
                }
                return false;
            }
        });
    }

    private void setNavigationView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /*    实现功能：点击EditText，软键盘出现并且不会隐藏，点击或者触摸EditText以外的其他任何区域，软键盘被隐藏；*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Finger touch screen event
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // get current focus,Generally it is EditText
            View view = getCurrentFocus();
            if (isShouldHideSoftKeyBoard(view, ev)) {
                hideSoftKeyBoard(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideSoftKeyBoard(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] l = {0, 0};
            view.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + view.getHeight(), right = left
                    + view.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        // if the focus is EditText,ignore it;
        return false;
    }

    private void hideSoftKeyBoard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        static final int TYPE_HEADER = 0;
        static final int TYPE_FOOTER = 1;
        static final int TYPE_NORMAL = 2;

        private final List<DummyContent.DummyItem> mValues;
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

        SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
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
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                            ItemDetailFragment fragment = new ItemDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, ItemDetailActivity.class);
                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

//                        context.startActivity(intent);
                            Utils.startActivityUseAnimation(context, intent, -1, Utils.ANIMATION_TYPE_LEFT_RIGHT);
                        }
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
            DummyContent.DummyItem mItem;

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
}
