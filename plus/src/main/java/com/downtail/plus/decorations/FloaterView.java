package com.downtail.plus.decorations;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FloaterView extends FrameLayout {

    private OnItemChildClickListener onItemChildClickListener;
    private OnItemClickListener onItemClickListener;
    private OnBindViewListener onBindViewListener;
    private SparseArray<View> cacheViews = new SparseArray<>();
    private int position = -1;

    private FloaterView(Context context) {
        this(context, null);
    }

    private FloaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFloaterView(@IntRange(from = 0, to = Integer.MAX_VALUE) int itemType, int cachePosition, int mOrientation, int mTop, int mLeft, boolean incompatible, int criticalValue) {
        int size = cacheViews.size();
        for (int i = 0; i < size; i++) {
            int key = cacheViews.keyAt(i);
            View view = cacheViews.valueAt(i);
            if (key == itemType) {
                if (position != cachePosition) {
                    position = cachePosition;
                    if (onBindViewListener != null) {
                        onBindViewListener.onBind(view, position);
                    }
                }
                if (mOrientation == LinearLayoutManager.VERTICAL) {
                    if (incompatible) {
                        if (view.getBottom() > criticalValue) {
                            view.setTranslationY(mTop - view.getBottom() + criticalValue);
                        } else {
                            view.setTranslationY(mTop);
                        }
                    } else {
                        view.setTranslationY(mTop);
                    }
                } else if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                    if (incompatible) {
                        if (view.getRight() > criticalValue) {
                            view.setTranslationX(mLeft - view.getRight() + criticalValue);
                        } else {
                            view.setTranslationX(mLeft);
                        }
                    } else {
                        view.setTranslationX(mLeft);
                    }
                }
                view.setVisibility(VISIBLE);
            } else {
                view.setVisibility(GONE);
            }
        }
    }

    public void hideFloaterView() {
        int size = cacheViews.size();
        for (int i = 0; i < size; i++) {
            View view = cacheViews.valueAt(i);
            view.setVisibility(GONE);
        }
    }

    public FloaterView addItemType(@IntRange(from = 0, to = Integer.MAX_VALUE) int itemType, @LayoutRes int itemLayout) {
        return addItemType(itemType, itemLayout, NO_ID);
    }

    public FloaterView addItemType(@IntRange(from = 0, to = Integer.MAX_VALUE) int itemType, @LayoutRes int itemLayout, @IdRes int... viewIds) {
        View view = cacheViews.get(itemType);
        if (view == null) {
            if (itemLayout == 0) {
                throw new IllegalArgumentException("layout id can't be zero");
            }
            view = LayoutInflater.from(this.getContext()).inflate(itemLayout, this, false);
            addView(view, getChildCount());
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, position);
                    }
                }
            });
            for (int viewId : viewIds) {
                if (viewId != NO_ID) {
                    View child = view.findViewById(viewId);
                    if (child != null) {
                        if (!child.isClickable()) {
                            child.setClickable(true);
                        }
                        child.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (onItemChildClickListener != null) {
                                    onItemChildClickListener.onItemChildClick(v, position);
                                }
                            }
                        });
                    }
                }
            }
            view.setVisibility(GONE);
            cacheViews.put(itemType, view);
        }
        return this;
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
        parent.removeView(recyclerView);
        FloaterView floaterView = new FloaterView(recyclerView.getContext());
        recyclerView.setClipToPadding(false);
        floaterView.addView(recyclerView, 0);
        parent.addView(floaterView, index, layoutParams);
        return floaterView;
    }

    public FloaterView setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
        return this;
    }

    public FloaterView setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public FloaterView setOnBindViewListener(OnBindViewListener onBindViewListener) {
        this.onBindViewListener = onBindViewListener;
        return this;
    }

    public interface OnItemChildClickListener {
        void onItemChildClick(View view, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnBindViewListener {
        void onBind(View view, int position);
    }
}
