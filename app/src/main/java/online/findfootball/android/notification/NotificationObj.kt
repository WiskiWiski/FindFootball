package online.findfootball.android.notification

import android.content.Context
import android.content.Intent
import online.findfootball.android.R
import java.util.*

/**
 * Created by WiskiW on 26.06.2017.
 */
class NotificationObj : Notificatable {

    override fun generateNotificationData(context: Context): NotificationObj = this

    var clickAction: Intent = Intent()

    var nLargeIcon = R.mipmap.ic_launcher
    var nSmallIcon = R.mipmap.ic_launcher

    var nTitle = ""
    var nText = ""
    var nTicker = ""
    var nId = Random().nextInt()

}