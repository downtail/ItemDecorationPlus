package com.downtail.plus.decorations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.downtail.plus.extensions.MaskedExtension;

public class MaskedItemDecoration extends RecyclerView.ItemDecoration {

    private static final int ORIENTATION_HORIZONTAL = 0;
    private static final int ORIENTATION_VERTICAL = 1;
    private static final int ORIENTATION_NONE = -1;

    private MaskedExtension maskedExtension;
    private SparseArray<View> cachePool;
    private View cacheView;
    private int orientation;

    public MaskedItemDecoration(MaskedExtension maskedExtension) {
        this.maskedExtension = maskedExtension;
        this.cachePool = new SparseArray<>();
        this.orientation = ORIENTATION_NONE;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (maskedExtension != null) {
            int top = parent.getTop() + parent.getPaddingTop();
            int left = parent.getLeft() + parent.getPaddingLeft();
            int topDatumLine = top;
            int leftDatumLine = left;

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

                parent.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
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

}
