package online.findfootball.android.game.football.screen.create.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getParent().onDataStateChange(verifyData(false));
            }

            @Override
            public void afterTextChanged(Editable s) {

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
    public void saveResult(@NonNull Object game) {
        ((GameObj) game).setDescription(getDescription());
        hideSoftKeyboard();
    }

    @Override
    public void updateView(@NonNull Object game) {
        String desc = ((GameObj) game).getDescription();
        if (desc != null && editText != null) {
            editText.setText(desc);
            editText.setSelection(desc.length());
            editText.requestFocus();
        }
    }

    @Override
    public boolean verifyData(boolean notifyUser) {
        String description = getDescription();
        if (description == null || description.isEmpty()) {
            if (notifyUser) {
                Toast.makeText(getContext(), getString(R.string.cg_game_description_frg_empty_description),
                        Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else if (description.length() < MINIMAL_DESCRIPTION_LENGTH) {
            if (notifyUser) {
                Formatter formatter = new Formatter();
                formatter.format(getString(R.string.cg_game_description_frg_too_short_description),
                        MINIMAL_DESCRIPTION_LENGTH);
                Toast.makeText(getContext(), formatter.toString(), Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else {
            return true;
        }
        if (editText != null) {
            editText.requestFocus();
        }
        return false;
    }
}
