package com.boardactive.bakitapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.boardactive.bakitapp.firebase.FCMService;
import com.boardactive.bakitapp.R;

import com.boardactive.bakitapp.models.NotifType;
import com.boardactive.bakitapp.room.AppDatabase;
import com.boardactive.bakitapp.room.MessageDAO;
import com.boardactive.bakitapp.room.table.MessageEntity;
import com.boardactive.bakitapp.utils.Tools;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogMessageActivity extends AppCompatActivity {

    public static final String TAG = DialogMessageActivity.class.getName();

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_FROM_NOTIF = "key.EXTRA_FROM_NOTIF";
    private static final String EXTRA_POSITION = "key.EXTRA_FROM_POSITION";

    // activity transition
    public static void navigate(Activity activity, MessageEntity obj, Boolean from_notif, int position) {
        Intent i = navigateBase(activity, obj, from_notif);
        i.putExtra(EXTRA_POSITION, position);
        activity.startActivity(i);
    }

    public static Intent navigateBase(Context context, MessageEntity obj, Boolean from_notif) {
        Intent i = new Intent(context, DialogMessageActivity.class);
        i.putExtra(FCMService.EXTRA_OBJECT, obj);
        i.putExtra(FCMService.EXTRA_MESSADE_ID, obj.getId());
        i.putExtra(EXTRA_FROM_NOTIF, from_notif);
        return i;
    }

    private Boolean from_notif;
    private MessageEntity messageEntity;
    private Intent intent;
    private MessageDAO mMessageDAO;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_message);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mMessageDAO = AppDatabase.getDb(this).getMessageDAO();

        messageEntity = (MessageEntity) getIntent().getSerializableExtra(EXTRA_OBJECT);
        from_notif = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);
        position = getIntent().getIntExtra(EXTRA_POSITION, -1);

        // set notification as read
//        messageEntity.setIsRead(true);;
//        mMessageDAO.insertMessage(messageEntity);

        initComponent();
    }

    private void initComponent() {
        String dateCreated = new SimpleDateFormat("MM/dd/yyyy").format(new Date(messageEntity.getDateCreated()));

        ((TextView) findViewById(R.id.title)).setText(messageEntity.getTitle());
        ((TextView) findViewById(R.id.content)).setText(messageEntity.getBody());
        ((TextView) findViewById(R.id.date)).setText(dateCreated);
        if (messageEntity.getIsTestMessage().equals("1") ){
            ((TextView) findViewById(R.id.type)).setText("Test Notification");
        } else {
            ((TextView) findViewById(R.id.type)).setText(messageEntity.getLatitude() + " " + messageEntity.getLongitude());
        }

        String image_url = messageEntity.getImageUrl();
        final String type = messageEntity.getIsTestMessage();
        intent = new Intent(this, MessageActivity.class);
        intent.putExtra(FCMService.EXTRA_MESSADE_ID, messageEntity.getId());

        if (type.equalsIgnoreCase(NotifType.IMAGE.name())) {
            image_url = messageEntity.getImageUrl();
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        if (from_notif) {
            (findViewById(R.id.bt_delete)).setVisibility(View.GONE);
            if (MessageActivity.active && (type.equalsIgnoreCase(NotifType.NORMAL.name()) || type.equalsIgnoreCase(NotifType.IMAGE.name()))) {
                ((LinearLayout) findViewById(R.id.lyt_action)).setVisibility(View.GONE);
            }
            ((TextView) findViewById(R.id.dialog_title)).setText(R.string.app_name);
        } else {
            if (type.equalsIgnoreCase(NotifType.NORMAL.name()) || type.equalsIgnoreCase(NotifType.IMAGE.name())) {
                (findViewById(R.id.bt_open)).setVisibility(View.GONE);
            }
        }

        (findViewById(R.id.lyt_image)).setVisibility(View.GONE);
        if (image_url != null) {
            (findViewById(R.id.lyt_image)).setVisibility(View.VISIBLE);
            Tools.displayImageOriginal(this, ((ImageView) findViewById(R.id.image)), image_url);
        }

        ((ImageView) findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        (findViewById(R.id.bt_open)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionOpen(type);
            }
        });

        (findViewById(R.id.bt_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!from_notif && position != -1) {
                    mMessageDAO.deleteMessage(messageEntity.getId());
                    MessagesActivity.getInstance().adapter.removeItem(position);
                    Snackbar.make(MessagesActivity.getInstance().parent_view, "Delete successfull", Snackbar.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        if (from_notif && type.equalsIgnoreCase(NotifType.LINK.name())) {
            actionOpen(type);
        }
    }

    private void actionOpen(String type) {
        if (type.equalsIgnoreCase(NotifType.LINK.name())) {
            Tools.openInAppBrowser(DialogMessageActivity.this, messageEntity.getTitle(), from_notif);
        } else {
            intent.putExtra(EXTRA_OBJECT, messageEntity);
            intent.putExtra(EXTRA_FROM_NOTIF, from_notif);
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 509) {
            if (from_notif) {
                Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            finish();
        }
    }
}