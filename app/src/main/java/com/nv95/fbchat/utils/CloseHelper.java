package com.nv95.fbchat.utils;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nv95.fbchat.R;

/**
 * Created by nv95 on 11.08.16.
 */

public class CloseHelper {

    @Nullable
    private static Snackbar mSnackbar = null;

    public static boolean tryClose(final View view) {
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
            if (view instanceof RecyclerView && LayoutUtils.findFirstVisibleItemPosition((RecyclerView) view) > 2) {
                mSnackbar.setAction(R.string.down, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((RecyclerView)view).scrollToPosition(0);
                    }
                });
            }
            mSnackbar.show();
            return false;
        }
    }
}
