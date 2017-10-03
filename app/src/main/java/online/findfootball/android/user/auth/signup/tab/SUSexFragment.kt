package online.findfootball.android.user.auth.signup.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import online.findfootball.android.R
import online.findfootball.android.game.football.screen.create.fragments.team.size.view.CustomNumberPicker
import online.findfootball.android.user.UserObj
import online.findfootball.android.user.auth.AuthUserObj

/**
 * Created by WiskiW on 12.09.2017.
 */

class SUSexFragment : BaseSUFragment() {

    private var sexPicker: CustomNumberPicker? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.su_fragment_sex, container, false)
        sexPicker = rootView.findViewById(R.id.sex_picker)
        sexPicker?.displayedValues = arrayOf("Male", "Female", "Other")
        sexPicker?.minValue = 0
        sexPicker?.maxValue = 2
        sexPicker?.wrapSelectorWheel = false
        sexPicker?.value = 1

        return rootView
    }

    override fun saveResult(o: Any) {
        if (sexPicker != null) {
            o as AuthUserObj
            when (sexPicker?.value) {
                0 -> o.sex = UserObj.Sex.MALE
                1 -> o.sex = UserObj.Sex.FEMALE
                else -> o.sex = UserObj.Sex.OTHER
            }
        }
    }

    override fun updateView(o: Any) {
        if (sexPicker != null) {
            o as AuthUserObj
            when (o.sex) {
                UserObj.Sex.MALE -> sexPicker?.value = 0
                UserObj.Sex.FEMALE -> sexPicker?.value = 1
                else -> sexPicker?.value = 2
            }
        }
    }

    override fun verifyData(notifyUser: Boolean) = true

}