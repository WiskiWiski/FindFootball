package online.findfootball.android.game.football.screen.create.fragments.time.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

/**
 * Created by WiskiW on 28.05.2017.
 */

public class CustomSpinner extends android.support.v7.widget.AppCompatSpinner {

    private static final int triangleColor = android.R.color.white;


    public CustomSpinner(Context context) {
        super(context);
        setTriangleColor();
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTriangleColor();
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTriangleColor();
    }

    public CustomSpinner(Context context, int mode) {
        super(context, mode);
        setTriangleColor();
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        setTriangleColor();
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
        setTriangleColor();
    }

    private void setTriangleColor() {
        getBackground().setColorFilter(
                ContextCompat.getColor(getContext(), triangleColor), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }
}
