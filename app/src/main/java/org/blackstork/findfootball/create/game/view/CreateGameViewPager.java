package org.blackstork.findfootball.create.game.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.blackstork.findfootball.create.game.BaseCGFragment;
import org.blackstork.findfootball.create.game.PagerAdapter;

/**
 * Created by WiskiW on 02.04.2017.
 */

public class CreateGameViewPager extends ViewPager {

    public CreateGameViewPager(Context context) {
        super(context);
    }

    public CreateGameViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    public boolean goNext() {
        if  (hasNext()){
            setCurrentItem(getCurrentItem() + 1);
            return true;
        } else {
            return false;
        }
    }

    public boolean goBack() {
        if (hasPreview()) {
            setCurrentItem(getCurrentItem() - 1);
            return true;
        }
        return false;
    }

    public boolean hasPreview() {
        return !(getCurrentItem() - 1 < 0);
    }

    public boolean hasNext() {
        return !(getCurrentItem() + 1 >= getAdapter().getCount());
    }

    public BaseCGFragment getCurrentFragment() {
        return ((PagerAdapter) getAdapter()).getItem(getCurrentItem());
    }

}