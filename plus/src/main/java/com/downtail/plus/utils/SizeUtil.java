package com.downtail.plus.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

public class SizeUtil {

    public static int getValidWidth(RecyclerView parent) {
        return (parent.getRight() - parent.getPaddingRight()) - (parent.getLeft() + parent.getPaddingLeft());
    }

    public static int getValidHeight(RecyclerView parent) {
        return (parent.getBottom() - parent.getPaddingBottom()) - (parent.getTop() + parent.getPaddingTop());
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}