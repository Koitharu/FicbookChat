package com.nv95.fbchat.utils;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.nv95.fbchat.R;

/**
 * Created by nv95 on 11.08.16.
 */

public class CloseHelper {

    @Nullable
    private static Snackbar mSnackbar = null;

    public static boolean tryClose(View view) {
        if (mSnackbar != null) {
            mSnackbar = null;
            return true;
        } else {
            mSnackbar = Snackbar.make(view, R.string.press_again_to_exit, Snackbar.LENGTH_SHORT);
            mSnackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    mSnackbar = null;
                    super.onDismissed(snackbar, event);
                }
            });
            mSnackbar.show();
            return false;
        }
    }
}
