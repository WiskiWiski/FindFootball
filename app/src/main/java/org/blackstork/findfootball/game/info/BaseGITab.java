package org.blackstork.findfootball.game.info;

import android.support.v4.app.Fragment;

import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.user.PublicUserObj;

/**
 * Created by WiskiW on 16.04.2017.
 */

public abstract class BaseGITab extends Fragment {

    public abstract void setData(GameObj game, PublicUserObj gameOwner);

}
