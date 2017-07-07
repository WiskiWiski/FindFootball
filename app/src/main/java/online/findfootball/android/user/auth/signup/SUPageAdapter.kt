package online.findfootball.android.user.auth.signup

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import online.findfootball.android.user.auth.signup.tab.BaseSUFragment
import java.util.*

/**
 * Created by WiskiW on 06.07.2017.
 */
class SUPageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var list: MutableList<BaseSUFragment> = ArrayList()

    fun addNext(tab: BaseSUFragment) = list.add(tab)

    override fun getItem(position: Int): BaseSUFragment? {
        if (position >= count) return null
        return list[position]
    }

    override fun getCount() = list.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as BaseSUFragment
        list[position] = fragment
        return fragment
    }

}