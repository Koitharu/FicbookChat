package com.nv95.fbchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv95.fbchat.core.ficbook.FicbookConnection;
import com.nv95.fbchat.utils.AvatarUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by nv95 on 29.11.15.
 */
public class UsersAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList;

    public UsersAdapter(Context context) {
        super(context, R.layout.item_user_list);
    }

    @Override
    public int getCount() {
        return (resultList != null ? resultList.size() : 0);
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_list, parent, false);
        }
        String item = getItem(position);
        ((TextView)v.findViewById(android.R.id.text1)).setText(item);
        AvatarUtils.assignAvatarTo((ImageView) v.findViewById(R.id.imageViewAvatar), item);
        return v;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            JSONArray a = new JSONArray(FicbookConnection.post("/ajax/author_ac?term=" + URLEncoder.encode(input, "UTF-8"), "", ""));
            int n = a.length();
            if (n > 20) n = 10;
            JSONObject o;
            for (int i = 0; i < n; i++) {
                o = a.getJSONObject(i);
                list.add(o.getString("nickname"));
            }
        } catch (Exception ignored) {
        }
        return list;
    }
}