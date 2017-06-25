package online.findfootball.android.game.chat

/**
 * Created by WiskiW on 22.06.2017.
 */
interface Mailbox {

    fun onMessageReceived(pos:Int, msg: MessageObj)

}