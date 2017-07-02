package online.findfootball.android.game.chat

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import online.findfootball.android.firebase.database.children.PackableArrayList

/**
 * Created by WiskiW on 30.06.2017.
 */

class GameChatObj : PackableArrayList<MessageObj> {

    companion object {
        private val CHAT_KEY = "chat"

        @JvmField val CREATOR: Parcelable.Creator<GameChatObj> =
                object : Parcelable.Creator<GameChatObj> {
                    override fun createFromParcel(source: Parcel): GameChatObj {
                        return GameChatObj(source)
                    }

                    override fun newArray(size: Int): Array<GameChatObj?> {
                        return arrayOfNulls(size)
                    }
                }
    }

    constructor()

    constructor(source: Parcel) : super(source)

    override fun newItem(itemSnapshot: DataSnapshot): MessageObj {
        val msg = MessageObj()
        msg.setPackableKey(itemSnapshot.key.toString())
        msg.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey())
        return msg
    }

    override fun add(element: MessageObj?): Boolean {
        element?.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey())
        return super.add(element)
    }

    override fun getPackableKey(): String = CHAT_KEY
}