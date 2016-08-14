package com.nv95.fbchatnew.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nv95 on 11.08.16.
 */

public class Rooms extends ArrayList<Rooms.Item> {

    public static class Item {

        public final String name;
        public final String about;
        public final String topic;
        public final int users;

        public Item(JSONObject jo) throws JSONException {
            name = jo.getString("name");
            about = jo.getString("about");
            topic = jo.getString("topic");
            users = jo.getInt("count_users");
        }
    }

    @Nullable
    public Item getSafe(int pos) {
        if (pos >= 0 && pos < size()) {
            return get(pos);
        } else {
            return null;
        }
    }

    public int indexOf(@NonNull String name) {
        for (int i=0;i<size();i++) {
            if (name.equals(get(i).name)) {
                return i;
            }
        }
        return -1;
    }
}
