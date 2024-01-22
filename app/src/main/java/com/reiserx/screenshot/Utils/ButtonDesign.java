package com.reiserx.screenshot.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.widget.Button;

import com.reiserx.screenshot.R;

@SuppressLint("UseCompatLoadingForDrawables")
public class ButtonDesign {
    private final Context context;

    public ButtonDesign(Context context) {
        this.context = context;
    }

    public void setButtonOutline(Button button) {
        button.setBackground(context.getDrawable(R.drawable.button_outline));
        button.setTextColor(context.getColor(R.color.PrimaryColor));
    }

    public void buttonFill(Button button) {
        button.setBackground(context.getDrawable(R.drawable.button_filled));
        button.setTextColor(Color.parseColor("#FFFFFF"));
    }
}
