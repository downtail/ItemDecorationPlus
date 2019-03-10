package com.downtail.plus.decorations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.downtail.plus.extensions.CosmeticExtension;

public class CosmeticItemDecoration extends RecyclerView.ItemDecoration {

    private CosmeticExtension cosmeticExtension;

    private CosmeticItemDecoration(CosmeticExtension cosmeticExtension) {
        this.cosmeticExtension = cosmeticExtension;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (cosmeticExtension != null) {
            int position = parent.getChildAdapterPosition(view);
            if (cosmeticExtension.isCosmeticItem(position)) {
                View cosmeticView = cosmeticExtension.getCosmeticView(position);
                if (cosmeticView!=null){
                    cosmeticView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    cosmeticView.layout(0, 0, cosmeticView.getMeasuredWidth(), cosmeticView.getMeasuredHeight());
                    outRect.top = cosmeticView.getMeasuredHeight();
                }
            }
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (cosmeticExtension != null) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(child);
                if (cosmeticExtension.isCosmeticItem(position)) {
                    View cosmeticView = cosmeticExtension.getCosmeticView(position);
                    if (cosmeticView != null) {
                        cosmeticView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        cosmeticView.layout(0, 0, cosmeticView.getMeasuredWidth(), cosmeticView.getMeasuredHeight());
                        cosmeticView.setDrawingCacheEnabled(true);
                        cosmeticView.buildLayer();
                        Bitmap bitmap = Bitmap.createBitmap(cosmeticView.getMeasuredWidth(), cosmeticView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawColor(Color.WHITE);
                        cosmeticView.draw(canvas);
                        c.drawBitmap(bitmap, child.getLeft(), child.getTop() - cosmeticView.getHeight(), null);
                    }
                }
            }
        }
    }

    public static class Builder {
        private CosmeticItemDecoration cosmeticItemDecoration;

        private Builder(CosmeticExtension cosmeticExtension) {
            cosmeticItemDecoration = new CosmeticItemDecoration(cosmeticExtension);
        }

        public static Builder with(CosmeticExtension cosmeticExtension) {
            return new Builder(cosmeticExtension);
        }

        public CosmeticItemDecoration build() {
            return cosmeticItemDecoration;
        }
    }

}
