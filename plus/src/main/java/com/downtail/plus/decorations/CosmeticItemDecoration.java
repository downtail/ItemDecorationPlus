package com.downtail.plus.decorations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.downtail.plus.extensions.SupportExtension;
import com.downtail.plus.utils.SizeUtil;
import com.downtail.plus.utils.ViewUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private SupportExtension supportExtension;

    private RecyclerView recyclerView;

    /**
     * 缓存position
     */
    private int cachePosition;

    /**
     * 缓存view
     */
    private Map<String, View> cacheViews;

    /**
     * 缓存边界位置
     */
    private Map<String, Integer> cacheEdges;

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
     * 设置偏移量
     */
    private int offset;

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

    private CosmeticItemDecoration(SupportExtension supportExtension) {
        this.supportExtension = supportExtension;
        this.cacheViews = new HashMap<>();
        this.cacheEdges = new HashMap<>();
        this.cachePosition = -1;
        this.orientation = ORIENTATION_NONE;
        this.offset = 0;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (supportExtension != null) {
            int position = parent.getChildAdapterPosition(view);
            if (supportExtension.isSupportItem(position)) {
                View cosmeticView = supportExtension.getSupportView(position);
                if (cosmeticView != null) {
                    int length = supportExtension.getSupportHeight(position);
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
                    String cacheKey = supportExtension.getCacheKey(position);
                    if (TextUtils.isEmpty(cacheKey)) {
                        cacheKey = String.valueOf(position);
                    }
                    cacheViews.put(cacheKey, cosmeticView);
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

        if (supportExtension != null) {
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
                    if (supportExtension.isSupportItem(position)) {
                        int distance = supportExtension.getSupportHeight(position);
                        if (topDistance - distance <= top) {
                            cachePosition = position;
                            String cacheKey = supportExtension.getCacheKey(cachePosition);
                            if (TextUtils.isEmpty(cacheKey)) {
                                cacheKey = String.valueOf(cachePosition);
                            }
                            cacheEdges.put(cacheKey, child.getLeft());
                        } else {
                            if (i == 0) {
                                cachePosition = getLatestCosmeticPosition(position - 1);
                            }
                            if (cachePosition != -1) {
                                int cacheHeight = supportExtension.getSupportHeight(cachePosition);
                                if (topDistance - distance <= top + cacheHeight) {
                                    topDatumLine = topDistance - distance - cacheHeight;
                                }
                            }
                        }
                    } else {
                        if (i == 0) {
                            cachePosition = getLatestCosmeticPosition(position);
                        }
                    }
                } else if (orientation == ORIENTATION_HORIZONTAL) {
                    int leftDistance = child.getLeft();
                    if (supportExtension.isSupportItem(position)) {
                        int distance = supportExtension.getSupportHeight(position);
                        if (leftDistance - distance <= left) {
                            cachePosition = position;
                            String cacheKey = supportExtension.getCacheKey(cachePosition);
                            if (TextUtils.isEmpty(cacheKey)) {
                                cacheKey = String.valueOf(cachePosition);
                            }
                            cacheEdges.put(cacheKey, child.getTop());
                        } else {
                            if (i == 0) {
                                cachePosition = getLatestCosmeticPosition(position - 1);
                            }
                            if (cachePosition != -1) {
                                int cacheHeight = supportExtension.getSupportHeight(cachePosition);
                                if (leftDistance - distance <= left + cacheHeight) {
                                    leftDatumLine = leftDistance - distance - cacheHeight;
                                }
                            }
                        }
                    } else {
                        if (i == 0) {
                            cachePosition = getLatestCosmeticPosition(position - 1);
                        }
                    }
                }
                if (supportExtension.isSupportItem(position) && cachePosition != position) {
                    View cosmeticView = drawView(position);
                    if (cosmeticView != null) {
                        cosmeticView.setDrawingCacheEnabled(true);
                        cosmeticView.buildDrawingCache();
                        Bitmap bitmap = Bitmap.createBitmap(cosmeticView.getDrawingCache());
                        cosmeticView.setDrawingCacheEnabled(false);
                        if (orientation == ORIENTATION_VERTICAL) {
                            c.drawBitmap(bitmap, child.getLeft(), child.getTop() - cosmeticView.getHeight(), null);
                        } else if (orientation == ORIENTATION_HORIZONTAL) {
                            c.drawBitmap(bitmap, child.getLeft() - cosmeticView.getWidth(), child.getTop(), null);
                        }
                        if (!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                    }
                }
            }

            if (cachePosition != -1 && orientation != ORIENTATION_NONE) {
                String cacheKey = supportExtension.getCacheKey(cachePosition);
                if (TextUtils.isEmpty(cacheKey)) {
                    cacheKey = String.valueOf(cachePosition);
                }
                View cacheView = drawView(cachePosition);
                if (cacheView != null) {
                    cacheView.setDrawingCacheEnabled(true);
                    cacheView.buildDrawingCache();
                    Bitmap bitmap = Bitmap.createBitmap(cacheView.getDrawingCache());
                    cacheView.setDrawingCacheEnabled(false);
                    Integer value = cacheEdges.get(cacheKey);
                    if (orientation == ORIENTATION_VERTICAL) {
                        if (value == null) {
                            if (layoutManager instanceof GridLayoutManager) {
                                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                                int spanCount = gridLayoutManager.getSpanCount();
                                int spanIndex = gridLayoutManager.getSpanSizeLookup().getSpanIndex(cachePosition, spanCount);
                                int unitSize = SizeUtil.getValidWidth(recyclerView) / spanCount;
                                value = spanIndex * unitSize;
                            } else {
                                value = parent.getPaddingLeft();
                            }
                            cacheEdges.put(cacheKey, value);
                        }
                        leftDatumLine = value;
                        topDatumLine += offset;
                    } else if (orientation == ORIENTATION_HORIZONTAL) {
                        if (value == null) {
                            if (layoutManager instanceof GridLayoutManager) {
                                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                                int spanCount = gridLayoutManager.getSpanCount();
                                int spanIndex = gridLayoutManager.getSpanSizeLookup().getSpanIndex(cachePosition, spanCount);
                                int unitSize = SizeUtil.getValidHeight(recyclerView) / spanCount;
                                value = spanIndex * unitSize;
                            } else {
                                value = parent.getPaddingTop();
                            }
                            cacheEdges.put(cacheKey, value);
                        }
                        topDatumLine = value;
                        leftDatumLine += offset;
                    }
                    c.drawBitmap(bitmap, leftDatumLine, topDatumLine, null);
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
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

    /**
     * 寻找当前位置的前一个粘性item
     *
     * @param position 当前位置
     * @return 粘性item位置
     */
    private int getLatestCosmeticPosition(int position) {
        if (supportExtension != null) {
            for (int i = position; i >= 0; i--) {
                if (supportExtension.isSupportItem(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 点击事件
     *
     * @param event 触摸事件
     * @return 是否消费
     */
    private boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        View child = recyclerView.findChildViewUnder(x, y);
        if (child == null) {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = recyclerView.getChildAt(i);
                int position = recyclerView.getChildAdapterPosition(view);
                if (supportExtension.isSupportItem(position)) {
                    int length = supportExtension.getSupportHeight(position);
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
                        View cosmeticView = drawView(position);
                        if (cosmeticView != null) {
                            return onChildTouchEvent(cosmeticView, x, y, left, top, position);
                        }
                    }
                }
            }

            //除了粘性item本身view为null，线性和网格布局混合情况下也有出现剩余空间的现象
            if (cachePosition != -1) {
                View cosmeticView = drawView(cachePosition);
                if (cosmeticView != null) {
                    if (x > leftDatumLine && x < leftDatumLine + cosmeticView.getWidth() && y > topDatumLine && y < topDatumLine + cosmeticView.getHeight()) {
                        return onChildTouchEvent(cosmeticView, x, y, leftDatumLine, topDatumLine, cachePosition);
                    }
                }
            }

            return false;
        } else {
            if (cachePosition != -1) {
                View cosmeticView = drawView(cachePosition);
                if (cosmeticView != null) {
                    if (x > leftDatumLine && x < leftDatumLine + cosmeticView.getWidth() && y > topDatumLine && y < topDatumLine + cosmeticView.getHeight()) {
                        return onChildTouchEvent(cosmeticView, x, y, leftDatumLine, topDatumLine, cachePosition);
                    }
                }
            }
            return false;
        }
    }

    private boolean onChildTouchEvent(View view, float x, float y, int left, int top, int position) {
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

    /**
     * 绘制并缓存粘性view
     *
     * @param position 粘性item位置
     */
    private View drawView(int position) {
        String cacheKey = supportExtension.getCacheKey(position);
        if (TextUtils.isEmpty(cacheKey)) {
            cacheKey = String.valueOf(position);
        }
        View view = cacheViews.get(cacheKey);
        if (view == null) {
            view = supportExtension.getSupportView(cachePosition);
            int cacheHeight = supportExtension.getSupportHeight(position);
            reMeasureAndLayout(view, cacheHeight);
            cacheViews.put(cacheKey, view);
        }
        return view;
    }

    public static class Builder {
        private CosmeticItemDecoration cosmeticItemDecoration;

        private Builder(SupportExtension supportExtension) {
            cosmeticItemDecoration = new CosmeticItemDecoration(supportExtension);
        }

        public static Builder with(SupportExtension supportExtension) {
            return new Builder(supportExtension);
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

    /**
     * 子view点击监听
     */
    public interface OnCosmeticViewClickListener {
        void onCosmeticViewClick(View view, int position);
    }

    /**
     * 粘性item点击监听
     */
    public interface OnCosmeticItemClickListener {
        void onCosmeticItemClick(int position);
    }

    /**
     * 上拉加载更多时对粘性布局设置偏移
     *
     * @param offset 绘制的偏移量
     */
    public void setOffset(int offset) {
        this.offset = offset;
        if (recyclerView != null) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                adapter.notifyItemChanged(0);
            }
        }
    }

    /**
     * 当需要重新绘制时清除缓存position
     */
    public void clearCache() {
        cachePosition = -1;
        cacheViews.clear();
    }

}
