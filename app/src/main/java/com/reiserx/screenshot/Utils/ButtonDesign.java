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

    public void setButtonOutlineDark(Button button) {
        button.setBackground(context.getDrawable(R.drawable.button_outline_dark));
        button.setTextColor(context.getColor(R.color.button_design_text));
    }

    public void buttonFillDark(Button button) {
        button.setBackground(context.getDrawable(R.drawable.button_filled));
        button.setTextColor(Color.parseColor("#FFFFFF"));
    }

    public void setButtonOutlineLight(Button button) {
        button.setBackground(context.getDrawable(R.drawable.button_outline_light));
        button.setTextColor(context.getColor(R.color.PrimaryColorNight));
    }

    public void buttonFillLight(Button button) {
        button.setBackground(context.getDrawable(R.drawable.button_filled_light));
        button.setTextColor(context.getColor(R.color.PrimaryColor));
    }
}
