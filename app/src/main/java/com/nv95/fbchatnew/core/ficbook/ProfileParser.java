package com.nv95.fbchatnew.core.ficbook;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.Html;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nv95 on 16.08.16.
 */


public class ProfileParser {

    @Nullable
    private final Element mRoot;
    private final String mSourceLink;

    @WorkerThread
    public ProfileParser(String linkToProfile) {
        mSourceLink = linkToProfile;
        Document doc = null;
        try {
            HttpURLConnection con = (HttpURLConnection)new URL(linkToProfile).openConnection();
            con.setConnectTimeout(15000);
            doc = Jsoup.parse(con.getInputStream(), "UTF-8", linkToProfile);
        } catch (Exception e) {
            doc = null;
        }
        mRoot = doc == null ? null : doc.select("div.article-holder").first();
    }

    private ProfileParser() {
        mSourceLink = null;
        mRoot = null;
    }

    @WorkerThread
    public static ProfileParser fromName(String username) {
        JSONObject jo = null;
        try {
            jo = new JSONObject(FicbookConnection.post("/ajax/user_info", "nickname", username));
            String s = jo.getString("link_to_profile");
            return new ProfileParser("https://ficbook.net" + s);
        } catch (JSONException e) {
            e.printStackTrace();
            return new ProfileParser();
        }
    }

    public boolean isSuccess() {
        return mRoot != null;
    }

    public String getProfileLink() {
        return mSourceLink;
    }

    @Nullable
    public Spanned getUserInfo() {
        if (mRoot == null) {
            return null;
        }
        Element e = mRoot.select("section.profile-section").first();
        if (e == null) {
            return null;
        }
        e = e.children().last();
        if (e == null) {
            return null;
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(e.html(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(e.html());
        }
    }
}
