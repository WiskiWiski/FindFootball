package online.findfootball.android.game.football.screen.create.fragments;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.screen.create.BaseCGFragment;


public class CGTitleFragment extends BaseCGFragment {

    private static final String TAG = App.G_TAG + ":CGTitleFrg";

    private static final int MINIMAL_TITLE_LENGTH = 6;

    private EditText editText;
    private GameObj thisGame;

    public CGTitleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cg_fragment_title, container, false);
        editText = (EditText) rootView.findViewById(R.id.edit_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    doneEdit();
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notifyDataStateChange();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (thisGame != null) {
            editText.setText(thisGame.getTitle());
        }
        return rootView;
    }

    private String getTitle() {
        String title = null;
        if (editText != null) {
            title = editText.getText().toString().trim();
        }
        return title;
    }


    @Override
    public void saveResult(GameObj game) {
        game.setTitle(getTitle());
    }

    @Override
    public void updateView(GameObj game) {
        this.thisGame = game;
        if (editText != null){
            editText.requestFocus();
        }
    }

    @Override
    public boolean verifyData(boolean showToast) {
        String title = getTitle();
        if (title == null || title.isEmpty()) {
            if (showToast) {
                Toast.makeText(getContext(), getString(R.string.cg_game_title_frg_empty_title),
                        Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else if (title.length() < MINIMAL_TITLE_LENGTH) {
            if (showToast) {
                Formatter formatter = new Formatter();
                formatter.format(getString(R.string.cg_game_title_frg_too_short_title),
                        MINIMAL_TITLE_LENGTH);
                Toast.makeText(getContext(), formatter.toString(), Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else {
            return true;
        }
        if (editText != null){
            editText.requestFocus();
        }
        return false;
    }

}
