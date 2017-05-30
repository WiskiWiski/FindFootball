package online.findfootball.android.game.create.fragments.team.size;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.create.BaseCGFragment;
import online.findfootball.android.game.create.fragments.team.size.view.CustomNumberPicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class CGTeamSizeFragment extends BaseCGFragment {

    private static final String TAG = App.G_TAG + ":CGTeamSizeFrg";

    private static final int MIN_TEAM_SIZE = 2;
    private static final int MAX_TEAM_SIZE = 6;

    private CustomNumberPicker numberPicker;

    public CGTeamSizeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cg_fragment_team_size, container, false);

        numberPicker = (CustomNumberPicker) rootView.findViewById(R.id.number_picker);
        numberPicker.setWrapSelectorWheel(false); // отключение круговой прокрутки
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // отключение доступа к клавиатуре
        numberPicker.setMinValue(MIN_TEAM_SIZE);
        numberPicker.setMaxValue(MAX_TEAM_SIZE);

        //noinspection ConstantConditions
        if ((MIN_TEAM_SIZE + 1) < MAX_TEAM_SIZE) {
            numberPicker.setValue(MIN_TEAM_SIZE + 1);
        }
        return rootView;
    }

    @Override
    public void saveResult(GameObj game) {

    }

    @Override
    public void updateView(GameObj game) {

    }

    @Override
    public boolean verifyData(boolean showToast) {
        return true;
    }
}
