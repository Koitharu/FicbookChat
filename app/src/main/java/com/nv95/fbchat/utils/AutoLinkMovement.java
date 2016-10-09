package com.nv95.fbchat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.nv95.fbchat.ImageOpenTask;
import com.nv95.fbchat.ImageViewActivity;
import com.nv95.fbchat.R;

/**
 * Created by nv95 on 15.08.16.
 */

public class AutoLinkMovement extends LinkMovementMethod {

    private static AutoLinkMovement instance = new AutoLinkMovement();

    public static AutoLinkMovement getInstance() {
        return instance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            String title;
            try{title = buffer.subSequence(buffer.getSpanStart(link[0]),buffer.getSpanEnd(link[0])).toString();}catch (Exception e){title = "";}
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    processLink(widget.getContext(), link[0].getURL());
                    Selection.removeSelection(buffer);
                    Touch.onTouchEvent(widget, buffer, event);
                } else {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
                Touch.onTouchEvent(widget, buffer, event);
                return false;
            }
        }
        return Touch.onTouchEvent(widget, buffer, event);

    }

    private void processLink(Context context, String url) {
        if (ImageOpenTask.isImageUrl(url)) {
            ImageViewActivity.show(context, url);
            //new ImageOpenTask(context, url).start();
        } else {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception e) {
                Toast.makeText(context, R.string.unable_open_link, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
