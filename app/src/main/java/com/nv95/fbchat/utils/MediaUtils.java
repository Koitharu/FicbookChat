package com.nv95.fbchat.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by nv95 on 20.08.16.
 */

public class MediaUtils {

    @NonNull
    public static String getImageFile(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String filePath = "";
        Cursor cursor = context.getContentResolver().query(
                uri, filePathColumn, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    public File getUriFile(Uri uri) {
        return new File(uri.getPath());
    }

    public static void cleanDir(File f) {
        File[] lst = f.listFiles();
        if (lst == null) {
            return;
        }
        for (File o : lst) {
            if (o.isDirectory()) {
                cleanDir(o);
            } else {
                o.delete();
            }
        }
    }
}
