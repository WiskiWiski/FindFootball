package org.blackstork.findfootball.game.create.fragments;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.game.create.BaseCGFragment;
import org.blackstork.findfootball.game.GameObj;

import java.util.Formatter;


public class CGTitleFragment extends BaseCGFragment {

    private static final String TAG = App.G_TAG + ":CGNameFrg";

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
        if (thisGame != null) {
            editText.setText(thisGame.getTitle());
        }

        return rootView;
    }

    private String getTitle(boolean checkForCorrect) {
        String title = null;
        if (editText != null) {
            title = editText.getText().toString().trim();
            if (checkForCorrect && !verifyTitle(title)) {
                editText.requestFocus();
                return null;
            }
        }
        return title;
    }

    private boolean verifyTitle(String title) {
        if (title.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.cg_game_title_frg_empty_title),
                    Toast.LENGTH_LONG).show();
        } else if (title.length() < MINIMAL_TITLE_LENGTH) {
            Formatter formatter = new Formatter();
            formatter.format(getString(R.string.cg_game_title_frg_too_short_title),
                    MINIMAL_TITLE_LENGTH);
            Toast.makeText(getContext(), formatter.toString(), Toast.LENGTH_LONG).show();
        } else {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveResult(boolean checkForCorrect, GameObj game) {
        String title = getTitle(checkForCorrect);
        if (title != null) {
            game.setTitle(title);
            return true;
        }
        return !checkForCorrect;
    }

    @Override
    public void updateView(GameObj game) {
        this.thisGame = game;
    }

}
