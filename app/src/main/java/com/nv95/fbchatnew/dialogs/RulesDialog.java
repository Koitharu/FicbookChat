package com.nv95.fbchatnew.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.nv95.fbchatnew.R;

/**
 * Created by nv95 on 14.08.16.
 */

public class RulesDialog {

    public static void show(Context context, DialogInterface.OnClickListener clickListener) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(R.string.rules)
                .setPositiveButton(R.string.accept, clickListener)
                .setNegativeButton(R.string.dismiss, null)
                .create()
                .show();
    }
}
