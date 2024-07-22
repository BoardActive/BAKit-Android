package com.boardactive.bakit.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.boardactive.bakit.R;


public class CustomEditText extends LinearLayout {
    LinearLayout llMain;
    EditText editText;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_edittext, this, true);// Create a layout file with same name in app module to customize the layout.

        llMain = (LinearLayout) getChildAt(0);
        editText = (EditText) llMain.getChildAt(0);

    }


    public void setInputTypeAccordingToType(String type) {
        if (type.equals("string")) {
            editText.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES);
        } else if (type.equals("integer")) {
            editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        } else if (type.equals("double")) {
            editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

    public void setHint(String name) {
        editText.setHint(name);
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
