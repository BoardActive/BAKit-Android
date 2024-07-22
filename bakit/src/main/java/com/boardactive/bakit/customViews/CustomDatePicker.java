package com.boardactive.bakit.customViews;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.boardactive.bakit.R;
import com.boardactive.bakit.customViewListener.ClickListenerInterface;


public class CustomDatePicker extends LinearLayout implements View.OnClickListener {
    LinearLayout llMain;
    EditText editText;
    protected Activity activity;
    ClickListenerInterface clickListener;
    public CustomDatePicker(Context context) {
        super(context);
    }

    public CustomDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.datepicker, this, true);// Create a layout file with same name in app module to customize the layout.

        llMain = (LinearLayout) getChildAt(0);
        editText = (EditText) llMain.getChildAt(0);

//        llMain.setOnClickListener(this);
        editText.setOnClickListener(this);
    }

    public void setHint(String name) {
        editText.setHint(name);
    }

    public void setContext(Activity customAttributesActivity) {
        activity = customAttributesActivity;
        clickListener = (ClickListenerInterface) activity;
    }


    @Override
    public void onClick(View v) {
        if(v instanceof EditText){
            if (clickListener != null) {
                clickListener.clickListener(getTag().toString());
            }
        }
    }

    public void setDate(String date) {
        editText.setText(date);
    }

    public String getText() {
        if (editText != null)
            return editText.getText().toString();
        return "";
    }

    public void setText(String toString) {
        editText.setText(toString);
    }
}
