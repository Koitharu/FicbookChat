package com.nv95.fbchatnew;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nv95.fbchatnew.components.BubbleDrawable;
import com.nv95.fbchatnew.utils.LayoutUtils;
import com.nv95.fbchatnew.utils.Palette;

/**
 * Created by nv95 on 08.08.16.
 */

public class ChatApp extends Application {

    public static final String CHAT_URL = "ws://146.120.111.42:7070";
    private static Palette mAppPalette;
    private static boolean mIsDark;

    public static boolean isDark() {
        return mIsDark;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mIsDark = prefs.getBoolean("dark",false);
        int color = prefs.getInt("color", -1);
        if (color == -1) {
            color = 217;
        }
        mAppPalette = Palette.fromValue(color);
        BubbleDrawable.pointerSize = LayoutUtils.DpToPx(getResources(), 18);
        initImageLoader(this);
    }

    public static Palette getApplicationPalette() {
        return mAppPalette;
    }

    public static ImageLoader initImageLoader(Context c) {
        if (ImageLoader.getInstance().isInited())
            return ImageLoader.getInstance();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(c)
                .defaultDisplayImageOptions(getImageLoaderOptions())
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
                .build();

        ImageLoader.getInstance().init(config);
        return ImageLoader.getInstance();
    }

    public static DisplayImageOptions getImageLoaderOptions() {
        return getImageLoaderOptionsBuilder().build();
    }

    public static DisplayImageOptions.Builder getImageLoaderOptionsBuilder() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(false)
                .displayer(new FadeInBitmapDisplayer(400, true, true, false));
    }
}
