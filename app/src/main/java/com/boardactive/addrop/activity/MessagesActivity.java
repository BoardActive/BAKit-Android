package com.boardactive.addrop.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.boardactive.addrop.R;

import com.boardactive.bakit.BoardActive;
import com.boardactive.addrop.adapter.AdapterMessages;
import com.boardactive.addrop.room.AppDatabase;
import com.boardactive.addrop.room.MessageDAO;
import com.boardactive.addrop.room.table.MessageEntity;
import com.boardactive.addrop.utils.Tools;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    public static final String TAG = MessagesActivity.class.getName();

    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, MessagesActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(i);
    }

    private SwipeRefreshLayout swipe_refresh;

    public View parent_view;
    private RecyclerView recyclerView;
    private MessageDAO mMessageDAO;
    public AdapterMessages adapter;
    static MessagesActivity activityNotifications;

    private BoardActive mBoardActive;

    private ActionBar actionBar;
    private Toolbar toolbar;

    private NavigationView mNavigationView;

    public static MessagesActivity getInstance() {
        return activityNotifications;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        activityNotifications = this;

        mBoardActive = new BoardActive(getApplicationContext());

        mMessageDAO = AppDatabase.getDb(this).getMessageDAO();

        initToolbar();
        iniComponent();
        onResume();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar = getSupportActionBar();
        if(Tools.getSharedPrecerenceBoolean(getApplicationContext(), "DEV")) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } else {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }
        actionBar.setTitle("My Messages");
        Tools.setSystemBarColor(this);
    }

    private void iniComponent() {
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        parent_view = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set data and list adapter
        adapter = new AdapterMessages(this, recyclerView, new ArrayList<MessageEntity>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterMessages.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final MessageEntity obj, final int pos) {

                mBoardActive.postEvent(new BoardActive.PostEventCallback() {
                    @Override
                    public void onResponse(Object value) {
                        Log.d(TAG, "[BAKitApp] Received Event: " + value.toString());
                        DialogMessageActivity.navigate(MessagesActivity.this, obj, false, pos);
                    }
                }, "opened", obj.getBaMessageId(), obj.getBaNotificationId(), obj.getFirebaseNotificationId());
            }
        });

        // on swipe list
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullAndRefresh();
            }
        });

        startLoadMoreAdapter();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_messages, menu);
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
        if (mBoardActive.isRegisteredDevice()) {
            pullAndRefresh();
        } else {
            //LoginActivity.navigate(this);
        }
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
                mMessageDAO.deleteAllMessage();
                startLoadMoreAdapter();
                Snackbar.make(parent_view, "Delete successfully", Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }

    private void startLoadMoreAdapter() {
        adapter.resetListData();
        List<MessageEntity> items = mMessageDAO.getMessageByPage(20, 0);
        adapter.insertData(items);
        showNoItemView();
        final int item_count = (int) mMessageDAO.getMessageCount();
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
                List<MessageEntity> items = mMessageDAO.getMessageByPage(20, (next_page * 20));
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

    private void pullAndRefresh() {
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startLoadMoreAdapter();
                swipeProgress(false);
            }
        }, 3000);
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            return;
        }
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }

    private void viewToken() {
        showAlert("FCM Token", mBoardActive.getAppToken());
    }

    private void viewAppVars() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("AppId", mBoardActive.getAppId());
            obj.put("AppUrl", mBoardActive.getAppUrl());
            obj.put("AppKey", mBoardActive.getAppKey());
            obj.put("AppEmail", mBoardActive.getUserEmail());
            obj.toString(2);
            showAlert("App Vars", obj.toString(2));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void registerDevice() {
        mBoardActive.registerDevice(new BoardActive.PostRegisterCallback() {
            @Override
            public void onResponse(Object value) {
                showAlert("Register Device", value.toString());
            }
        });

    }

    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MessagesActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
