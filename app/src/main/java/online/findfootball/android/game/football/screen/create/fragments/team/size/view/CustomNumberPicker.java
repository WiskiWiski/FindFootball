package online.findfootball.android.game.football.screen.create.fragments.team.size.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by WiskiW on 30.05.2017.
 */

public class CustomNumberPicker extends NumberPicker {

    private static final int PICKER_COLOR = Color.WHITE;
    private static final int PICKER_TEXT_SIZE = 24;

    public CustomNumberPicker(Context context) {
        super(context);
        setDividerColor();
    }

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDividerColor();
    }

    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDividerColor();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setDividerColor();
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    private void updateView(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextSize(PICKER_TEXT_SIZE);
            ((EditText) view).setTextColor(PICKER_COLOR);
        }
    }

    private void setDividerColor() {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(PICKER_COLOR);
                    pf.set(this, colorDrawable);
                } catch (IllegalArgumentException | Resources.NotFoundException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
