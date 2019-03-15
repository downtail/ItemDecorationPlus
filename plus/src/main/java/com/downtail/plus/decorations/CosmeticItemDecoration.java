package com.downtail.plus.decorations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.downtail.plus.extensions.CosmeticExtension;
import com.downtail.plus.utils.SizeUtil;
import com.downtail.plus.utils.ViewUtil;

import java.util.List;

public class CosmeticItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 布局方向
     */
    private static final int ORIENTATION_HORIZONTAL = 0;
    private static final int ORIENTATION_VERTICAL = 1;
    private static final int ORIENTATION_NONE = -1;

    /**
     * 拓展接口
     */
    private CosmeticExtension cosmeticExtension;

    private RecyclerView recyclerView;

    /**
     * 缓存position
     */
    private int cachePosition;

    /**
     * 缓存边界位置
     */
    private SparseIntArray cacheEdges;

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

    /**
     * 子view设置监听事件
     */
    private OnCosmeticViewClickListener onCosmeticViewClickListener;

    /**
     * 整个item设置监听事件
     */
    private OnCosmeticItemClickListener onCosmeticItemClickListener;

    /**
     * 手势检测，拦截事件后让其处理
     */
    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return onTouchEvent(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    private CosmeticItemDecoration(CosmeticExtension cosmeticExtension) {
        this.cosmeticExtension = cosmeticExtension;
        this.cacheEdges = new SparseIntArray();
        this.cachePosition = -1;
        this.orientation = ORIENTATION_NONE;
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
                        int spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(position);
                        orientation = gridLayoutManager.getOrientation();
                        if (orientation == ORIENTATION_VERTICAL) {
                            ViewUtil.measureAndLayout(cosmeticView, SizeUtil.getValidWidth(parent) * spanSize / spanCount, length);
                            outRect.top = cosmeticView.getHeight();
                        } else if (orientation == ORIENTATION_HORIZONTAL) {
                            ViewUtil.measureAndLayout(cosmeticView, length, SizeUtil.getValidHeight(parent) * spanSize / spanCount);
                            outRect.left = cosmeticView.getWidth();
                        }
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        orientation = ((LinearLayoutManager) layoutManager).getOrientation();
                        if (orientation == ORIENTATION_VERTICAL) {
                            ViewUtil.measureAndLayout(cosmeticView, SizeUtil.getValidWidth(parent), length);
                            outRect.top = cosmeticView.getHeight();
                        } else if (orientation == ORIENTATION_HORIZONTAL) {
                            ViewUtil.measureAndLayout(cosmeticView, length, SizeUtil.getValidHeight(parent));
                            outRect.left = cosmeticView.getWidth();
                        }
                    } else {
                        orientation = ORIENTATION_NONE;
                    }
                }
            }
        }

    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (recyclerView == null || recyclerView != parent) {
            recyclerView = parent;
        }

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
            for (int i = childCount - 1; i >= 0; i--) {
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                if (orientation == ORIENTATION_VERTICAL) {
                    int topDistance = child.getTop();
                    if (cosmeticExtension.isCosmeticItem(position)) {
                        int distance = cosmeticExtension.getCosmeticHeight(position);
                        if (topDistance - distance <= top) {
                            cachePosition = position;
                            cacheEdges.put(position, child.getLeft());
                            break;
                        } else {
                            cachePosition = getLatestCosmeticPosition(position - 1);
                            if (cachePosition != -1) {
                                int cacheHeight = cosmeticExtension.getCosmeticHeight(cachePosition);
                                if (topDistance - distance <= top + cacheHeight) {
                                    topDatumLine = topDistance - distance - cacheHeight;
                                }
                            }
                        }
                    } else {
                        cachePosition = getLatestCosmeticPosition(position - 1);
                    }

                    if (cosmeticExtension.isCosmeticItem(position) && cachePosition != position) {
                        View cosmeticView = cosmeticExtension.getCosmeticView(position);
                        if (cosmeticView != null) {
                            int length = cosmeticExtension.getCosmeticHeight(position);
                            if (layoutManager instanceof GridLayoutManager) {
                                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                                int spanCount = gridLayoutManager.getSpanCount();
                                int spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(position);
                                ViewUtil.measureAndLayout(cosmeticView, SizeUtil.getValidWidth(parent) * spanSize / spanCount, length);
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                ViewUtil.measureAndLayout(cosmeticView, SizeUtil.getValidWidth(parent), length);
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
                    if (cosmeticExtension.isCosmeticItem(position)) {
                        int distance = cosmeticExtension.getCosmeticHeight(position);
                        if (leftDistance - distance <= left) {
                            cachePosition = position;
                            cacheEdges.put(position, child.getTop());
                            break;
                        } else {
                            cachePosition = getLatestCosmeticPosition(position - 1);
                            if (cachePosition != -1) {
                                int cacheHeight = cosmeticExtension.getCosmeticHeight(cachePosition);
                                if (leftDistance - distance <= left + cacheHeight) {
                                    leftDatumLine = leftDistance - distance - cacheHeight;
                                }
                            }
                        }
                    } else {
                        cachePosition = getLatestCosmeticPosition(position - 1);
                    }

                    if (cosmeticExtension.isCosmeticItem(position) && cachePosition != position) {
                        View cosmeticView = cosmeticExtension.getCosmeticView(position);
                        if (cosmeticView != null) {
                            int length = cosmeticExtension.getCosmeticHeight(position);
                            if (layoutManager instanceof GridLayoutManager) {
                                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                                int spanCount = gridLayoutManager.getSpanCount();
                                int spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(position);
                                ViewUtil.measureAndLayout(cosmeticView, length, SizeUtil.getValidHeight(parent) * spanSize / spanCount);
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                ViewUtil.measureAndLayout(cosmeticView, length, SizeUtil.getValidHeight(parent));
                            }
                            Bitmap bitmap = Bitmap.createBitmap(cosmeticView.getWidth(), cosmeticView.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            canvas.drawColor(Color.WHITE);
                            cosmeticView.draw(canvas);
                            c.drawBitmap(bitmap, child.getLeft() - cosmeticView.getWidth(), child.getTop(), null);
                        }
                    }
                }
            }
            if (cachePosition != -1 && orientation != ORIENTATION_NONE) {
                View cacheView = cosmeticExtension.getCosmeticView(cachePosition);
                if (cacheView != null) {
                    int length = cosmeticExtension.getCosmeticHeight(cachePosition);
                    if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                        int spanCount = gridLayoutManager.getSpanCount();
                        int spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(cachePosition);
                        if (orientation == ORIENTATION_VERTICAL) {
                            ViewUtil.measureAndLayout(cacheView, SizeUtil.getValidWidth(parent) * spanSize / spanCount, length);
                        } else if (orientation == ORIENTATION_HORIZONTAL) {
                            ViewUtil.measureAndLayout(cacheView, length, SizeUtil.getValidHeight(parent) * spanSize / spanCount);
                        }
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        if (orientation == ORIENTATION_VERTICAL) {
                            ViewUtil.measureAndLayout(cacheView, SizeUtil.getValidWidth(parent), length);
                        } else if (orientation == ORIENTATION_HORIZONTAL) {
                            ViewUtil.measureAndLayout(cacheView, length, SizeUtil.getValidHeight(parent));
                        }
                    }
                    Bitmap bitmap = Bitmap.createBitmap(cacheView.getWidth(), cacheView.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.WHITE);
                    cacheView.draw(canvas);
                    if (orientation == ORIENTATION_VERTICAL) {
                        leftDatumLine = cacheEdges.get(cachePosition);
                    } else if (orientation == ORIENTATION_HORIZONTAL) {
                        topDatumLine = cacheEdges.get(cachePosition);
                    }
                    c.drawBitmap(bitmap, leftDatumLine, topDatumLine, null);
                }
            }

            if (gestureDetector == null) {
                gestureDetector = new GestureDetector(parent.getContext(), onGestureListener);
                parent.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                        return gestureDetector.onTouchEvent(motionEvent);
                    }

                    @Override
                    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean b) {

                    }
                });
            }
        }
    }


    private void reMeasureAndLayout(View view, int length) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            int spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(cachePosition);
            if (orientation == ORIENTATION_VERTICAL) {
                ViewUtil.measureAndLayout(view, SizeUtil.getValidWidth(recyclerView) * spanSize / spanCount, length);
            } else if (orientation == ORIENTATION_HORIZONTAL) {
                ViewUtil.measureAndLayout(view, length, SizeUtil.getValidHeight(recyclerView) * spanSize / spanCount);
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (orientation == ORIENTATION_VERTICAL) {
                ViewUtil.measureAndLayout(view, SizeUtil.getValidWidth(recyclerView), length);
            } else if (orientation == ORIENTATION_HORIZONTAL) {
                ViewUtil.measureAndLayout(view, length, SizeUtil.getValidHeight(recyclerView));
            }
        }
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

    private boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        View child = recyclerView.findChildViewUnder(x, y);
        if (child == null) {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = recyclerView.getChildAt(i);
                int position = recyclerView.getChildAdapterPosition(view);
                if (cosmeticExtension.isCosmeticItem(position)) {
                    int length = cosmeticExtension.getCosmeticHeight(position);
                    int bottom = view.getBottom();
                    int top = view.getTop();
                    int left = view.getLeft();
                    int right = view.getRight();
                    if (orientation == ORIENTATION_VERTICAL) {
                        bottom = view.getTop();
                        top = bottom - length;
                    } else if (orientation == ORIENTATION_HORIZONTAL) {
                        right = view.getLeft();
                        left = right - length;
                    }
                    if (x > left && x < right && y > top && y < bottom) {
                        View cosmeticView = cosmeticExtension.getCosmeticView(position);
                        if (cosmeticView != null) {
                            return onChildTouchEvent(cosmeticView, length, x, y, left, top, position);
                        }
                    }
                }
            }
            return false;
        } else {
            if (cachePosition != -1) {
                View cosmeticView = cosmeticExtension.getCosmeticView(cachePosition);
                if (cosmeticView != null) {
                    int length = cosmeticExtension.getCosmeticHeight(cachePosition);
                    reMeasureAndLayout(cosmeticView, length);
                    if (x > leftDatumLine && x < leftDatumLine + cosmeticView.getWidth() && y > topDatumLine && y < topDatumLine + cosmeticView.getHeight()) {
                        return onChildTouchEvent(cosmeticView, length, x, y, leftDatumLine, topDatumLine, cachePosition);
                    }
                }
            }
            return false;
        }
    }

    private boolean onChildTouchEvent(View view, int length, float x, float y, int left, int top, int position) {
        reMeasureAndLayout(view, length);
        if (onCosmeticViewClickListener != null) {
            List<View> children = ViewUtil.getChildViewWithId(view);
            for (int j = 0; j < children.size(); j++) {
                View subView = children.get(j);
                if (x > left + subView.getLeft() && x < left + subView.getRight() && y > top + subView.getTop() && y < top + subView.getBottom()) {
                    onCosmeticViewClickListener.onCosmeticViewClick(subView, position);
                    return true;
                }
            }
        }
        if (onCosmeticItemClickListener != null) {
            onCosmeticItemClickListener.onCosmeticItemClick(position);
        }
        return true;
    }

    public static class Builder {
        private CosmeticItemDecoration cosmeticItemDecoration;

        private Builder(CosmeticExtension cosmeticExtension) {
            cosmeticItemDecoration = new CosmeticItemDecoration(cosmeticExtension);
        }

        public static Builder with(CosmeticExtension cosmeticExtension) {
            return new Builder(cosmeticExtension);
        }

        public Builder setOnCosmeticViewClickListener(OnCosmeticViewClickListener onCosmeticViewClickListener) {
            cosmeticItemDecoration.onCosmeticViewClickListener = onCosmeticViewClickListener;
            return this;
        }

        public Builder setOnCosmeticItemClickListener(OnCosmeticItemClickListener onCosmeticViewItemClickListener) {
            cosmeticItemDecoration.onCosmeticItemClickListener = onCosmeticViewItemClickListener;
            return this;
        }

        public CosmeticItemDecoration build() {
            return cosmeticItemDecoration;
        }
    }

    public interface OnCosmeticViewClickListener {
        void onCosmeticViewClick(View view, int position);
    }

    public interface OnCosmeticItemClickListener {
        void onCosmeticItemClick(int position);
    }

}
