package com.boardactive.bakitapp.customViews;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.boardactive.bakitapp.R;


public class CustomCheckBox extends LinearLayout {
    LinearLayout llMain;
    CheckBox checkBox;

    public CustomCheckBox(Context context) {
        super(context);
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_checkbox, this, true);// Create a layout file with same name in app module to customize the layout.

        llMain = (LinearLayout) getChildAt(0);
        checkBox = (CheckBox) llMain.getChildAt(0);

    }

    public void setHint(String name) {
        checkBox.setHint(name);
    }

    public void setContext(Activity customAttributesActivity) {
    }

    public boolean getCheckBoxValue() {
        return checkBox.isChecked();
    }

    public void checkCheckBox(boolean object) {
        if(object)
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);
    }
}
