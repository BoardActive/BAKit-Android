package com.boardactive.bakitapp.customViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.boardactive.bakitapp.BoardActive;
import com.boardactive.bakitapp.R;
import com.boardactive.bakitapp.customViewListener.ClickListenerInterface;
import com.boardactive.bakitapp.models.CustomAttributes;
import com.boardactive.bakitapp.models.Me;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class CustomAttributesActivity extends AppCompatActivity implements ClickListenerInterface {

    private BoardActive mBoardActive;
    private List<CustomAttributes> customAttributesList;
    LinearLayout llCustomViews;
    ViewFlipper viewFlipper;
    LinearLayout llSave;
    HashMap<String,Object> updatedCustomAttributes;
    public static final int LOADING = 0;
    public static final int CONTENT = 1;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_attributes);

        initToolBar();
        initViews();



        // base url sent by end user.
        url = getIntent().getStringExtra("baseUrl");
        initBakit();
    }

    private void initBakit() {
        // Create an instant of BoardActive
        mBoardActive = new BoardActive(getApplicationContext());

        // Add URL to point to BoardActive REST API
        mBoardActive.setAppUrl(url);

        // Add AppID provided by BoardActive
        mBoardActive.setAppId("164");

        // Add AppKey provided by BoardActive
        mBoardActive.setAppKey("ef748553-e55a-4cb4-b339-7813e395a5b1");

        // Add the version of your App
        mBoardActive.setAppVersion("1.0.0");

//        mStockAttributes = new Stock();

        mBoardActive.getAttributes(new BoardActive.GetMeCallback() {
            @Override
            public void onResponse(Object value) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type collectionType = new TypeToken<List<CustomAttributes>>(){}.getType();
                customAttributesList = gson.fromJson(value.toString(), collectionType);
                getMe();

            }
        });
    }

    private void initViews() {
        llCustomViews = findViewById(R.id.llCustomViews);
        viewFlipper = findViewById(R.id.viewFlipper);
        btn_Save();
        viewFlipper.setDisplayedChild(LOADING);
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSystemBarColor(this);
    }


    public static void setSystemBarColor(AppCompatActivity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void getMe() {
        mBoardActive.getMe(new BoardActive.GetMeCallback() {
            @Override
            public void onResponse(Object value) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Me mMe = gson.fromJson(value.toString(), Me.class);
                HashMap customAttributesMap = mMe.getAttributes().getCustom();

                for (int i = 0; i <customAttributesList.size() ; i++) {
                    if(!customAttributesList.get(i).getIsStock()){
                            View v =  returnViewAccordingToDataType(customAttributesList.get(i),i+"",customAttributesMap.get(customAttributesList.get(i).getName()));
                            if(v!=null){
                                llCustomViews.addView(v);
                            }
                    }
                }

                viewFlipper.setDisplayedChild(CONTENT);
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void btn_Save() {
        llSave = findViewById(R.id.llSave);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedCustomAttributes = new HashMap<>();
                for (int i = 0; i <customAttributesList.size() ; i++) {
                    if(!customAttributesList.get(i).getIsStock()){
                        getDataFromView(i,customAttributesList.get(i).getType(),customAttributesList.get(i).getName());
                    }
                }


                mBoardActive.putCustomAtrributes(new BoardActive.PutMeCallback() {
                    @Override
                    public void onResponse(Object value) {
                        finish();
                    }
                }, updatedCustomAttributes);
            }
        });
    }

    private void getDataFromView(int i, String type, String name) {
        switch ((String)type){
            case "string":
                CustomEditText customEditText = llCustomViews.findViewWithTag(i + "");
                updatedCustomAttributes.put(name,customEditText.getText());
                break;

            case "integer":
                CustomEditText customEditTextForInt = llCustomViews.findViewWithTag(i + "");
                updatedCustomAttributes.put(name,customEditTextForInt.getText());
                break;
            case "double":
                CustomEditText customEditTextForDouble = llCustomViews.findViewWithTag(i + "");
                updatedCustomAttributes.put(name,customEditTextForDouble.getText());
                break;

            case "date":
                CustomDatePicker customDatePicker = llCustomViews.findViewWithTag(i + "");
                updatedCustomAttributes.put(name,customDatePicker.getText());
                break;
            case "boolean":
                CustomCheckBox customCheckBox = llCustomViews.findViewWithTag(i + "");
                updatedCustomAttributes.put(name,customCheckBox.getCheckBoxValue());
                break;
        }

    }

    private View returnViewAccordingToDataType(CustomAttributes customAttributes, String tag, Object object) {
        View v;
        String type = customAttributes.getType();
        switch (type){
            case "string":
                v= getLayoutInflater().inflate(R.layout.edit_text,null);

                ((CustomEditText) v).setInputTypeAccordingToType(type);
                if(object == null || object.toString().equals(""))
                ((CustomEditText) v).setHint(customAttributes.getName());
                else
                    ((CustomEditText) v).setText(object.toString());
                v.setTag(tag);
                break;

            case "integer":
                v= getLayoutInflater().inflate(R.layout.edit_text,null);
                //set input type as number
                ((CustomEditText) v).setInputTypeAccordingToType(type);
                if(object == null || object.toString().equals(""))
                    ((CustomEditText) v).setHint(customAttributes.getName());
                else
                    ((CustomEditText) v).setText(object.toString());
                v.setTag(tag);
                break;
            case "double":
                v= getLayoutInflater().inflate(R.layout.edit_text,null);
                //set input type as number
                ((CustomEditText) v).setInputTypeAccordingToType(type);
                if(object == null || object.toString().equals(""))
                    ((CustomEditText) v).setHint(customAttributes.getName());
                else
                    ((CustomEditText) v).setText(object.toString());
                v.setTag(tag);
                break;

            case "date":
                v = getLayoutInflater().inflate(R.layout.date,null);
                if(object == null || object.toString().equals(""))
                    ((CustomDatePicker) v).setHint(customAttributes.getName());
                else
                    ((CustomDatePicker) v).setText(object.toString());
                ((CustomDatePicker) v).setContext(this);
                v.setTag(tag);
                break;
            case "boolean":
                v = getLayoutInflater().inflate(R.layout.checkbox,null);
                ((CustomCheckBox) v).setHint(customAttributes.getName());

                if(object!=null){
                    if(object instanceof Boolean){
                        ((CustomCheckBox) v).checkCheckBox((Boolean) object);
                    }
                }

                ((CustomCheckBox) v).setContext(this);
                v.setTag(tag);
                break;
            default:
                v = null;
                break;
        }
        return v;
    }

    @Override
    public void clickListener(final String tag) {
        if(llCustomViews.findViewWithTag(tag) instanceof  CustomDatePicker ){

            Calendar cur_calender = Calendar.getInstance();
            DatePickerDialog datePicker = DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            long date_ship_millis = calendar.getTimeInMillis();
                            CustomDatePicker customDatePicker = llCustomViews.findViewWithTag(tag);
                            customDatePicker.setDate(getFormattedDateSimple(date_ship_millis));
                        }
                    },
                    cur_calender.get(Calendar.YEAR),
                    cur_calender.get(Calendar.MONTH),
                    cur_calender.get(Calendar.DAY_OF_MONTH)
            );
            //set dark light
            datePicker.setThemeDark(false);
            datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
            datePicker.setMinDate(cur_calender);
            datePicker.show(getFragmentManager(), "Datepickerdialog");

        }
    }


    public static String getFormattedDateSimple(Long dateTime) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(tz);
        String nowAsISO = df.format(dateTime);
        return nowAsISO;
//        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        return newFormat.format(new Date(dateTime));
    }

}
