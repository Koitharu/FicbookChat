package com.nv95.fbchatnew.components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nv95.fbchatnew.ChatApp;
import com.nv95.fbchatnew.R;
import com.nv95.fbchatnew.utils.LayoutUtils;
import com.nv95.fbchatnew.utils.ThemeUtils;

import java.util.ArrayList;

/**
 * Created by nv95 on 10.08.16.
 */

public abstract class EndlessHeaderedAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean mLoading = false;
    private boolean mLoadEnabled = false;
    private OnLoadMoreListener onLoadMoreListener;
    private int mHeaders = 0;
    private ArrayList<View> mHeaderViews = new ArrayList<>();

    public EndlessHeaderedAdapter(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = LayoutUtils.getItemCount(recyclerView);
                lastVisibleItem = LayoutUtils.findLastVisibleItemPosition(recyclerView);
                if (!mLoading && isLoadEnabled() && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // End has been reached
                    // Do something
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    mLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mHeaders) {
            return position;
        } else if (position == mHeaders + getDataItemCount()) {
            return mHeaders;    //progress footer
        } else {
            return mHeaders + 1 + getDataItemType(position - mHeaders);
        }
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < mHeaders) {
            return new HeaderHolder(mHeaderViews.get(viewType));
        } else if (viewType == mHeaders) {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.footer_loading, parent, false));
        } else {
            return onCreateDataViewHolder(parent, viewType - mHeaders - 1);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            //do nothing
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).setVisible(isLoadEnabled());
        } else {
            onBindDataViewHolder((VH) holder, position - mHeaders);
        }
    }

    public void notifyItemsAppended(int count) {
        notifyItemRangeInserted(mHeaders + getDataItemCount() - count, count);
    }

    public void setLoaded() {
        mLoading = false;
    }

    @Override
    public int getItemCount() {
        return mHeaders + getDataItemCount() + 1;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            ThemeUtils.paintView(v, ChatApp.getApplicationPalette());
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }

        public void setVisible(boolean visible) {
            progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private boolean isLoadEnabled() {
        return mLoadEnabled && getDataItemCount() != 0;
    }

    public void setLoadEnabled(boolean enabled) {
        mLoadEnabled = enabled;
        notifyItemChanged(mHeaders + getDataItemCount());
    }

    public abstract int getDataItemCount();

    public int getDataItemType(int position) {
        return 0;
    }

    public abstract VH onCreateDataViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindDataViewHolder(VH holder, int position);

    public int getHeadersCount() {
        return mHeaders;
    }

    public void addHeader(View header, int position) {
        mHeaderViews.add(position, header);
        mHeaders++;
        notifyItemInserted(position);
    }

    private static class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }
}