package online.findfootball.android.game.football.screen.info.tabs.chat.recyclerview

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import online.findfootball.android.R
import online.findfootball.android.game.chat.MessageObj
import online.findfootball.android.game.chat.OutComingMessage

/**
 * Created by WiskiW on 22.06.2017.
 */
class OutComingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var message: OutComingMessage = OutComingMessage(MessageObj())
        set(value) {
            val msgText: TextView = itemView.findViewById(R.id.msg_edit_text) as TextView
            msgText.text = value.text


            var color = ContextCompat.getColor(itemView.context, android.R.color.holo_orange_light)
            if (value.sendStatus == OutComingMessage.SendStatus.OK) {
                color = ContextCompat.getColor(itemView.context, android.R.color.holo_green_light)
            }
            msgText.setBackgroundColor(color)
        }

}