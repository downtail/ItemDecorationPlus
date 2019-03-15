package com.downtail.plus.decorations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.downtail.plus.extensions.MaskedExtension;
import com.downtail.plus.utils.SizeUtil;
import com.downtail.plus.utils.ViewUtil;

import java.util.List;

public class MaskedItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 布局方向
     */
    private static final int ORIENTATION_HORIZONTAL = 0;
    private static final int ORIENTATION_VERTICAL = 1;
    private static final int ORIENTATION_NONE = -1;

    /**
     * 拓展接口
     */
    private MaskedExtension maskedExtension;

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

    private MaskedItemDecoration(MaskedExtension maskedExtension) {
        this.maskedExtension = maskedExtension;
        this.cacheEdges = new SparseIntArray();
        this.cachePosition = -1;
        this.orientation = ORIENTATION_NONE;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (recyclerView == null || recyclerView != parent) {
            recyclerView = parent;
        }

        if (maskedExtension != null) {
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
                    if (maskedExtension.isMaskedItem(position)) {
                        if (topDistance <= top) {
                            cachePosition = position;
                            cacheEdges.put(position, child.getLeft());
                            break;
                        } else {
                            cachePosition = getLatestMaskedPosition(position - 1);
                            if (cachePosition != -1) {
                                int cacheHeight = maskedExtension.getMaskedHeight(cachePosition);
                                if (topDistance < top + cacheHeight) {
                                    topDatumLine = topDistance - cacheHeight;
                                }
                            }
                        }
                    } else {
                        cachePosition = getLatestMaskedPosition(position - 1);
                    }
                } else if (orientation == ORIENTATION_HORIZONTAL) {
                    int leftDistance = child.getLeft();
                    if (maskedExtension.isMaskedItem(position)) {
                        if (leftDistance <= left) {
                            cachePosition = position;
                            cacheEdges.put(position, child.getTop());
                            break;
                        } else {
                            cachePosition = getLatestMaskedPosition(position - 1);
                            if (cachePosition != -1) {
                                int cacheHeight = maskedExtension.getMaskedHeight(cachePosition);
                                if (leftDistance < left + cacheHeight) {
                                    leftDatumLine = leftDistance - cacheHeight;
                                }
                            }
                        }
                    } else {
                        cachePosition = getLatestMaskedPosition(position - 1);
                    }
                }
            }

            if (cachePosition != -1 && orientation != ORIENTATION_NONE) {
                View cacheView = maskedExtension.getMaskedView(cachePosition);
                if (cacheView != null) {
                    int length = maskedExtension.getMaskedHeight(cachePosition);
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
                        View childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                        if (childView != null) {
                            return gestureDetector.onTouchEvent(motionEvent);
                        }
                        return false;
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
     * @return
     */
    private int getLatestMaskedPosition(int position) {
        if (maskedExtension != null) {
            for (int i = position; i >= 0; i--) {
                if (maskedExtension.isMaskedItem(i)) {
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
            View maskedView = maskedExtension.getMaskedView(cachePosition);
            if (maskedView != null) {
                float x = event.getX();
                float y = event.getY();

                int length = maskedExtension.getMaskedHeight(cachePosition);
                reMeasureAndLayout(maskedView, length);
                if (x > leftDatumLine && x < leftDatumLine + maskedView.getWidth() && y > topDatumLine && y < topDatumLine + maskedView.getHeight()) {
                    if (onMaskedViewClickListener != null) {
                        List<View> children = ViewUtil.getChildViewWithId(maskedView);
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
                } else {
                    return false;
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

        public Builder(MaskedExtension maskedExtension) {
            maskedItemDecoration = new MaskedItemDecoration(maskedExtension);
        }

        public static Builder with(MaskedExtension maskedExtension) {
            return new Builder(maskedExtension);
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

}
