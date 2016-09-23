package com.nv95.fbchat.utils;

import android.util.Log;

import java.io.File;

/**
 * Created by nv95 on 23.09.16.
 */

public class StorageUtils {

    public static long dirSize(File dir) {
        if (!dir.exists()) {
            return 0;
        }
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                size += file.length();
            } else
                size += dirSize(file);
        }
        return size;
    }

    public static void removeDir(File dir) {
        if (dir == null || !dir.exists()) {
            return;
        }
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File o : files) {
                    if (o.isDirectory()) {
                        removeDir(o);
                    } else {
                        o.delete();
                    }
                }
            }
        }
        dir.delete();
    }
}
