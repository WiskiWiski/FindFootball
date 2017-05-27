package online.findfootball.android.game.create;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;

/**
 * Created by WiskiW on 02.04.2017.
 */

public abstract class BaseCGFragment extends Fragment {

    private static final String TAG = App.G_TAG + ":BaseCGFrg";
    private static final long VIBRATION_DURATION = 35;

    private CGTabEditListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (CGTabEditListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getName()
                    + " must implement CGTabEditListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    interface CGTabEditListener {
        void onDoneEdit();
    }

    protected void doneEdit() {
        hideSoftKeyboard();
        if (callback != null) {
            callback.onDoneEdit();
        }
    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            Log.w(TAG, "HideSoftKeyboard: couldn't hide the keyboard!", e);
        }
    }

    protected void vibrate() {
        App.vibrate(getContext(), VIBRATION_DURATION);
    }

    public abstract void saveResult(GameObj game); // return : были ли данных сохранены успешно

    public abstract void updateView(GameObj game);

    public abstract boolean verifyData(boolean showToast); // проверка данных на корректность

}
