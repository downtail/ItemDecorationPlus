package com.downtail.plus.decorations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.downtail.plus.extensions.MaskedExtension;
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
     * 缓存的views对象
     */
    private SparseArray<View> cachePool;

    /**
     * 缓存的view对象
     */
    private View cacheView;

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
        this.cachePool = new SparseArray<>();
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
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                orientation = ORIENTATION_NONE;
            }

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                if (orientation == ORIENTATION_VERTICAL) {
                    int topDistance = child.getTop();
                    if (cacheView != null) {
                        if (maskedExtension.isMaskedItem(position)) {
                            if (topDistance <= top) {
                                cacheView = cachePool.get(position);
                                if (cacheView == null) {
                                    cachePool.append(position, child);
                                    cacheView = child;
                                    parent.getChildViewHolder(child).setIsRecyclable(false);
                                }
                            } else {
                                clearCachePool(parent, position);
                                int latestPosition = getLatestMaskedPosition(position - 1);
                                if (latestPosition != -1) {
                                    cacheView = cachePool.get(latestPosition);
                                } else {
                                    cacheView = null;
                                }
                                if (cacheView != null) {
                                    if (topDistance < top + cacheView.getHeight()) {
                                        topDatumLine = topDistance - cacheView.getHeight();
                                    }
                                }
                                break;
                            }
                        } else {
                            clearCachePool(parent, position);
                            int latestPosition = getLatestMaskedPosition(position);
                            if (latestPosition != -1) {
                                cacheView = cachePool.get(latestPosition);
                            } else {
                                cacheView = null;
                            }
                            if (cacheView != null) {
                                if (layoutManager instanceof GridLayoutManager) {
                                    if (topDistance > top + cacheView.getHeight()) {
                                        break;
                                    }
                                } else if (layoutManager instanceof LinearLayoutManager) {
                                    if (child.getBottom() > top + cacheView.getHeight()) {
                                        break;
                                    }
                                }
                            } else {
                                if (layoutManager instanceof GridLayoutManager) {
                                    //next
                                } else if (layoutManager instanceof LinearLayoutManager) {
                                    break;
                                }
                            }
                        }
                    } else {
                        if (maskedExtension.isMaskedItem(position)) {
                            if (topDistance <= top) {
                                cachePool.append(position, child);
                                cacheView = child;
                                parent.getChildViewHolder(child).setIsRecyclable(false);
                            }
                        } else {
                            if (layoutManager instanceof GridLayoutManager) {
                                //next
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                break;
                            }
                        }
                    }
                } else if (orientation == ORIENTATION_HORIZONTAL) {
                    int leftDistance = child.getLeft();
                    if (cacheView != null) {
                        if (maskedExtension.isMaskedItem(position)) {
                            if (leftDistance <= left) {
                                cacheView = cachePool.get(position);
                                if (cacheView == null) {
                                    cachePool.append(position, child);
                                    cacheView = child;
                                    parent.getChildViewHolder(child).setIsRecyclable(false);
                                }
                            } else {
                                clearCachePool(parent, position);
                                int latestPosition = getLatestMaskedPosition(position - 1);
                                if (latestPosition != -1) {
                                    cacheView = cachePool.get(latestPosition);
                                } else {
                                    cacheView = null;
                                }
                                if (cacheView != null) {
                                    if (leftDistance < left + cacheView.getWidth()) {
                                        leftDatumLine = leftDistance - cacheView.getWidth();
                                    }
                                }
                                break;
                            }
                        } else {
                            clearCachePool(parent, position);
                            int latestPosition = getLatestMaskedPosition(position);
                            if (latestPosition != -1) {
                                cacheView = cachePool.get(latestPosition);
                            } else {
                                cacheView = null;
                            }
                            if (cacheView != null) {
                                if (layoutManager instanceof GridLayoutManager) {
                                    if (leftDistance > left + cacheView.getWidth()) {
                                        break;
                                    }
                                } else if (layoutManager instanceof LinearLayoutManager) {
                                    if (child.getRight() > left + cacheView.getWidth()) {
                                        break;
                                    }
                                }
                            } else {
                                if (layoutManager instanceof GridLayoutManager) {
                                    //next
                                } else if (layoutManager instanceof LinearLayoutManager) {
                                    break;
                                }
                            }
                        }
                    } else {
                        if (maskedExtension.isMaskedItem(position)) {
                            if (leftDistance <= left) {
                                cachePool.append(position, child);
                                cacheView = child;
                                parent.getChildViewHolder(child).setIsRecyclable(false);
                            }
                        } else {
                            if (layoutManager instanceof GridLayoutManager) {
                                //next
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                break;
                            }
                        }
                    }
                }
            }

            if (cacheView != null && orientation != ORIENTATION_NONE) {
                cacheView.setDrawingCacheEnabled(true);
                cacheView.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(cacheView.getDrawingCache());
                if (orientation == ORIENTATION_VERTICAL) {
                    leftDatumLine = cacheView.getLeft();
                } else if (orientation == ORIENTATION_HORIZONTAL) {
                    topDatumLine = cacheView.getTop();
                }
                c.drawBitmap(bitmap, leftDatumLine, topDatumLine, null);

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
     * 清除某个位置后缓存的view
     *
     * @param parent   RecyclerView
     * @param position 当前view的position
     */
    private void clearCachePool(RecyclerView parent, int position) {
        int size = cachePool.size();
        for (int i = 0; i < size; i++) {
            int key = cachePool.keyAt(i);
            if (key >= position) {
                View child = cachePool.get(key);
                if (child != null) {
                    parent.getChildViewHolder(child).setIsRecyclable(true);
                }
                cachePool.remove(key);
            }
        }
    }

    /**
     * 拦截并处理item的点击事件
     *
     * @param event 触摸事件，在此具体为单击事件
     * @return 是否将其拦截消费掉
     */
    private boolean onTouchEvent(MotionEvent event) {
        if (cacheView != null) {
            float x = event.getX();
            float y = event.getY();
            if (x > leftDatumLine && x < leftDatumLine + cacheView.getWidth()
                    && y > topDatumLine && y < topDatumLine + cacheView.getHeight()) {

                if (onMaskedViewClickListener != null) {
                    List<View> children = ViewUtil.getChildViewWithId(cacheView);
                    for (int i = 0; i < children.size(); i++) {
                        View child = children.get(i);
                        int top = child.getTop() + topDatumLine;
                        int bottom = child.getBottom() + topDatumLine;
                        int left = child.getLeft() + leftDatumLine;
                        int right = child.getRight() + leftDatumLine;
                        if (x > left && x < right && y > top && y < bottom) {
                            onMaskedViewClickListener.onMaskedViewClick(child, recyclerView.getChildAdapterPosition(cacheView));
                            return true;
                        }
                    }
                }
                if (onMaskedItemClickListener != null) {
                    onMaskedItemClickListener.onMaskedItemClick(recyclerView.getChildAdapterPosition(cacheView));
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
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
