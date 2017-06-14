package online.findfootball.android.game.football.screen.create.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.screen.create.BaseCGFragment;
import online.findfootball.android.game.football.screen.create.PagerAdapter;

import static java.lang.Math.abs;


/**
 * Created by WiskiW on 02.04.2017.
 */

public class CreateGameViewPager extends ViewPager {

    private static final String TAG = App.G_TAG + ":CreateGameViewPage";

    // погрешность для акцивации проверки ввода при свайпе вправо
    private static final int MINIMAL_SWIPE_OFFSET = 100;

    public GameObj thisGameObj;
    private CreateGameListener createGameListener;

    private float initialXValue;
    private int pressState = 0;

    public CreateGameViewPager(Context context) {
        super(context);
    }

    public CreateGameViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCreateGameListener(CreateGameListener createGameListener) {
        this.createGameListener = createGameListener;
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
                    CreateGameViewPager.this.createGameListener.onNextTab();
                } else {
                    // Swiped Left
                    CreateGameViewPager.this.createGameListener.onPreviewTab();
                }
                previewPos = state;
            }
        });
    }

    public GameObj getGameObj() {
        return thisGameObj;
    }

    public void setGameObj(GameObj thisGameObj) {
        this.thisGameObj = thisGameObj;
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
                            BaseCGFragment cgFragment = getCurrentFragment();
                            if (cgFragment.verifyData(pressState < 2)) {
                                cgFragment.hideSoftKeyboard();
                                cgFragment.saveResult(getGameObj());
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else if (diffX > 0) {
                        // swipe from left to right detected
                        return true;
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

    @Override
    public void setAdapter(android.support.v4.view.PagerAdapter adapter) {
        super.setAdapter(adapter);
        getCurrentFragment().updateView(getGameObj());
    }

    public boolean tryGoNext() {
        // Возвращает был ли сменен таб
        if (getCurrentFragment().verifyData(true)) {
            // сохраняем данных с таба
            getCurrentFragment().saveResult(getGameObj());
            if (hasNext()) {
                goNext();
                getCurrentFragment().updateView(getGameObj());
                return true;
            } else if (createGameListener != null) {
                createGameListener.onGameCreated(getGameObj());
            }
            return false;
        }
        return false;
    }

    public boolean tryGoBack() {
        if (!hasPreview()) {
            return false;
        }
        // сохраняем данных с предыдущего таба
        getCurrentFragment().saveResult(getGameObj());

        goBack();
        // обновляем данные в следующем табе
        getCurrentFragment().updateView(getGameObj());
        return true;
    }

    private void goNext() {
        if (hasNext()) {
            setCurrentItem(getCurrentItem() + 1);
        }
    }

    private void goBack() {
        if (hasPreview()) {
            setCurrentItem(getCurrentItem() - 1);
        }
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

    public interface CreateGameListener {
        void onGameCreated(GameObj game);

        void onNextTab();

        void onPreviewTab();
    }

}