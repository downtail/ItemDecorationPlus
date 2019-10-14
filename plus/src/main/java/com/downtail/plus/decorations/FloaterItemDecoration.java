package com.downtail.plus.decorations;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.downtail.plus.FloaterView;
import com.downtail.plus.extensions.FloaterExtension;

public class FloaterItemDecoration extends RecyclerView.ItemDecoration {

    private FloaterExtension extension;
    private FloaterView floaterView;

    public FloaterItemDecoration(FloaterExtension extension,FloaterView floaterView) {
        this.extension = extension;
        this.floaterView=floaterView;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int mTop = parent.getTop() + parent.getPaddingTop();
        int mLeft = parent.getLeft() + parent.getPaddingLeft();

        int childCount = parent.getChildCount();
        int cachePosition = -1;
        View child = parent.getChildAt(0);
        int position = parent.getChildAdapterPosition(child);
        if (extension.isFloaterView(position)) {
            cachePosition = position;
        } else {
            cachePosition = getPreviousFloaterPosition(position);
        }
        if (cachePosition != -1) {
            View lastChild = parent.getChildAt(childCount - 1);
            int lastPosition = parent.getChildAdapterPosition(lastChild);
            int nextFloaterPosition = getNextFloaterPosition(position + 1, lastPosition);
            View nextFloaterView = getNextFloaterView(parent, nextFloaterPosition);
            if (nextFloaterView != null) {
                floaterView.setFloaterView(extension.getItemType(cachePosition),cachePosition);
            }else {
                floaterView.hideFloaterView();
            }
        }else {
            floaterView.hideFloaterView();
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
        for (int i = 1; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(child) == position) {
                return child;
            }
        }
        return null;
    }
}
