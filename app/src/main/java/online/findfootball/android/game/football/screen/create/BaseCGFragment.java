package online.findfootball.android.game.football.screen.create;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import online.findfootball.android.app.App;
import online.findfootball.android.app.view.verify.view.pager.VerifyTabsParent;
import online.findfootball.android.app.view.verify.view.pager.VerifycapableTab;

/**
 * Created by WiskiW on 02.04.2017.
 */

public abstract class BaseCGFragment extends Fragment implements VerifycapableTab {

    private static final String TAG = App.G_TAG + ":BaseCGFrg";
    private static final long VIBRATION_DURATION = 25;

    private VerifyTabsParent parent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            parent = (VerifyTabsParent) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getName()
                    + " must implement VerifyTabsParent");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parent = null;
    }

    protected VerifyTabsParent getParent(){
        return this.parent;
    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            Log.w(TAG, "HideSoftKeyboard: couldn't hide the keyboard!");
        }
    }

    protected void vibrate() {
        App.vibrate(getContext(), VIBRATION_DURATION);
    }

    @Override
    public boolean isDifficultToSwipe() {
        return false;
    }
}
