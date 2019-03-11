package com.downtail.plus.decorations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.downtail.plus.extensions.CosmeticExtension;

public class CosmeticItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 布局方向
     */
    private static final int ORIENTATION_HORIZONTAL = 0;
    private static final int ORIENTATION_VERTICAL = 1;
    private static final int ORIENTATION_NONE = -1;

    /**
     * 缓存position
     */
    private int cachePosition;

    /**
     * 拓展接口
     */
    private CosmeticExtension cosmeticExtension;

    /**
     * 实际的布局方向
     */
    private int orientation;

    /**
     * top基准线，通常对应为屏幕y=0
     */
    private int topDatumLine;

    /**
     * left基准线，通常对于为屏幕x=0
     */
    private int leftDatumLine;

    private CosmeticItemDecoration(CosmeticExtension cosmeticExtension) {
        this.cosmeticExtension = cosmeticExtension;
        this.cachePosition = -1;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (cosmeticExtension != null) {
            int position = parent.getChildAdapterPosition(view);
            if (cosmeticExtension.isCosmeticItem(position)) {
                View cosmeticView = cosmeticExtension.getCosmeticView(position);
                if (cosmeticView != null) {
                    int length = cosmeticExtension.getCosmeticHeight(position);
                    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                        int spanCount = gridLayoutManager.getSpanCount();
                        orientation = gridLayoutManager.getOrientation();
                        if (orientation == ORIENTATION_VERTICAL) {
                            measureAndLayout(cosmeticView, getValidWidth(parent) / spanCount, length);
                            outRect.top = cosmeticView.getHeight();
                        } else if (orientation == ORIENTATION_HORIZONTAL) {
                            measureAndLayout(cosmeticView, length, getValidHeight(parent) / spanCount);
                            outRect.left = cosmeticView.getWidth();
                        }
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        orientation = ((LinearLayoutManager) layoutManager).getOrientation();
                        if (orientation == ORIENTATION_VERTICAL) {
                            measureAndLayout(cosmeticView, getValidWidth(parent), length);
                            outRect.top = cosmeticView.getHeight();
                        } else if (orientation == ORIENTATION_HORIZONTAL) {
                            measureAndLayout(cosmeticView, length, getValidHeight(parent));
                            outRect.left = cosmeticView.getWidth();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (cosmeticExtension != null) {
            int top = parent.getTop() + parent.getPaddingTop();
            int left = parent.getLeft() + parent.getPaddingLeft();
            topDatumLine = top;
            leftDatumLine = left;

            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                orientation = ((GridLayoutManager) layoutManager).getOrientation();
            } else if (layoutManager instanceof LinearLayoutManager) {
                orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            } else {
                orientation = ORIENTATION_NONE;
            }

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                if (orientation == ORIENTATION_VERTICAL) {
                    int topDistance = child.getTop();
                    if (cachePosition != -1) {
                        if (cosmeticExtension.isCosmeticItem(position)) {
                            int distance = cosmeticExtension.getCosmeticHeight(position);
                            if (topDistance - distance <= top) {
                                cachePosition = position;
                            } else {
                                cachePosition = getLatestCosmeticPosition(position - 1);
                                if (cachePosition != -1) {
                                    int cacheHeight = cosmeticExtension.getCosmeticHeight(cachePosition);
                                    if (topDistance - distance <= top + cacheHeight) {
                                        topDatumLine = topDistance - distance - cacheHeight;
                                    }
                                }
                            }
                        }
                    } else {
                        if (cosmeticExtension.isCosmeticItem(position)) {
                            int distance = cosmeticExtension.getCosmeticHeight(position);
                            if (topDistance - distance <= top) {
                                cachePosition = position;
                            }
                        }
                    }
                    if (cosmeticExtension.isCosmeticItem(position) && cachePosition != position) {
                        View cosmeticView = cosmeticExtension.getCosmeticView(position);
                        if (cosmeticView != null) {
                            int length = cosmeticExtension.getCosmeticHeight(position);
                            if (layoutManager instanceof GridLayoutManager) {
                                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                                int spanCount = gridLayoutManager.getSpanCount();
                                measureAndLayout(cosmeticView, getValidWidth(parent) / spanCount, length);
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                measureAndLayout(cosmeticView, getValidWidth(parent), length);
                            }
                            Bitmap bitmap = Bitmap.createBitmap(cosmeticView.getWidth(), cosmeticView.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            canvas.drawColor(Color.WHITE);
                            cosmeticView.draw(canvas);
                            c.drawBitmap(bitmap, child.getLeft(), child.getTop() - cosmeticView.getHeight(), null);
                        }
                    }
                } else if (orientation == ORIENTATION_HORIZONTAL) {
                    int leftDistance = child.getLeft();
                    if (cachePosition != -1) {
                        if (cosmeticExtension.isCosmeticItem(position)) {
                            int distance = cosmeticExtension.getCosmeticHeight(position);
                            if (leftDistance - distance <= left) {
                                cachePosition = position;
                            } else {
                                cachePosition = getLatestCosmeticPosition(position - 1);
                                if (cachePosition != -1) {
                                    int cacheHeight = cosmeticExtension.getCosmeticHeight(cachePosition);
                                    if (leftDistance - distance <= left + cacheHeight) {
                                        leftDatumLine = leftDistance - distance - cacheHeight;
                                    }
                                }
                            }
                        }
                    } else {
                        if (cosmeticExtension.isCosmeticItem(position)) {
                            int distance = cosmeticExtension.getCosmeticHeight(position);
                            if (leftDistance - distance <= left) {
                                cachePosition = position;
                            }
                        }
                    }
                    if (cosmeticExtension.isCosmeticItem(position)) {
                        View cosmeticView = cosmeticExtension.getCosmeticView(position);
                        if (cosmeticView != null) {
                            int length = cosmeticExtension.getCosmeticHeight(position);
                            if (layoutManager instanceof GridLayoutManager) {
                                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                                int spanCount = gridLayoutManager.getSpanCount();
                                measureAndLayout(cosmeticView, length, getValidHeight(parent) / spanCount);
                                leftDatumLine = child.getLeft() - cosmeticView.getWidth();
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                measureAndLayout(cosmeticView, length, getValidHeight(parent));
                                leftDatumLine = child.getLeft() - cosmeticView.getWidth();
                            }
                            Bitmap bitmap = Bitmap.createBitmap(cosmeticView.getWidth(), cosmeticView.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            canvas.drawColor(Color.WHITE);
                            cosmeticView.draw(canvas);
                            c.drawBitmap(bitmap, leftDatumLine, topDatumLine, null);
                        }
                    }
                }
            }
            if (cachePosition != -1) {
                View cacheView = cosmeticExtension.getCosmeticView(cachePosition);
                if (cacheView != null) {
                    int length = cosmeticExtension.getCosmeticHeight(cachePosition);
                    if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                        int spanCount = gridLayoutManager.getSpanCount();
                        if (orientation == ORIENTATION_VERTICAL) {
                            measureAndLayout(cacheView, getValidWidth(parent) / spanCount, length);
                        } else if (orientation == ORIENTATION_HORIZONTAL) {
                            measureAndLayout(cacheView, length, getValidHeight(parent) / spanCount);
                        }
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        if (orientation == ORIENTATION_VERTICAL) {
                            measureAndLayout(cacheView, getValidWidth(parent), length);
                        } else if (orientation == ORIENTATION_HORIZONTAL) {
                            measureAndLayout(cacheView, length, getValidHeight(parent));
                        }
                    }
                    Bitmap bitmap = Bitmap.createBitmap(cacheView.getWidth(), cacheView.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.WHITE);
                    cacheView.draw(canvas);
                    c.drawBitmap(bitmap, leftDatumLine, topDatumLine, null);
                }
            }
        }
    }

    /**
     * 指定宽高不可见view的测量和布局过程
     *
     * @param view
     * @param width
     * @param height
     */
    private void measureAndLayout(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private int getValidWidth(RecyclerView parent) {
        return (parent.getRight() - parent.getPaddingRight()) - (parent.getLeft() + parent.getPaddingLeft());
    }

    private int getValidHeight(RecyclerView parent) {
        return (parent.getBottom() - parent.getPaddingBottom()) - (parent.getTop() + parent.getPaddingTop());
    }

    private int getLatestCosmeticPosition(int position) {
        if (cosmeticExtension != null) {
            for (int i = position; i >= 0; i--) {
                if (cosmeticExtension.isCosmeticItem(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static class Builder {
        private CosmeticItemDecoration cosmeticItemDecoration;

        private Builder(CosmeticExtension cosmeticExtension) {
            cosmeticItemDecoration = new CosmeticItemDecoration(cosmeticExtension);
        }

        public static Builder with(CosmeticExtension cosmeticExtension) {
            return new Builder(cosmeticExtension);
        }

        public CosmeticItemDecoration build() {
            return cosmeticItemDecoration;
        }
    }

}
