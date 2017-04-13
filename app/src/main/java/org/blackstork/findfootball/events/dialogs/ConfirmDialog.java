package org.blackstork.findfootball.events.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by WiskiW on 13.04.2017.
 */

public class ConfirmDialog {

    private AlertDialog.Builder builder;
    private Context context;
    private String buttonText;
    private DialogInterface.OnClickListener completeClickListener;

    public ConfirmDialog(Context context) {
        this.context = context;
        builder = new AlertDialog.Builder(context);
    }

    public ConfirmDialog setTitle(String title) {
        builder.setTitle(title);
        builder.setNegativeButton(context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return this;
    }

    public ConfirmDialog setCompleteClickListener(DialogInterface.OnClickListener clickListener) {
        this.completeClickListener = clickListener;
        return this;
    }

    public ConfirmDialog setCompleteButtonText(String buttonText) {
        this.buttonText = buttonText;
        return this;
    }

    public AlertDialog.Builder getBuilder() {
        return builder;
    }

    public void build() {
        builder.setNeutralButton(buttonText, completeClickListener);
        builder.create().show();
    }


}
