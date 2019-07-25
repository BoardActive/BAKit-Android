package com.boardactive.addrop.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boardactive.addrop.R;
import com.boardactive.addrop.adapter.AdapterMessages;
import com.boardactive.addrop.room.AppDatabase;
import com.boardactive.addrop.room.DAO;
import com.boardactive.addrop.room.table.MessageEntity;
import com.boardactive.addrop.utils.Tools;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, MessagesActivity.class);
        activity.startActivity(i);
    }

    public View parent_view;
    private RecyclerView recyclerView;
    private DAO dao;
    public AdapterMessages adapter;
    static MessagesActivity activityNotifications;

    public static MessagesActivity getInstance() {
        return activityNotifications;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        activityNotifications = this;

        dao = AppDatabase.getDb(this).getDAO();

        initToolbar();
        iniComponent();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Messages");
        Tools.setSystemBarColor(this, android.R.color.black);
    }

    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set data and list adapter
        adapter = new AdapterMessages(this, recyclerView, new ArrayList<MessageEntity>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterMessages.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MessageEntity obj, int pos) {
                obj.isRead = true;
                DialogMessageActivity.navigate(MessagesActivity.this, obj, false, pos);
            }
        });

        startLoadMoreAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_notification, menu);
        Tools.changeMenuIconColor(menu, Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        } else if (item_id == R.id.action_delete) {
            if (adapter.getItemCount() == 0) {
                return true;
            }
            dialogDeleteConfirmation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void dialogDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure want to delete all notifications ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                dao.deleteAllMessage();
                startLoadMoreAdapter();
                Snackbar.make(parent_view, "Delete successfully", Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }

    private void startLoadMoreAdapter() {
        adapter.resetListData();
        List<MessageEntity> items = dao.getMessageByPage(20, 0);
        adapter.insertData(items);
        showNoItemView();
        final int item_count = (int) dao.getMessageCount();
        // detect when scroll reach bottom
        adapter.setOnLoadMoreListener(new AdapterMessages.OnLoadMoreListener() {
            @Override
            public void onLoadMore(final int current_page) {
                if (item_count > adapter.getItemCount() && current_page != 0) {
                    displayDataByPage(current_page);
                } else {
                    adapter.setLoaded();
                }
            }
        });
    }

    private void displayDataByPage(final int next_page) {
        adapter.setLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<MessageEntity> items = dao.getMessageByPage(20, (next_page * 20));
                adapter.insertData(items);
                showNoItemView();
            }
        }, 500);
    }

    private void showNoItemView() {
//        View lyt_no_item = findViewById(R.id.lyt_failed);
//        (findViewById(R.id.failed_retry)).setVisibility(View.GONE);
//        ((ImageView) findViewById(R.id.failed_icon)).setImageResource(R.drawable.img_no_item);
//        ((TextView) findViewById(R.id.failed_message)).setText(R.string.no_item);
//        if (adapter.getItemCount() == 0) {
//            lyt_no_item.setVisibility(View.VISIBLE);
//        } else {
//            lyt_no_item.setVisibility(View.GONE);
//        }
    }
}
