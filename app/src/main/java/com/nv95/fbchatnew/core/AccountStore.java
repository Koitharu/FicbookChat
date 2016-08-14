package com.nv95.fbchatnew.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nv95 on 11.08.16.
 */

public class AccountStore {

    public static boolean isAuthorized(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("acc", Context.MODE_PRIVATE);
        return prefs.contains("pass");
    }


    public static void write(Context context, String login, String password) {
        context.getSharedPreferences("acc", Context.MODE_PRIVATE)
                .edit()
                .putString("login", login)
                .putString("pass", password)
                .apply();
    }

    public static String getLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("acc", Context.MODE_PRIVATE);
        return prefs.getString("login", "");
    }

    public static String getPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("acc", Context.MODE_PRIVATE);
        return prefs.getString("pass", "");
    }

    public static void clear(Context context) {
        context.getSharedPreferences("acc", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
