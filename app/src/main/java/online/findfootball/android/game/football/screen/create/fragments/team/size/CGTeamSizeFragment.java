package online.findfootball.android.game.football.screen.create.fragments.team.size;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.screen.create.BaseCGFragment;
import online.findfootball.android.game.football.screen.create.fragments.team.size.view.CustomNumberPicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class CGTeamSizeFragment extends BaseCGFragment {

    private static final String TAG = App.G_TAG + ":CGTeamSizeFrg";

    private static final int MIN_TEAM_SIZE = 4;
    private static final int MAX_TEAM_SIZE = 11;

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

        numberPicker.setValue((MAX_TEAM_SIZE + MIN_TEAM_SIZE) / 2);
        return rootView;
    }

    @Override
    public void saveResult(GameObj game) {
        game.getTeams().setTeamsCapacity(numberPicker.getValue());
    }

    @Override
    public void updateView(GameObj game) {
        hideSoftKeyboard(); // прячем клавиатуру, если она есть
    }

    @Override
    public boolean verifyData(boolean showToast) {
        return true;
    }
}
