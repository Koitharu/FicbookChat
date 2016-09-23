package com.nv95.fbchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by nv95 on 23.09.16.
 */

public class ImageOpenTask extends AsyncTask<String,Integer,File> implements DialogInterface.OnCancelListener {

    private static final Executor SERIAL_EXECUTOR = Executors.newSingleThreadExecutor();

    private final String mUrl;
    private final ProgressDialog mProgressDialog;
    private final File mDir;

    public ImageOpenTask(Context context,String url) {
        mUrl = url;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(context.getString(R.string.loading_image));
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgressDialog.cancel();
            }
        });
        mProgressDialog.setOnCancelListener(this);
        mDir = getTempImageDir(context);
    }

    public void start() {
        executeOnExecutor(SERIAL_EXECUTOR, mUrl);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.show();
    }

    @Override
    protected File doInBackground(String... params) {
        if (mDir == null) {
            return null;
        }
        File dest = null;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            dest = new File(mDir, params[0].hashCode() + "." + fileExtFromUrl(params[0]));
            if (dest.exists()) {
                return dest;
            }
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            input = connection.getInputStream();
            output = new FileOutputStream(dest);
            int total = connection.getContentLength();
            int done = 0;
            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                done += count;
                if (isCancelled()) {
                    throw new InterruptedException();
                } else {
                    publishProgress(total, done);
                }
            }
        } catch (Exception e) {
            if (dest != null && dest.exists()) {
                dest.delete();
            }
            dest = null;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }
        return dest;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values.length == 2 && values[0] != 0) {
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(values[0]);
            mProgressDialog.setProgress(values[1]);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mProgressDialog.dismiss();
    }

    @Override
    protected void onPostExecute(File file) {
        Context context = mProgressDialog.getContext();
        super.onPostExecute(file);
        mProgressDialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (file == null) {
            intent.setData(Uri.parse(mUrl));
        } else {
            intent.setDataAndType(Uri.fromFile(file), "image/*");
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.unable_open_link, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    public static File getTempImageDir(Context context) {
        File f = context.getExternalCacheDir();
        if (f == null) {
            f = context.getCacheDir();
        }
        if (f == null) {
            return null;
        }
        f = new File(f, "img");
        if (f.exists() || f.mkdir()) {
            return f;
        } else {
            return null;
        }
    }

    public static String fileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/')+1, url.length());
    }

    public static String fileExtFromUrl(String url) {
        return url.substring(url.lastIndexOf('.')+1, url.length());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        this.cancel(false);
    }

    public static boolean isImageUrl(String url) {
        try {
            String ext = fileExtFromUrl(url);
            ext = ext.toLowerCase();
            return ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("bmp");
        } catch (Exception e) {
            return false;
        }
    }
}
