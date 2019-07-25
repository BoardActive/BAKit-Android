package com.boardactive.addrop.activity;

import androidx.annotation.NonNull;

import com.boardactive.addrop.BuildConfig;
import com.boardactive.addrop.adapter.AdapterMessages;
import com.boardactive.addrop.dialog.SettingsDialog;
import com.boardactive.addrop.fragment.DebugFragment;
import com.boardactive.addrop.fragment.HelpFragment;
import com.boardactive.addrop.model.User;
import com.boardactive.addrop.room.AppDatabase;
import com.boardactive.addrop.room.DAO;
import com.boardactive.addrop.room.table.MessageEntity;
import com.boardactive.bakit.BoardActive;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardactive.addrop.R;
import com.boardactive.addrop.utils.Tools;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BoardActive mBoardActive;

    private BottomNavigationView mNavigation;

    final Fragment debugFragment = new DebugFragment();
    final Fragment helpFragment = new HelpFragment();

    private Toolbar mToolbar;
    private ActionBar actionBar;

    private LinearLayout ll_signedout, ll_signedin, ll_display_instructions, ll_message_container, ll_fragment_container;
    public FrameLayout fl_fragment_container;
    private Button mBtnSignIn, mBtnSignOut;
    public ImageView iv_title_logo, iv_image;
    private TextView tv_email;

    private RecyclerView recyclerView;
    private DAO dao;
    public AdapterMessages adapter;

    public static final Integer APP_VERSION = BuildConfig.VERSION_CODE;
    public static final String APP_OS = BuildConfig.VERSION_NAME;

    private static final int MAX_CLICKS_TO_UNLOCK_EGG = 7;
    private int numTimesVersionClicked;

    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, MainActivity.class);
        activity.startActivity(i);
    }


    public View parent_view;
    static MessagesActivity activityNotifications;

    public static MessagesActivity getInstance() {
        return activityNotifications;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardActive = new BoardActive(getApplicationContext());
        dao = AppDatabase.getDb(this).getDAO();

        if(mBoardActive.isRegisteredDevice()){
            registerDevice();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        initToolbar();
        initComponent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mBoardActive.isRegisteredDevice()){
            mToolbar.setVisibility(View.VISIBLE);
            mNavigation.setVisibility(View.VISIBLE);
            ll_signedin.setVisibility(View.VISIBLE);
            ll_signedout.setVisibility(View.GONE);
            ll_display_instructions.setVisibility(View.GONE);
            iv_image.setImageResource(R.drawable.image_13);
            tv_email.setVisibility(View.VISIBLE);
            String email = getSharedPreference(BoardActive.BAKIT_USER_EMAIL);
            tv_email.setText(email);
            refreshAdapter();

        } else {
            mToolbar.setVisibility(View.GONE);
            mNavigation.setVisibility(View.GONE);
            ll_signedin.setVisibility(View.GONE);
            ll_signedout.setVisibility(View.VISIBLE);
            ll_display_instructions.setVisibility(View.VISIBLE);
            iv_image.setImageResource(R.drawable.image_27);
            tv_email.setVisibility(View.GONE);
        }

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this);
    }


    private void initComponent() {
        ll_signedin = (LinearLayout) findViewById(R.id.ll_signedin);
        ll_signedout = (LinearLayout) findViewById(R.id.ll_signedout);
        ll_message_container = (LinearLayout) findViewById(R.id.ll_message_container);
        ll_display_instructions = (LinearLayout) findViewById(R.id.ll_display_instructions);
        fl_fragment_container = (FrameLayout) findViewById(R.id.fragment_container);

        fl_fragment_container.setVisibility(View.VISIBLE);
        ll_message_container.setVisibility(View.GONE);

        mBtnSignIn = (Button) findViewById(R.id.btn_SignIn);
        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoardActive.setAppVersion("1.0.0");
                mBoardActive.setAppTest("1");
                mBoardActive.setAppOSVersion(APP_VERSION.toString());
                openSignIn();
            }
        });

        mBtnSignOut = (Button) findViewById(R.id.btn_SignOut);
        mBtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                            mBoardActive.unRegisterDevice();
                            onResume();
            }
        });

        iv_title_logo = (ImageView) findViewById(R.id.iv_title_logo);
        iv_title_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBoardActive.isRegisteredDevice()) {
                    if (++numTimesVersionClicked == MAX_CLICKS_TO_UNLOCK_EGG) {
                        ll_message_container.setVisibility(View.GONE);
                        fl_fragment_container.setVisibility(View.VISIBLE);
                        loadFragment(debugFragment);
                        numTimesVersionClicked = 0;
                    } else {
                        onResume();
                    }
                }
            }
        });

        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_messages:
                        ll_message_container.setVisibility(View.GONE);
                        fl_fragment_container.setVisibility(View.VISIBLE);
                        loadFragment(debugFragment);
                        return true;
                    case R.id.navigation_help:
                        ll_message_container.setVisibility(View.GONE);
                        fl_fragment_container.setVisibility(View.VISIBLE);
                        loadFragment(helpFragment);
                        return true;
                }
                return false;
            }
        });

        iv_image = (ImageView) findViewById(R.id.iv_image);
        tv_email = (TextView) findViewById(R.id.tv_email);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        NestedScrollView nested_content = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateNavigation(false);
                    animateSearchBar(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateNavigation(true);
                    animateSearchBar(true);
                }
            }
        });

        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);

        loadFragment(debugFragment);

    }

    private void refreshAdapter() {
        //set data and list adapter
        adapter = new AdapterMessages(MainActivity.this, recyclerView, new ArrayList<MessageEntity>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterMessages.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MessageEntity obj, int pos) {
                obj.isRead = true;
                DialogMessageActivity.navigate(MainActivity.this, obj, false, pos);
            }
        });

        startLoadMoreAdapter();

    }

    boolean isNavigationHide = false;

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * mNavigation.getHeight()) : 0;
        mNavigation.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
