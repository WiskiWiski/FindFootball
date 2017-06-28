package online.findfootball.android.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.app.NotificationCompat


/**
 * Created by WiskiW on 26.06.2017.
 */

class NotificationBuilder {

    companion object {
        @JvmStatic fun show(context: Context, notificatable: Notificatable) {
            val nObject = notificatable.generateNotificationData(context)
            val mBuilder = NotificationCompat.Builder(context)
                    .setSmallIcon(nObject.nSmallIcon)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, nObject.nLargeIcon))
                    .setContentTitle(nObject.nTitle)
                    .setTicker(nObject.nTicker)
                    .setContentText(nObject.nText)
            mBuilder.setContentIntent(PendingIntent.getActivity(context, 0,
                    nObject.clickAction, PendingIntent.FLAG_CANCEL_CURRENT))

            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            mNotificationManager.notify(nObject.nId, mBuilder.build())
        }
    }


}