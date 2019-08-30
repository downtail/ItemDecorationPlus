package com.downtail.plus.decorations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.downtail.plus.extensions.StickyExtension;
import com.downtail.plus.utils.BitmapLoader;

public class StickyItemDecoration extends RecyclerView.ItemDecoration {

    private StickyExtension extension;

    private BitmapLoader loader;

    private RecyclerView mRecyclerView;
    private int mOrientation;
    private int mTop;
    private int mLeft;
    private int mCachePosition;

    public StickyItemDecoration(StickyExtension extension) {
        this.extension = extension;
        this.loader = new BitmapLoader();
        this.mCachePosition = -1;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (mRecyclerView == null || mRecyclerView != parent) {
            mRecyclerView = parent;
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                mOrientation = ((LinearLayoutManager) layoutManager).getOrientation();
            }
            mTop = mRecyclerView.getTop() + mRecyclerView.getPaddingTop();
            mLeft = mRecyclerView.getLeft() + mRecyclerView.getPaddingLeft();
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (extension != null) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                int top = child.getTop();
                int left = child.getLeft();

                if (top < mTop) {
                    if (extension.isStickyItem(position)){

                    }else {

                    }
                } else {
                    if (extension.isStickyItem(position)){

                    }else {

                    }
                }

                if (extension.isStickyItem(position)) {
                    cacheBitmap(child, position);
                    if (top < mTop) {
                        mCachePosition = position;
                    } else {
                        int lastStickyPosition = getLastStickyPosition(position);
                        if (lastStickyPosition != -1) {
                            mCachePosition = lastStickyPosition;
                        } else {
                            mCachePosition = lastStickyPosition;
                            break;
                        }
                    }
                } else {

                }
            }
        }
    }

    private void cacheBitmap(View view, int position) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        loader.addBitmap(String.valueOf(position), bitmap);
    }

    private int getLastStickyPosition(int position) {
        if (extension != null) {
            for (int i = position; i >= 0; i--) {
                if (extension.isStickyItem(i)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
