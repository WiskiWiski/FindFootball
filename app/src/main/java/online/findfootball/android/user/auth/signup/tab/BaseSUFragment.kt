package online.findfootball.android.user.auth.signup.tab

import android.support.v4.app.Fragment
import online.findfootball.android.app.view.verify.view.pager.VerifycapableTab

/**
 * Created by WiskiW on 06.07.2017.
 */
abstract class BaseSUFragment : Fragment(), VerifycapableTab {
    override fun isDifficultToSwipe(): Boolean = false
}