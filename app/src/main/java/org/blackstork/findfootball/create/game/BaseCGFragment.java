package org.blackstork.findfootball.create.game;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.objects.GameObj;

/**
 * Created by WiskiW on 02.04.2017.
 */

public abstract class BaseCGFragment extends Fragment {

    private static final String TAG = App.G_TAG + ":BaseCGFrg";

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

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e){
            Log.w(TAG, "HideSoftKeyboard: couldn't hide the keyboard!", e);
        }
    }

    public abstract boolean saveResult(GameObj game); // return : были ли данных сохранены успешно

    public abstract void updateView(GameObj game);

}
