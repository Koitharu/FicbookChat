package com.nv95.fbchat.utils;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nv95.fbchat.ChatApp;
import com.nv95.fbchat.R;
import com.nv95.fbchat.core.ficbook.FicbookConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by nv95 on 12.08.16.
 */

public class AvatarUtils {

    private static DisplayImageOptions mOptions = null;
    private static HashMap<String,String> mCache = new HashMap<>();
    private static Executor mExecutor = Executors.newFixedThreadPool(3);

    @WorkerThread
    public static String getAvatarUrl(String nickname) {
        if (mCache.containsKey(nickname)) {
            return mCache.get(nickname);
        }
        try {
            JSONObject jo = new JSONObject(FicbookConnection.post("/ajax/user_info", "nickname", nickname));
            String s = jo.getString("avatar_path");
            s = FicbookConnection.fixUrl(s);
            mCache.put(nickname, s);
            return s;
        } catch (JSONException e) {
            return "";
        }
    }

    @MainThread
    public static void assignAvatarTo(ImageView imageView, String nickname) {
        imageView.setImageResource(R.drawable.ic_avatar_holder);
        new LoadAvatarTask(imageView).executeOnExecutor(mExecutor, nickname);
    }

    public static void clearLinksCache() {
        mCache.clear();
    }

    private static class LoadAvatarTask extends AsyncTask<String,Void,String> {

        private final ImageView mImageView;

        private LoadAvatarTask(ImageView imageView) {
            mImageView = imageView;
            if (mOptions == null) {
                mOptions = ChatApp.getImageLoaderOptionsBuilder()
                        .showImageForEmptyUri(R.drawable.ic_avatar_holder)
                        .showImageOnFail(R.drawable.ic_avatar_holder)
                        .showImageOnLoading(R.drawable.ic_avatar_holder)
                        .displayer(new CircleBitmapDisplayer(Color.WHITE, LayoutUtils.DpToPx(imageView.getResources(), 0.5f)))
                        .build();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            return getAvatarUrl(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ImageLoader.getInstance().displayImage(s, mImageView, mOptions);
        }
    }
}
