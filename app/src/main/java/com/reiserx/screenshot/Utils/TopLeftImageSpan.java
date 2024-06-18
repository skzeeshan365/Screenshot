package com.reiserx.screenshot.Utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

import androidx.annotation.NonNull;

public class TopLeftImageSpan extends DynamicDrawableSpan {

    private Drawable mDrawable;

    public TopLeftImageSpan(Drawable drawable) {
        super(ALIGN_BASELINE);
        mDrawable = drawable;
    }

    @Override
    public Drawable getDrawable() {
        return mDrawable;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Rect rect = mDrawable.getBounds();
        if (fm != null) {
            // Adjust the font metrics to ensure the text alignment is not disturbed
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int textHeight = fontMetricsInt.descent - fontMetricsInt.ascent;
            int drawableHeight = rect.bottom - rect.top;
            int topOffset = (textHeight - drawableHeight) / 2;
            fm.ascent = fontMetricsInt.ascent - topOffset;
            fm.top = fontMetricsInt.top - topOffset;
            fm.bottom = fontMetricsInt.bottom;
            fm.descent = fontMetricsInt.descent;
        }
        return rect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        // Adjust x and y to position the drawable at the top left corner
        int drawableHeight = mDrawable.getIntrinsicHeight();
        int textHeight = (int) paint.getFontMetrics().descent - (int) paint.getFontMetrics().ascent;
        int translateY = top + (textHeight - drawableHeight) / 2;

        canvas.save();

        // Adjust x and y position as needed
        canvas.translate(x, translateY);
        mDrawable.draw(canvas);

        canvas.restore();
    }
}
