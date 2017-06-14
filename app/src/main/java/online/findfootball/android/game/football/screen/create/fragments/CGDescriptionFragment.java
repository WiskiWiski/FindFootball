package online.findfootball.android.game.football.screen.create.fragments;


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

import java.util.Formatter;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.screen.create.BaseCGFragment;

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

    private String getDescription() {
        String description = null;
        if (editText != null) {
            description = editText.getText().toString().trim();
        }
        return description;
    }


    @Override
    public void saveResult(GameObj game) {
        game.setDescription(getDescription());
    }

    @Override
    public void updateView(GameObj game) {
        String desc = game.getDescription();
        if (desc != null && editText != null){
            editText.setText(desc);
            editText.setSelection(desc.length());
        }
    }

    @Override
    public boolean verifyData(boolean showToast) {
        String description = getDescription();
        if (description == null || description.isEmpty()) {
            if (showToast) {
                Toast.makeText(getContext(), getString(R.string.cg_game_description_frg_empty_description),
                        Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else if (description.length() < MINIMAL_DESCRIPTION_LENGTH) {
            if (showToast) {
                Formatter formatter = new Formatter();
                formatter.format(getString(R.string.cg_game_description_frg_too_short_description),
                        MINIMAL_DESCRIPTION_LENGTH);
                Toast.makeText(getContext(), formatter.toString(), Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else {
            return true;
        }
        editText.requestFocus();
        return false;
    }
}
