package online.findfootball.android.app.view.verify.view.pager;

import android.content.Context;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import online.findfootball.android.app.App;

import static java.lang.Math.abs;


/**
 * Created by WiskiW on 02.04.2017.
 */

public class VerifyTabViewPager extends ViewPager {

    private static final String TAG = App.G_TAG + ":VerifyViewPager";

    // погрешность для акцивации проверки ввода при свайпе вправо
    private static final int MINIMAL_SWIPE_OFFSET = 100;

    private VerifyTabsParent parent;

    private float initialXValue;
    private int pressState = 0;

    public VerifyTabViewPager(Context context) {
        super(context);
    }

    public VerifyTabViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParent(VerifyTabsParent parent) {
        this.parent = parent;
        addOnPageChangeListener(new OnPageChangeListener() {
            int previewPos;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (previewPos > state) {
                    //Swiped Right
                    VerifyTabViewPager.this.parent.onRightSwipe();
                } else {
                    // Swiped Left
                    VerifyTabViewPager.this.parent.onLeftSwipe();
                }
                previewPos = state;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isNextAllowed(event)) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isNextAllowed(event)) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    private boolean isNextAllowed(MotionEvent event) {
        // https://stackoverflow.com/questions/19602369/how-to-disable-viewpager-from-swiping-in-one-direction
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialXValue = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                try {
                    float diffX = event.getX() - initialXValue;
                    if (diffX < 0) {
                        // swipe from right to left detected
                        if (abs(diffX) >= MINIMAL_SWIPE_OFFSET) {
                             /*
                               т. к. данный код вызывается несколько раз за один скайп,
                               pressState хранит количество вызовов, для показа сообщения
                               из verifyData() только один раз
                             */
                            pressState++;
                            VerifycapableTab tab = getCurrentTab();
                            if (!tab.isDifficultToSwipe() && tab.verifyData(pressState < 2)) {
                                parent.saveTabData(tab);
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else if (diffX > 0) {
                        // swipe from left to right detected
                        return !getCurrentTab().isDifficultToSwipe();
                    } else {
                        return false;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pressState = 0;
        }
        return true;
    }

    public void goNext() {
        setCurrentItem(getCurrentItem() + 1);
    }

    public void goBack() {
        setCurrentItem(getCurrentItem() - 1);
    }

    public boolean hasPreview() {
        return !(getCurrentItem() - 1 < 0);
    }

    public boolean hasNext() {
        return !(getCurrentItem() + 1 >= getAdapter().getCount());
    }

    public VerifycapableTab getCurrentTab() {
        return (VerifycapableTab) ((FragmentStatePagerAdapter) getAdapter()).getItem(getCurrentItem());
    }
}