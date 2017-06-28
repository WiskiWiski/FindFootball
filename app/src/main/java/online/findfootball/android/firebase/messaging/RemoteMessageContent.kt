package online.findfootball.android.firebase.messaging

/**
 * Created by WiskiW on 28.06.2017.
 */
interface RemoteMessageContent {

    // Заполняет поля экземпляра по данным сообщения Firebase Cloud
    fun rebuildByMessageData(contentMap: Map<String, String>): Boolean

}