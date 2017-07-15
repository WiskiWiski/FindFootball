package online.findfootball.android.user.auth.signup.tab

import android.content.Context
import android.support.v4.app.Fragment
import online.findfootball.android.app.App
import online.findfootball.android.app.view.verify.view.pager.VerifyTabsParent
import online.findfootball.android.app.view.verify.view.pager.VerifycapableTab

/**
 * Created by WiskiW on 06.07.2017.
 */
abstract class BaseSUFragment : Fragment(), VerifycapableTab {
    private var parent : VerifyTabsParent? = null
    val VIBRATION_DURATION: Long = 25
    override fun isDifficultToSwipe(): Boolean = false
    protected fun vibrate() {
        App.vibrate(context, VIBRATION_DURATION)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            parent = context as VerifyTabsParent
        } catch (e: ClassCastException) {
            throw ClassCastException(context?.javaClass?.name + " must implement VerifyTabsParent")
        }

    }

    override fun onDetach() {
        super.onDetach()
        parent = null
    }

    override fun getParent(): VerifyTabsParent? = parent
}