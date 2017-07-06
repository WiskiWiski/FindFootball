package online.findfootball.android.app.view.verify.view.pager

/**
 * Created by WiskiW on 05.07.2017.
 */
interface VerifyTabsParent {

    fun onRightSwipe() // после смены таба вправо

    fun onLeftSwipe() // после смены таба влево

    fun saveTabData(tab: VerifycapableTab) // сохранить данные из таба

    fun onDataStateChange(correct: Boolean) // данные в табе обновлены

    fun tryLeftSwipe(): Boolean

    fun tryRightSwipe(): Boolean
}