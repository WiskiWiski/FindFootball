package org.blackstork.findfootball.game.create.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.game.create.BaseCGFragment;

import java.util.Formatter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CGDescriptionFragment extends BaseCGFragment {


    private static final String TAG = App.G_TAG + ":CGDescriptionFrg";

    private static final int MINIMAL_DESCRIPTION_LENGTH = 12;

    private EditText editText;


    public CGDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cg_fragment_description, container, false);
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
        return rootView;
    }

    private String getDescription(boolean checkForCorrect) {
        String description = null;
        if (editText != null) {
            description = editText.getText().toString().trim();
            if (checkForCorrect && !verifyDescription(description)) {
                editText.requestFocus();
                return null;
            }
        }
        return description;
    }

    private boolean verifyDescription(String description) {
        if (description.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.cg_game_description_frg_empty_description),
                    Toast.LENGTH_LONG).show();
        } else if (description.length() < MINIMAL_DESCRIPTION_LENGTH) {
            Formatter formatter = new Formatter();
            formatter.format(getString(R.string.cg_game_description_frg_too_short_description),
                    MINIMAL_DESCRIPTION_LENGTH);
            Toast.makeText(getContext(), formatter.toString(), Toast.LENGTH_LONG).show();
        } else {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveResult(boolean checkForCorrect, GameObj game) {
        String description = getDescription(checkForCorrect);
        if (description != null) {
            game.setDescription(description);
            return true;
        }
        return !checkForCorrect;
    }

    @Override
    public void updateView(GameObj game) {
        editText.setText(game.getDescription());
    }
}
