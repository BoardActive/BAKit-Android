package com.boardactive.bakit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class GeneratedDialog {
    Context context;
    String title;
    String description;
    String positiveButtonText;
    String negativeButtonText;
    DialogInterface.OnClickListener negativeListener;
    DialogInterface.OnClickListener positiveListener;

    public GeneratedDialog(Context context) {
        this.context = context;
    }

    public static GeneratedDialog with(Context context){
        return new GeneratedDialog(context);
    }

    public  GeneratedDialog setTitle(String title){
        this.title =  title;
        return this;
    }

    public GeneratedDialog setDescription(String description){
        this.description =  description;
        return this;
    }

    public GeneratedDialog setPositiveButtonText(String positiveButtonText){
        this.positiveButtonText =  positiveButtonText;
        return this;
    }

    public GeneratedDialog setNegativeButtonText(String negativeButtonText){
        this.negativeButtonText =  negativeButtonText;
        return this;
    }

    public GeneratedDialog setNegativeListener(DialogInterface.OnClickListener negativeListener){
        this.negativeListener =  negativeListener;
        return this;
    }

    public GeneratedDialog setPositiveListener(DialogInterface.OnClickListener positiveListener){
        this.positiveListener =  positiveListener;
        return this;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"NewApi", "WrongConstant"})
    public void showPermissionDialog(){
        TextView tvDescription = new TextView(this.context);
        tvDescription.setText(this.description);
        tvDescription.setPadding(65, 20, 65, 10);
        tvDescription.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        tvDescription.setTextColor(this.context.getResources().getColor(R.color.colorText));
        tvDescription.setTextSize(16);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(this.title);
        builder.setView(tvDescription);
        builder.setCancelable(false);
        //builder.setMessage(this.description);
        builder.setPositiveButton(this.positiveButtonText, this.positiveListener);
        builder.setNegativeButton(this.negativeButtonText, this.negativeListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

