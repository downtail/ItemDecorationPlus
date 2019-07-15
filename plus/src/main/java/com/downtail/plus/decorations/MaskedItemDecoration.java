package com.downtail.plus.decorations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
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

public class MaskedItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 布局方向
     */
    private static final int ORIENTATION_HORIZONTAL = 0;
    private static final int ORIENTATION_VERTICAL = 1;
    private static final int ORIENTATION_NONE = -1;

    /**
     * 实际的布局方向
     */
    private int orientation;

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
    private OnMaskedViewClickListener onMaskedViewClickListener;

    /**
     * 整个item设置监听事件
     */
    private OnMaskedItemClickListener onMaskedItemClickListener;

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

    private MaskedItemDecoration(SupportExtension supportExtension) {
        this.supportExtension = supportExtension;
        this.cacheViews = new HashMap<>();
        this.cacheEdges = new HashMap<>();
        this.cachePosition = -1;
        this.orientation = ORIENTATION_NONE;
        this.offset = 0;
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
                    int bottomDistance = child.getBottom();
                    if (supportExtension.isSupportItem(position)) {
                        if (topDistance <= top) {
                            cachePosition = position;
                            String cacheKey = supportExtension.getCacheKey(cachePosition);
                            if (TextUtils.isEmpty(cacheKey)) {
                                cacheKey = String.valueOf(cachePosition);
                            }
                            cacheEdges.put(cacheKey, child.getLeft());
                        } else {
                            int latestMaskedPosition = getLatestMaskedPosition(position - 1);
                            if (latestMaskedPosition != -1) {
                                cachePosition = latestMaskedPosition;
                                int cacheHeight = supportExtension.getSupportHeight(cachePosition);
                                if (topDistance < top + cacheHeight) {
                                    topDatumLine = topDistance - cacheHeight;
                                }
                                break;
                            } else {
                                cachePosition = -1;
                                break;
                            }
                        }
                    } else {
                        int latestMaskedPosition = getLatestMaskedPosition(position - 1);
                        if (latestMaskedPosition != -1) {
                            cachePosition = latestMaskedPosition;
                            int cacheHeight = supportExtension.getSupportHeight(cachePosition);
                            if (layoutManager instanceof GridLayoutManager) {
                                if (topDistance > top + cacheHeight) {
                                    break;
                                }
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                if (bottomDistance > top + cacheHeight) {
                                    break;
                                }
                            }
                        } else {
                            cachePosition = -1;
                            if (layoutManager instanceof GridLayoutManager) {
                                //continue;
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                break;
                            }
                        }
                    }
                } else if (orientation == ORIENTATION_HORIZONTAL) {
                    int leftDistance = child.getLeft();
                    int rightDistance = child.getRight();
                    if (supportExtension.isSupportItem(position)) {
                        if (leftDistance <= left) {
                            cachePosition = position;
                            String cacheKey = supportExtension.getCacheKey(cachePosition);
                            if (TextUtils.isEmpty(cacheKey)) {
                                cacheKey = String.valueOf(cachePosition);
                            }
                            cacheEdges.put(cacheKey, child.getTop());
                            break;
                        } else {
                            int latestMaskedPosition = getLatestMaskedPosition(position - 1);
                            if (latestMaskedPosition != -1) {
                                cachePosition = latestMaskedPosition;
                                int cacheHeight = supportExtension.getSupportHeight(cachePosition);
                                if (leftDistance < left + cacheHeight) {
                                    leftDatumLine = leftDistance - cacheHeight;
                                }
                                break;
                            } else {
                                cachePosition = -1;
                                break;
                            }
                        }
                    } else {
                        int latestMaskedPosition = getLatestMaskedPosition(position - 1);
                        if (latestMaskedPosition != -1) {
                            cachePosition = latestMaskedPosition;
                            int cacheHeight = supportExtension.getSupportHeight(cachePosition);
                            if (layoutManager instanceof GridLayoutManager) {
                                if (leftDistance > left + cacheHeight) {
                                    break;
                                }
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                if (rightDistance > left + cacheHeight) {
                                    break;
                                }
                            }
                        } else {
                            cachePosition = -1;
                            if (layoutManager instanceof GridLayoutManager) {
                                //continue;
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                break;
                            }
                        }
                    }
                }
            }

            if (cachePosition != -1 && orientation != ORIENTATION_NONE) {
                String cacheKey = supportExtension.getCacheKey(cachePosition);
                if (TextUtils.isEmpty(cacheKey)) {
                    cacheKey = String.valueOf(cachePosition);
                }
                View cacheView = cacheViews.get(cacheKey);
                if (cacheView == null) {
                    cacheView = supportExtension.getSupportView(cachePosition);
                    int cacheHeight = supportExtension.getSupportHeight(cachePosition);
                    reMeasureAndLayout(cacheView, cacheHeight);
                    cacheViews.put(cacheKey, cacheView);
                }

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
                        if (onMaskedItemClickListener == null && onMaskedViewClickListener == null) {
                            return false;
                        }
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


    /**
     * 获取上一个Masked的position
     *
     * @param position 当前view的position
     * @return 上一个粘性position
     */
    private int getLatestMaskedPosition(int position) {
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
     * 拦截并处理item的点击事件
     *
     * @param event 触摸事件，在此具体为单击事件
     * @return 是否将其拦截消费掉
     */
    private boolean onTouchEvent(MotionEvent event) {
        if (cachePosition != -1) {
            String cacheKey = supportExtension.getCacheKey(cachePosition);
            if (TextUtils.isEmpty(cacheKey)) {
                cacheKey = String.valueOf(cachePosition);
            }
            View cacheView = cacheViews.get(cacheKey);
            if (cacheView == null) {
                cacheView = supportExtension.getSupportView(cachePosition);
                int cacheHeight = supportExtension.getSupportHeight(cachePosition);
                reMeasureAndLayout(cacheView, cacheHeight);
                cacheViews.put(cacheKey, cacheView);
            }

            if (cacheView != null) {
                float x = event.getX();
                float y = event.getY();
                if (x > leftDatumLine && x < leftDatumLine + cacheView.getWidth() && y > topDatumLine && y < topDatumLine + cacheView.getHeight()) {
                    if (onMaskedViewClickListener != null) {
                        List<View> children = ViewUtil.getChildViewWithId(cacheView);
                        for (int i = 0; i < children.size(); i++) {
                            View child = children.get(i);
                            int top = child.getTop() + topDatumLine;
                            int bottom = child.getBottom() + topDatumLine;
                            int left = child.getLeft() + leftDatumLine;
                            int right = child.getRight() + leftDatumLine;
                            if (x > left && x < right && y > top && y < bottom) {
                                onMaskedViewClickListener.onMaskedViewClick(child, cachePosition);
                                return true;
                            }
                        }
                    }
                    if (onMaskedItemClickListener != null) {
                        onMaskedItemClickListener.onMaskedItemClick(cachePosition);
                    }
                    return true;
                }
            }
        }
        return false;
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

    public static class Builder {
        private MaskedItemDecoration maskedItemDecoration;

        private Builder(SupportExtension supportExtension) {
            maskedItemDecoration = new MaskedItemDecoration(supportExtension);
        }

        public static Builder with(SupportExtension supportExtension) {
            return new Builder(supportExtension);
        }

        public Builder setOnMaskedViewClickListener(OnMaskedViewClickListener onMaskedViewClickListener) {
            maskedItemDecoration.onMaskedViewClickListener = onMaskedViewClickListener;
            return this;
        }

        public Builder setOnMaskedItemClickListener(OnMaskedItemClickListener onMaskedItemClickListener) {
            maskedItemDecoration.onMaskedItemClickListener = onMaskedItemClickListener;
            return this;
        }

        public MaskedItemDecoration build() {
            return maskedItemDecoration;
        }
    }

    /**
     * childView点击监听
     */
    public interface OnMaskedViewClickListener {
        void onMaskedViewClick(View view, int position);
    }

    /**
     * item点击监听
     */
    public interface OnMaskedItemClickListener {
        void onMaskedItemClick(int position);
    }

    /**
     * 上拉加载更多时对粘性布局设置偏移
     *
     * @param offset
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
