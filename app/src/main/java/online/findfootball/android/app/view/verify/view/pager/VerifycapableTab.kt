package online.findfootball.android.app.view.verify.view.pager

/**
 * Created by WiskiW on 05.07.2017.
 */
interface VerifycapableTab {

    fun saveResult(o: Any) // Сохраняет данные из таба; return : были ли данных сохранены успешно

    fun updateView(o: Any) // Обновляет вью таба по данным

    fun verifyData(notifyUser: Boolean): Boolean // Проверяет данные таба на корректность

    fun swipeble(): Boolean // Таб с проблематичной регистрацией свайпов

    fun getParent(): VerifyTabsParent?
}