package online.findfootball.android.game.chat

import com.google.firebase.database.DataSnapshot
import online.findfootball.android.app.App
import online.findfootball.android.firebase.database.DatabaseLoader
import online.findfootball.android.firebase.database.children.SelfPackableArrayList

/**
 * Created by WiskiW on 22.06.2017.
 */


class PostBot(val chat: SelfPackableArrayList<MessageObj>) {

    // TODO : Finish this piece of shi.. code
    // 1. загрузка последних N сообщений
    // 2. установка слушателя с последнего сообщения
    // 3. подгрузка старых сообщения при запросе(на скролинг вверх)

    val TAG: String = App.G_TAG + ":PostBot"
    val LOAD_ON_REQUEST = 10 // кол-во сообщений загружаемых при loadMoreOnTop()

    var mailbox: Mailbox? = null
    val loader: DatabaseLoader = DatabaseLoader.newLoader()


    fun startListening() {
        loader.abortAllLoadings()
        loader.listen(chat, object : DatabaseLoader.OnListenListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot) {
                //Log.d("FFB:onChildAdded", "dataSnapshot: " + dataSnapshot)
                val msg = MessageObj()
                msg.unpack(dataSnapshot)
                if (!chat.contains(msg)){
                    chat.add(msg)
                }
                mailbox?.onMessageReceived(chat.size-1, msg) // mailbox != null
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot) {
                //Log.d("FFB:onChildChanged", "dataSnapshot: " + dataSnapshot)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }
        })
    }

    fun stopListening() {
        loader.abortAllLoadings()
    }

    fun isLoading() : Boolean = loader.isLoading

    fun loadMoreOnTop() {

    }

}

