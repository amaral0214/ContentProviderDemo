package com.example.contentproviderdemo;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.contentproviderdemo.dummy.DummyItem;
import com.example.dict.content.Words;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PageDirection {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private EditText customEditText;
    private DrawerLayout drawer;

    private ContentResolver contentResolver;
    private List<com.example.contentproviderdemo.dummy.DummyItem> listItems = new ArrayList<>();
    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter = null;

    private int currentPos = 0;//current pos in NavigationView
    private int currentItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        customEditText = (EditText) findViewById(R.id.custom_edit_text);

        contentResolver = getContentResolver();
        setupRecyclerView();

        setNavigationView();
        setSearchBar(customEditText);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private SimpleItemRecyclerViewAdapter getSimpleItemRecyclerViewAdapter() {
        if (simpleItemRecyclerViewAdapter == null) {
            simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(this, listItems);
        }
        return simpleItemRecyclerViewAdapter;
    }

    private void setupRecyclerView() {
        listItems.clear();
        Cursor cursor = contentResolver.query(Words.Word.DICT_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                DummyItem dictItem = new DummyItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                listItems.add(dictItem);
            }
            cursor.close();
        }
        recyclerView.setAdapter(getSimpleItemRecyclerViewAdapter());
    }

    private void setHeaderAndFooter() {
        View view = getLayoutInflater().inflate(R.layout.custom_edit_text_layout, (ViewGroup) recyclerView.getRootView(), false);
        ((SimpleItemRecyclerViewAdapter) recyclerView.getAdapter()).setHeaderView(view);
    }

    private void setNavigationView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
                    handleSearchAction();
                }
                return false;
            }
        });
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
                    handleSearchAction();
                    return true;
                }
                return false;
            }
        });
    }

    private void handleSearchAction() {
        // 获取用户输入
        String key = customEditText.getText().toString();
        customEditText.setText(null);
        recyclerView.requestFocus();
        hideSoftKeyBoard(recyclerView.getWindowToken());
        if (!TextUtils.isEmpty(key)) {
            Cursor cursor = contentResolver.query(
                    Words.Word.DICT_CONTENT_URI, null,
                    "word like ?", new String[]{
                            "%" + key + "%"}, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    List<com.example.contentproviderdemo.dummy.DummyItem> searchOutItems = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        DummyItem dictItem = new DummyItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                        searchOutItems.add(dictItem);
                    }
                    showDictListDialog(searchOutItems);
                } else {
                    showDictAddDialog(key);
                }
                cursor.close();
            }
        } else {
            showDictAddDialog(key);
        }
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

        if (id == R.id.nav_dict) {
            currentPos = 0;
            fab.setVisibility(View.GONE);
        } else if (id == R.id.nav_account) {
            currentPos = 1;
            fab.setVisibility(View.GONE);
        } else if (id == R.id.nav_gallery) {
            currentPos = 2;
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_play_btn);
        } else if (id == R.id.nav_music) {
            currentPos = 3;
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_play_btn);
        } else if (id == R.id.nav_share) {
            currentPos = 4;
            fab.setVisibility(View.GONE);
        } else if (id == R.id.nav_send) {
            currentPos = 5;
            fab.setVisibility(View.GONE);
            startActivity(new Intent(this, SendActivity.class));
        }
        setupRecyclerView();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void toItemDetailPage(int id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, id);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
            startActivity(intent);
        }
    }

    private void showDictListDialog(final List<com.example.contentproviderdemo.dummy.DummyItem> searchOutItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_list_layout, null);
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        builder.setTitle(getString(R.string.dialog_list_title)).setView(view)
                .setPositiveButton(getString(R.string.edit_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.example.contentproviderdemo.dummy.DummyItem dl = ((SearchResultAdapter) listView.getAdapter()).getSelectedItems().get(0);
                        showDictUpdateDialog(dl);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.delete_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<com.example.contentproviderdemo.dummy.DummyItem> deleteList = ((SearchResultAdapter) listView.getAdapter()).getSelectedItems();
                        int count = 0;
                        for (com.example.contentproviderdemo.dummy.DummyItem dl : deleteList) {
                            if (dl.id == currentItemId) {
                                currentItemId = -1;
                            }
                            int c = contentResolver.delete(ContentUris.withAppendedId(Words.Word.WORD_CONTENT_URI, dl.id), null, null);
                            if (c > 0) {
                                count += c;
                            }
                        }
                        if (count > 0) {
                            listItems.clear();
                            Cursor cursor = contentResolver.query(Words.Word.DICT_CONTENT_URI, null, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    DummyItem dictItem = new DummyItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                                    listItems.add(dictItem);
                                }
                                cursor.close();
                            }
                            dialog.dismiss();
                            getSimpleItemRecyclerViewAdapter().notifyDataSetChanged();
                            if (getSimpleItemRecyclerViewAdapter().getItemCount() <= 0) {
                                currentItemId = 0;
                            } else if (currentItemId == -1) {
                                currentItemId = (int) getSimpleItemRecyclerViewAdapter().getItemId(0);
                            }
                            if (mTwoPane) {
                                toItemDetailPage(currentItemId);
                            }
                        }

                    }
                })
                .setNeutralButton(getString(R.string.cancel_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        SearchResultAdapter searchResultAdapter = new SearchResultAdapter(this, searchOutItems, alertDialog);
        SearchResultMultiChoiceMode mCallback = new SearchResultMultiChoiceMode(searchResultAdapter, alertDialog);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(mCallback);
        listView.setAdapter(searchResultAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toItemDetailPage(((SearchResultAdapter) ((ListView) parent).getAdapter()).getId(position));
            }
        });
        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
    }

    private void showDictUpdateDialog(final com.example.contentproviderdemo.dummy.DummyItem dl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_layout, null);
        final EditText content_et = (EditText) v.findViewById(R.id.content);
        final EditText details_et = (EditText) v.findViewById(R.id.details);
        content_et.setText(dl.content);
        details_et.setText(dl.details);
        builder.setView(v);

        builder.setTitle(getString(R.string.dialog_update_title))
                .setPositiveButton(getString(R.string.update_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = content_et.getText().toString();
                        String details = details_et.getText().toString();
                        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(details)) {
                            keepDialogOpen(dialog, true);
                            Toast.makeText(ItemListActivity.this, "请补充完整！", Toast.LENGTH_SHORT).show();
                        } else {
                            keepDialogOpen(dialog, false);
                            if (!content.equals(dl.content) || !details.equals(dl.details)) {
                                ContentValues values = new ContentValues();
                                values.put(Words.Word.WORD, content);
                                values.put(Words.Word.DETAIL, details);
                                contentResolver.update(ContentUris.withAppendedId(Words.Word.WORD_CONTENT_URI, dl.id), values, null, null);

                                listItems.clear();
                                Cursor cursor = contentResolver.query(Words.Word.DICT_CONTENT_URI, null, null, null, null);
                                if (cursor != null && cursor.getCount() > 0) {
                                    while (cursor.moveToNext()) {
                                        DummyItem dictItem = new DummyItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                                        listItems.add(dictItem);
                                    }
                                    cursor.close();
                                }
                                getSimpleItemRecyclerViewAdapter().notifyDataSetChanged();
                                currentItemId = dl.id;
                                if (mTwoPane) {
                                    toItemDetailPage(currentItemId);
                                }
                                Toast.makeText(ItemListActivity.this, "更新生词成功！", Toast.LENGTH_SHORT).show();
                            }
                            recyclerView.requestFocus();
                            hideSoftKeyBoard(recyclerView.getWindowToken());
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        builder.show();
    }

    private void showDictAddDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_layout, null);
        final EditText content_et = (EditText) v.findViewById(R.id.content);
        final EditText details_et = (EditText) v.findViewById(R.id.details);
        content_et.setHint(key);
        builder.setView(v);

        builder.setTitle(String.format(getString(R.string.dialog_add_title), key))
                .setPositiveButton(getString(R.string.add_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = content_et.getText().toString();
                        String details = details_et.getText().toString();
                        if (content.length() == 0 && content_et.getHint().length() > 0) {
                            content = content_et.getHint().toString();
                        }
                        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(details)) {
                            keepDialogOpen(dialog, true);
                            Toast.makeText(ItemListActivity.this, "请补充完整！", Toast.LENGTH_SHORT).show();
                        } else {
                            keepDialogOpen(dialog, false);
                            // 插入生词记录
                            ContentValues values = new ContentValues();
                            values.put(Words.Word.WORD, content);
                            values.put(Words.Word.DETAIL, details);
                            Uri uri = contentResolver.insert(Words.Word.DICT_CONTENT_URI, values);
                            if (uri != null) {
                                long id = ContentUris.parseId(uri);
                                if (id >= 0) {
                                    DummyItem dictItem = new DummyItem((int) id, content, details);
                                    listItems.add(dictItem);
                                    getSimpleItemRecyclerViewAdapter().notifyDataSetChanged();
                                    currentItemId = (int) id;
                                    if (mTwoPane) {
                                        toItemDetailPage(currentItemId);
                                    }
                                    Toast.makeText(ItemListActivity.this, "添加生词成功！", Toast.LENGTH_SHORT).show();
                                }
                            }
                            recyclerView.requestFocus();
                            hideSoftKeyBoard(recyclerView.getWindowToken());
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        builder.show();
    }

    //保持dialog不关闭的方法
    private void keepDialogOpen(DialogInterface dialog, boolean b) {
        try {
            java.lang.reflect.Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, !b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
