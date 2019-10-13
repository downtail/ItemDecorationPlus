package com.downtail.plus;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FloaterView extends FrameLayout {

    private SparseIntArray resources = new SparseIntArray();
    private SparseArray<View> cacheViews = new SparseArray<>();

    public FloaterView(Context context) {
        this(context, null);
    }

    public FloaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addItemType(@IntRange(from = 0, to = Integer.MAX_VALUE) int itemType, @LayoutRes int itemLayout) {
        resources.put(itemType, itemLayout);
    }

    public View getItemView(@IntRange(from = 0, to = Integer.MAX_VALUE) int itemType) {
        View view = cacheViews.get(itemType);
        if (view == null) {
            int itemLayout = resources.get(itemType);
            if (itemLayout == 0) {
                throw new IllegalArgumentException("layout id can't be zero");
            }
            view = View.inflate(getContext(), itemLayout, null);
            cacheViews.put(itemType, view);
        }
        return view;
    }

    public static FloaterView init(RecyclerView recyclerView) {
        if (recyclerView == null) {
            throw new NullPointerException("RecyclerView cant't be null");
        }
        ViewGroup parent = (ViewGroup) recyclerView.getParent();
        if (parent == null) {
            throw new NullPointerException("ParentView can't be null");
        }
        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        int index = parent.indexOfChild(recyclerView);
        FloaterView floaterView = new FloaterView(recyclerView.getContext());
        parent.removeView(recyclerView);
        floaterView.addView(recyclerView, 0);
        parent.addView(floaterView, index, layoutParams);
        return floaterView;
    }
}
