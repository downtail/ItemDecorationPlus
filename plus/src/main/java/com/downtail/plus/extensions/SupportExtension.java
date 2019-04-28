package com.downtail.plus.extensions;

import android.view.View;

/**
 * 拓展接口
 */
public interface SupportExtension {

    /**
     * 这个item项是否需要粘性
     *
     * @param position
     * @return
     */
    boolean isSupportItem(int position);

    /**
     * 这个item需要粘性的长度，当isSupportItem(int position)返回true时调用
     *
     * @param position
     * @return
     */
    int getSupportHeight(int position);

    /**
     * 返回粘性的view，当isSupportItem(int position)返回true时调用
     *
     * @param position
     * @return
     */
    View getSupportView(int position);

    /**
     * 获取缓存key，如返回null表示不缓存当前粘性view
     *
     * @param position
     * @return
     */
    String getCacheKey(int position);

}
