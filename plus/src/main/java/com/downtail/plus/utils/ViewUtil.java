package com.downtail.plus.utils;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ViewUtil {

    /**
     * 获取所有带id的view
     *
     * @param view 最上层的view
     * @return
     */
    public static List<View> getChildViewWithId(View view) {
        List<View> children = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            LinkedList<ViewGroup> groups = new LinkedList<>();
            groups.add(viewGroup);
            while (!groups.isEmpty()) {
                ViewGroup current = groups.removeFirst();
                int childCount = current.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = current.getChildAt(i);
                    if (child instanceof ViewGroup) {
                        groups.addLast((ViewGroup) child);
                    }
                    if (child.getId() != View.NO_ID) {
                        children.add(child);
                    }
                }
            }
        }
        return children;
    }

    /**
     * 指定宽高的不可见view的测量和布局过程
     *
     * @param view
     * @param width
     * @param height
     */
    public static void measureAndLayout(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

}
