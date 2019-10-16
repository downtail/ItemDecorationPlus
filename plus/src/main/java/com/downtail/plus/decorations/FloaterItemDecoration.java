package com.downtail.plus.decorations;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.downtail.plus.extensions.FloaterExtension;

public class FloaterItemDecoration extends RecyclerView.ItemDecoration {

    private FloaterExtension extension;
    private FloaterView floaterView;

    public FloaterItemDecoration(FloaterExtension extension, FloaterView floaterView) {
        this.extension = extension;
        this.floaterView = floaterView;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int mTop = parent.getTop();
        int mLeft = parent.getLeft();
//        int mTop = parent.getTop() + parent.getPaddingTop();
//        int mLeft = parent.getLeft() + parent.getPaddingLeft();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        int childCount = parent.getChildCount();
        if (childCount > 0 && layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            int cachePosition;
            View child = parent.getChildAt(0);
            int position = parent.getChildAdapterPosition(child);
            if (extension.isFloaterView(position)) {
                cachePosition = position;
            } else {
                cachePosition = getPreviousFloaterPosition(position - 1);
            }
            if (cachePosition != -1) {
                View lastChild = parent.getChildAt(childCount - 1);
                int lastPosition = parent.getChildAdapterPosition(lastChild);
                int nextFloaterPosition = getNextFloaterPosition(position + 1, lastPosition);
                if (nextFloaterPosition != -1) {
                    View nextFloaterView = getNextFloaterView(parent, nextFloaterPosition);
                    if (nextFloaterView != null) {
                        if (orientation == LinearLayoutManager.VERTICAL) {
                            floaterView.setFloaterView(extension.getItemType(cachePosition), cachePosition, orientation, mTop, mLeft, true, nextFloaterView.getTop());
                        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                            floaterView.setFloaterView(extension.getItemType(cachePosition), cachePosition, orientation, mTop, mLeft, true, nextFloaterView.getLeft());
                        }
                    } else {
                        floaterView.setFloaterView(extension.getItemType(cachePosition), cachePosition, orientation, mTop, mLeft, false, 0);
                    }
                } else {
                    floaterView.setFloaterView(extension.getItemType(cachePosition), cachePosition, orientation, mTop, mLeft, false, 0);
                }
            } else {
                floaterView.hideFloaterView();
            }
        }
    }

    private int getPreviousFloaterPosition(int position) {
        if (extension != null) {
            for (int i = position; i >= 0; i--) {
                if (extension.isFloaterView(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getNextFloaterPosition(int start, int end) {
        if (extension != null) {
            for (int i = start; i <= end; i++) {
                if (extension.isFloaterView(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private View getNextFloaterView(RecyclerView parent, int position) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);
            if (childPosition > position) {
                break;
            } else if (childPosition == position) {
                return child;
            }
        }
        return null;
    }
}
