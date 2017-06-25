package online.findfootball.android.game.football.screen.info;

import android.support.v4.app.Fragment;

import online.findfootball.android.game.GameObj;
import online.findfootball.android.user.UserObj;

/**
 * Created by WiskiW on 16.04.2017.
 */

public abstract class BaseGITab extends Fragment {

    public abstract void setData(GameObj game, UserObj gameOwner);

}
