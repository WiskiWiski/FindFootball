package online.findfootball.android.notification

import android.content.Context

/**
 * Created by WiskiW on 28.06.2017.
 */
interface Notificatable {

    // Генерирует объект NotificationObj из данного экземпляра
    fun generateNotificationData(context: Context): NotificationObj

}