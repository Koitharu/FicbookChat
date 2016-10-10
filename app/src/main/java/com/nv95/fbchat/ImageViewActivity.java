package com.nv95.fbchat;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nv95.fbchat.components.SwipeToCloseLayout;
import com.nv95.fbchat.utils.LayoutUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nv95 on 31.10.15.
 */
public class ImageViewActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private SwipeToCloseLayout mContainer;
    private String mUrl;
    private TextView mTextView;
    private View mProgressBlock;
    private ProgressBar mProgressBar;
    private SubsamplingScaleImageView mSsiv;
    @Nullable
    private ImageLoadTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        setSupportActionBar(mToolbar = (Toolbar) findViewById(R.id.toolbar));
        enableTransparentStatusBar(android.R.color.transparent);
        enableHomeAsUp();
        mContainer = (SwipeToCloseLayout) findViewById(R.id.container);
        mProgressBlock = findViewById(R.id.progressBlock);
        mTextView = (TextView) findViewById(R.id.textView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSsiv = (SubsamplingScaleImageView) findViewById(R.id.subsamplingImageView);
        mUrl = getIntent().getStringExtra("url");
        mContainer.setSwipeTouchListener(new SwipeCloseListener());
        mSsiv.setOnClickListener(this);
        mTask = new ImageLoadTask(mUrl);
        mTask.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        if (mTask != null) {
            mTask.cancel(false);
            mTask = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveImage(new File(getTempImageDir(ImageViewActivity.this), mUrl.hashCode() + "." + fileExtFromUrl(mUrl)));
                return true;
            case R.id.action_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mUrl));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.unable_open_link, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static boolean show(Context context, String url) {
        if (!isImageUrl(url)) {
            return false;
        }
        Intent intent = new Intent(context, ImageViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
        return true;
    }

    private boolean mToolbarVisible = false;

    @Override
    public void onClick(View v) {
        if (mToolbarVisible) {
            mToolbarVisible = false;
            mToolbar.setVisibility(View.INVISIBLE);
        } else {
            mToolbarVisible = true;
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    private class SwipeCloseListener implements View.OnTouchListener, ValueAnimator.AnimatorUpdateListener {

        private final int MIN_DELTA = 80;
        private final int MAX_DELTA = 400;
        private float lasty = 0;
        private final TextView textViewHint;
        private View view;

        public SwipeCloseListener() {
            textViewHint = (TextView) findViewById(R.id.textView_hint);
        }

        public float FloatToBounds(float value, int min, int max) {
            if (value > max) {
                return max;
            } else if (value < min) {
                return min;
            } else {
                return value;
            }
        }

        private void update(float delta) {
            if (view == null) {
                return;
            }
            view.setTranslationY(-delta);
            delta = (delta - 100) / 400;
            textViewHint.setAlpha(FloatToBounds(delta, 0, 1));
            mToolbar.setAlpha(1 - FloatToBounds(delta, 0, 1));
            delta /= 4;
            mContainer.setBackgroundColor(Color.argb((int) ((1 - FloatToBounds(delta, 0, 1))*255),0,0,0));
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getPointerCount() != 1) {
                return false;
            }
            float dy = lasty - event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lasty = event.getY();
                    view = mSsiv;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (dy >= MIN_DELTA) {
                        update(dy - MIN_DELTA);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (dy >= MAX_DELTA) {
                        close(dy);
                        break;
                    }
                case MotionEvent.ACTION_CANCEL:
                    if (dy >= MIN_DELTA) {
                        ValueAnimator restorer = ValueAnimator.ofFloat(dy - MIN_DELTA, 0);
                        restorer.addUpdateListener(this);
                        restorer.setDuration(200);
                        restorer.start();
                    }
                    break;
            }
            return false;
        }

        public void close(float dy) {
            ValueAnimator closer = ValueAnimator.ofFloat(dy - 150, 2 * mContainer.getWidth());
            closer.addUpdateListener(this);
            closer.setDuration(200);
            closer.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mContainer.setVisibility(View.INVISIBLE);
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            closer.start();
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float val = (Float) animation.getAnimatedValue();
            update(val);
        }
    }

    private void enableTransparentStatusBar(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            final Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(ContextCompat.getColor(this, color));
        }
    }

    private void enableHomeAsUp() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private class ImageLoadTask extends AsyncTask<String,Integer,File> {

        private final String mUrl;
        private final File mDir;

        public ImageLoadTask(String url) {
            mUrl = url;
            mDir = getTempImageDir(ImageViewActivity.this);
        }

        public void start() {
            executeOnExecutor(THREAD_POOL_EXECUTOR, mUrl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBlock.setVisibility(View.VISIBLE);
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
                mTextView.setText(getString(R.string.loading_image_progress, values[1] * 100 / values[0]));
                mProgressBar.setIndeterminate(false);
                mProgressBar.setMax(values[0]);
                mProgressBar.setProgress(values[1]);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mTask = null;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            mTask = null;
            if (file != null) {
                mSsiv.setImage(ImageSource.uri(Uri.fromFile(file)).tilingEnabled());
                LayoutUtils.crossFade(mProgressBlock, mSsiv);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mTextView.setText(R.string.fail_load);
            }
        }
    }

    @Nullable
    static File getTempImageDir(Context context) {
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

    static String fileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/')+1, url.length());
    }

    static String fileExtFromUrl(String url) {
        return url.substring(url.lastIndexOf('.')+1, url.length());
    }

    private static boolean isImageUrl(String url) {
        try {
            String ext = fileExtFromUrl(url);
            ext = ext.toLowerCase();
            return ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("bmp");
        } catch (Exception e) {
            return false;
        }
    }

    private void saveImage(File file) {
        File dest = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), file.getName());
        try {
            InputStream in = new FileInputStream(file);
            OutputStream out = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            MediaScannerConnection.scanFile(ImageViewActivity.this,
                    new String[]{dest.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            //....
                        }
                    });
            Snackbar.make(mSsiv, R.string.image_saved, Snackbar.LENGTH_LONG)
                    .show();
        } catch (IOException e) {
            Snackbar.make(mSsiv, R.string.unable_open_link, Snackbar.LENGTH_SHORT).show();
        }
    }
}
