package com.nv95.fbchat.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by nv95 on 08.08.16.
 */

public class LayoutUtils {

    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE);
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static int getScreenSize(Context context) {
        return context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    }

    public static int DpToPx(Resources res, float dp) {
        float density = res.getDisplayMetrics().density;
        return (int) (dp * density  + 0.5f);
    }

    public static Pair<Integer,Integer> getOptimalColumnCountAndWidth(Resources resources, int columnWidth, int padding) {
        float width = resources.getDisplayMetrics().widthPixels - padding;
        float modW = width % columnWidth;
        int count = (int) (width / columnWidth);
        if (modW > columnWidth/2) {
            count++;
        }
        if (count == 0) {
            count = 1;
        }
        return new Pair<>(count, (int)(width / count));
    }

    public static int getOptimalColumnWidth(Resources resources, int columnWidth, int padding) {
        return getOptimalColumnCountAndWidth(resources, columnWidth, padding).second;
    }


    public static int getOptimalColumnsCount(Resources resources, int columnWidth, int padding) {
        return getOptimalColumnCountAndWidth(resources, columnWidth, padding).first;
    }

    public static boolean isTabletLandscape(Context context) {
        return isTablet(context) && isLandscape(context);
    }

    //------recyclerview<

    public static int getItemCount(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        return layoutManager == null ? 0 : layoutManager.getItemCount();
    }

    public static int findLastCompletelyVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        final View child = findOneVisibleChild(layoutManager, layoutManager.getChildCount() - 1, -1, true, false);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    public static int findLastVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        final View child = findOneVisibleChild(layoutManager, layoutManager.getChildCount() - 1, -1, false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    public static int findFirstVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        final View child = findOneVisibleChild(layoutManager, 0, layoutManager.getChildCount(), false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    public static int findFirstCompletelyVisibleItemPosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        final View child = findOneVisibleChild(layoutManager, 0, layoutManager.getChildCount(), true, false);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    private static View findOneVisibleChild(RecyclerView.LayoutManager layoutManager, int fromIndex, int toIndex,
                                            boolean completelyVisible, boolean acceptPartiallyVisible) {
        OrientationHelper helper;
        if (layoutManager.canScrollVertically()) {
            helper = OrientationHelper.createVerticalHelper(layoutManager);
        } else {
            helper = OrientationHelper.createHorizontalHelper(layoutManager);
        }

        final int start = helper.getStartAfterPadding();
        final int end = helper.getEndAfterPadding();
        final int next = toIndex > fromIndex ? 1 : -1;
        View partiallyVisible = null;
        for (int i = fromIndex; i != toIndex; i += next) {
            final View child = layoutManager.getChildAt(i);
            final int childStart = helper.getDecoratedStart(child);
            final int childEnd = helper.getDecoratedEnd(child);
            if (childStart < end && childEnd > start) {
                if (completelyVisible) {
                    if (childStart >= start && childEnd <= end) {
                        return child;
                    } else if (acceptPartiallyVisible && partiallyVisible == null) {
                        partiallyVisible = child;
                    }
                } else {
                    return child;
                }
            }
        }
        return partiallyVisible;
    }

    public static int gravityOf(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof FrameLayout.LayoutParams) {
            return ((FrameLayout.LayoutParams) lp).gravity;
        } else {
            return Gravity.NO_GRAVITY;
        }
    }
}
