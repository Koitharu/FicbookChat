package com.nv95.fbchatnew.utils;

import android.graphics.Bitmap;

/**
 * Created by nv95 on 08.08.16.
 */

public class MosaicBitmap {

    private final Bitmap[] mBitmaps;
    private final int mRows, mCols;
    private final int mItemHeight, mItemWidth;

    public MosaicBitmap(Bitmap source, int columns, int rows) {
        mRows = rows;
        mCols = columns;
        mItemHeight = source.getHeight() / columns;
        mItemWidth = source.getWidth() / rows;

        mBitmaps = new Bitmap[mRows * mCols];

        int xCoord = 0;
        for(int y=0; y<mCols; y++){
            int yCoord = 0;
            for(int x=0; x<mRows; x++){
                mBitmaps[y*mRows+x] = Bitmap.createBitmap(source, xCoord, yCoord, mItemWidth, mItemHeight);
                yCoord += mItemHeight;
            }
            xCoord += mItemWidth;
        }
    }

    public Bitmap get(int index) {
        return mBitmaps[index];
    }

    public int getCount() {
        return mBitmaps.length;
    }

    public int getRowCount() {
        return mRows;
    }

    public int getColumnCount() {
        return mCols;
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public int getItemWidth() {
        return mItemWidth;
    }
}
