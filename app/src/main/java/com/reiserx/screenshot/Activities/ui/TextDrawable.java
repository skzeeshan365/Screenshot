package com.reiserx.screenshot.Activities.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

public class TextDrawable extends Drawable {
    private final String text;
    private final Paint paint;

    public TextDrawable(String text) {
        this.text = text;
        this.paint = new Paint();
        paint.setColor(0xff000000); // default color black
        paint.setTextSize(100); // default text size
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int x = bounds.centerX();
        float y = bounds.centerY() - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(text, x, y, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setTextColor(int color) {
        paint.setColor(color);
    }

    public void setTextSize(float size) {
        paint.setTextSize(size);
    }

    public void setFont(Context context, int fontResId) {
        Typeface typeface = ResourcesCompat.getFont(context, fontResId);
        paint.setTypeface(typeface);
    }
}
