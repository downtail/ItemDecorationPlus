package com.downtail.plus.utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;

public class BitmapLoader {

    private LruCache<String, Bitmap> lruCache;

    public BitmapLoader() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    public void addBitmap(String key, Bitmap bitmap) {
        lruCache.put(key, bitmap);
    }

    public Bitmap getBitmap(String key) {
        return lruCache.get(key);
    }
}