//        int moveY = hide ? -(2 * search_bar.getHeight()) : 0;
//        search_bar.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void registerDevice(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String fcmToken = task.getResult().getToken();
                        mBoardActive.setAppToken(fcmToken);

                        mBoardActive.initialize();

                        mBoardActive.registerDevice(new BoardActive.PostRegisterCallback() {
                            @Override
                            public void onResponse(Object value) {
                                Log.d("[BAkit]", value.toString());
                                Gson gson = new Gson();
                                User user = gson.fromJson(value.toString(), User.class);
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("USER_DETAILS", user.toString());
                                editor.commit();

                                onResume();
                            }
                        });

                    }
                });
    }


    public void openSignIn() {
        new SettingsDialog(this, new SettingsDialog.InputSenderDialogListener() {
            @Override
            public void onSignIn(final Boolean development, final String email, final String password) {
                Log.d(TAG, "The user tapped OK, number is " + email);
                if (development) {
                    mBoardActive.setAppUrl(BoardActive.APP_URL_DEV);
                    mBoardActive.setAppKey(BoardActive.APP_KEY_DEV);
                } else {
                    mBoardActive.setAppUrl(BoardActive.APP_URL_PROD);
                    mBoardActive.setAppKey(BoardActive.APP_KEY_PROD);
                }
                mBoardActive.postLogin(new BoardActive.PostLoginCallback() {
                    @Override
                    public void onResponse(Object value) {
                        Log.d("[BAkit] postLocation", value.toString());
                        registerDevice();
                    }
                }, email, password);
            }

        }).setCredentials(getSharedPreference(BoardActive.BAKIT_USER_EMAIL), getSharedPreference(BoardActive.BAKIT_USER_PASSWORD)).show();
    }

    private String getSharedPreference(String name) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String value = settings.getString(name,"");
        return value;
    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void startLoadMoreAdapter() {
        adapter.resetListData();
        List<MessageEntity> items = dao.getMessageByPage(20, 0);
        adapter.insertData(items);
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
            }
        }, 500);
    }


    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }


}