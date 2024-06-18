package com.reiserx.screenshot.Activities.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.ButtonDesign;

public class IconCropView extends RelativeLayout {

    private Rect rect;
    private Paint paint;
    private int handleSize = 100;
    private boolean isResizing = false;
    private float lastTouchX, lastTouchY;
    private int touchOffsetX, touchOffsetY;
    private int type;

    boolean isDragging;

    Button button;
    public IconCropView(Context context, int type) {
        super(context);
        init();
        this.type = type;
    }

    public IconCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        rect = new Rect(200, 500, 900, 900);
        paint = new Paint();
        paint.setColor(Color.parseColor("#F0EBE3"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        setBackgroundColor(Color.parseColor("#80000000"));

        ImageView closeIcon = new ImageView(getContext());
        closeIcon.setImageResource(R.drawable.ic_baseline_close_24);
        LayoutParams closeIconParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeIconParams.addRule(ALIGN_PARENT_TOP);
        closeIconParams.addRule(ALIGN_PARENT_LEFT);
        closeIcon.setPadding(16, 16, 16, 16);
        closeIconParams.setMargins(8, 8, 8, 8);
        closeIcon.setOnClickListener(v -> closeSelection());
        addView(closeIcon, closeIconParams);

        button = new Button(getContext());
        button.setText("Capture");
        button.setTextSize(14);
        button.setBackgroundColor(getContext().getColor(R.color.PrimaryColor));
        LayoutParams buttonParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(ALIGN_PARENT_TOP);
        buttonParams.addRule(ALIGN_PARENT_RIGHT);
        buttonParams.setMargins(8, 8, 16, 8);
        addView(button, buttonParams);

        ButtonDesign buttonDesign = new ButtonDesign(getContext());
        buttonDesign.setButtonOutlineLight(button);

        button.setOnClickListener(view -> {
            if (accessibilityService.instance != null) {
                buttonDesign.buttonFillLight(button);
                accessibilityService.instance.captureSelectedArea(rect, type);
            } else {
                Toast.makeText(getContext(), "Failed to capture, Accessibility service is disabled.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void closeSelection() {
        if (accessibilityService.instance != null) {
            accessibilityService.instance.closeSelection();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(rect, paint);
        drawResizeHandles(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawRect(rect, paint);
        drawResizeHandles(canvas);
    }

    private void drawResizeHandles(Canvas canvas) {
        int handleRadius = handleSize / 3;
        int left = rect.left - handleRadius;
        int top = rect.top - handleRadius;
        int right = rect.right + handleRadius;
        int bottom = rect.bottom + handleRadius;

        // Top-left handle
        canvas.drawCircle(left, top, handleRadius, paint);
        // Top-right handle
        canvas.drawCircle(right, top, handleRadius, paint);
        // Bottom-left handle
        canvas.drawCircle(left, bottom, handleRadius, paint);
        // Bottom-right handle
        canvas.drawCircle(right, bottom, handleRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isTouchWithinHandle(x, y)) {
                    isResizing = true;
                    lastTouchX = x;
                    lastTouchY = y;
                    touchOffsetX = (int) (x - rect.left);
                    touchOffsetY = (int) (y - rect.top);
                } else if (isTouchWithinRect(x, y)) {
                    isDragging = true;
                    lastTouchX = x;
                    lastTouchY = y;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isResizing) {
                    handleResize(x, y);
                } else if (isDragging) {
                    handleDrag(x, y);
                }
                return true;
            case MotionEvent.ACTION_UP:
                isResizing = false;
                isDragging = false;
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleResize(float x, float y) {
        int dx = (int) (x - lastTouchX);
        int dy = (int) (y - lastTouchY);
        if (x < rect.left) {
            rect.left += dx;
        } else if (x > rect.right) {
            rect.right += dx;
        }
        if (y < rect.top) {
            rect.top += dy;
        } else if (y > rect.bottom) {
            rect.bottom += dy;
        }
        lastTouchX = x;
        lastTouchY = y;
        invalidate();
    }

    private void handleDrag(float x, float y) {
        int dx = (int) (x - lastTouchX);
        int dy = (int) (y - lastTouchY);
        rect.offset(dx, dy);
        lastTouchX = x;
        lastTouchY = y;
        invalidate();
    }

    private boolean isTouchWithinRect(float x, float y) {
        return rect.contains((int) x, (int) y);
    }


    private boolean isTouchWithinHandle(float x, float y) {
        int handleRadius = handleSize / 2;
        int left = rect.left - handleRadius;
        int top = rect.top - handleRadius;
        int right = rect.right + handleRadius;
        int bottom = rect.bottom + handleRadius;

        int leftHandleX = left + handleRadius;
        int leftHandleY = (top + bottom) / 2;
        int rightHandleX = right - handleRadius;
        int rightHandleY = (top + bottom) / 2;
        int topHandleX = (left + right) / 2;
        int topHandleY = top - handleRadius;
        int bottomHandleX = (left + right) / 2;
        int bottomHandleY = bottom + handleRadius;

        return Math.pow(x - leftHandleX, 2) + Math.pow(y - leftHandleY, 2) <= Math.pow(handleRadius, 2) ||
                Math.pow(x - rightHandleX, 2) + Math.pow(y - rightHandleY, 2) <= Math.pow(handleRadius, 2) ||
                Math.pow(x - topHandleX, 2) + Math.pow(y - topHandleY, 2) <= Math.pow(handleRadius, 2) ||
                Math.pow(x - bottomHandleX, 2) + Math.pow(y - bottomHandleY, 2) <= Math.pow(handleRadius, 2) ||
                Math.pow(x - left, 2) + Math.pow(y - top, 2) <= Math.pow(handleRadius, 2) ||
                Math.pow(x - right, 2) + Math.pow(y - top, 2) <= Math.pow(handleRadius, 2) ||
                Math.pow(x - left, 2) + Math.pow(y - bottom, 2) <= Math.pow(handleRadius, 2) ||
                Math.pow(x - right, 2) + Math.pow(y - bottom, 2) <= Math.pow(handleRadius, 2);
    }
}
