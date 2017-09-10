package online.findfootball.android.firebase.messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import online.findfootball.android.app.App
import online.findfootball.android.game.chat.MessageObj
import online.findfootball.android.notification.NotificationBuilder
import online.findfootball.android.user.UserObj


/**
 * Created by WiskiW on 26.06.2017.
 */
open class FFBFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val TAG = App.G_TAG + ":FFB_FMS"

        private val REMOTE_MSG_TYPE_KEY = "type"

        private val TYPE_VALUE_MESSAGE_OBJ = "chat_message" // объект типа сообщение чата

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage != null) {
            val remoteMessageData = remoteMessage.data
            val msgType = remoteMessageData[REMOTE_MSG_TYPE_KEY]
            when (msgType) {
                TYPE_VALUE_MESSAGE_OBJ -> {
                    val message = MessageObj()
                    if (message.rebuildByMessageData(remoteMessageData)) {

                        // Подгружаем пользователя
                        message.userFrom.load { result, packable ->
                            message.userFrom = packable as UserObj

                            // Отображение пуш-уведомления
                            NotificationBuilder.show(applicationContext, message)
                        }
                    } else {
                        badRemoteMessageData(remoteMessageData)
                    }
                }
                else -> {
                    unknownContentType(msgType.toString())
                }
            }
        } else {
            Log.d(TAG, "onMessageReceived: null!")
        }
    }

    private fun badRemoteMessageData(remoteMessageData: Map<String, String>) {
        Log.d(TAG, "badRemoteMessageData: ${remoteMessageData[REMOTE_MSG_TYPE_KEY]}")
    }

    private fun unknownContentType(contentType: String) {
        Log.d(TAG, "unknownContentType: unknown content type: $contentType")
    }


}