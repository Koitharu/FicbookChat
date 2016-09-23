package com.nv95.fbchat.core.ficbook;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by nv95 on 12.08.16.
 */

public class FicbookConnection {

    public static String post(String url, String... data){
        try {
            HttpURLConnection con = (HttpURLConnection)new URL("https://ficbook.net" + url).openConnection();
            con.setConnectTimeout(15000);
            con.setRequestProperty("Accept","application/json");
            con.setRequestMethod("POST");
            con.setDoInput(true);
            if (data!=null) {
                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                String query = "";
                for (int i = 0; i < data.length; i = i + 2) {
                    query += URLEncoder.encode(data[i], "UTF-8") + "=" + URLEncoder.encode(data[i + 1], "UTF-8");
                    query += "&";
                }
                if (query.length()>1) query = query.substring(0, query.length() - 1);
                out.writeBytes(query);
                out.flush();
                out.close();
            }
            if (con.getResponseCode()!=HttpURLConnection.HTTP_OK)return "";
            String res ="";
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null) {
                res=res+decodedString;
            }
            in.close();
            return res;
        } catch (IOException e){
            return "";
        }
    }

    public static String fixUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        try {
            String s;
            int _p = url.lastIndexOf("/")+1;
            s=url.substring(_p);
            s= URLEncoder.encode(s, "UTF-8");
            s = s.replace("+","%20");
            return url.substring(0,_p)+s;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isAvailable() && ni.isConnected();
    }
}
