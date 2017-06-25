package online.findfootball.android.game.football.screen.info.tabs.chat.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import online.findfootball.android.R
import online.findfootball.android.game.chat.MessageObj
import online.findfootball.android.game.chat.OutComingMessage

/**
 * Created by WiskiW on 22.06.2017.
 */
class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val OUT_COMING_MSG_VIEW_TYPE = 0
    private val INCOMING_MSG_VIEW_TYPE = 1

    var messageList: ArrayList<MessageObj> = arrayListOf()

    override fun getItemViewType(position: Int): Int {
        if (messageList[position] is OutComingMessage) {
            return OUT_COMING_MSG_VIEW_TYPE
        } else {
            return INCOMING_MSG_VIEW_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder?.itemViewType) {
            OUT_COMING_MSG_VIEW_TYPE -> {
                val holderOutComing: OutComingMessageViewHolder = holder as OutComingMessageViewHolder
                holderOutComing.message = messageList[position] as OutComingMessage
            }
            INCOMING_MSG_VIEW_TYPE -> {
                val holderIncoming: IncomingMessageViewHolder = holder as IncomingMessageViewHolder
                holderIncoming.message = messageList[position]
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    fun updateMessageStatus(pos: Int, status: OutComingMessage.SendStatus) {
        val msg = messageList[pos]
        if (msg is OutComingMessage) {
            msg.sendStatus = status
            notifyItemChanged(pos)
        }
    }


    fun indexOf(msg: MessageObj): Int {
        return messageList.indexOf(msg)
    }

    fun addMessage(newMsg: MessageObj) {
        if (!messageList.contains(newMsg)){
            addMessage(messageList.size, newMsg)
        }
    }

    fun addMessage(pos: Int, msg: MessageObj) {
        if (pos < 0) {
            messageList.add(0, msg)
            notifyItemInserted(0)
        } else {
            val size = messageList.size
            if (pos > size) {
                messageList.add(msg)
                notifyItemInserted(size - 1)
            } else {
                messageList.add(pos, msg)
                notifyItemInserted(pos)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            OUT_COMING_MSG_VIEW_TYPE -> {
                val itemView = LayoutInflater.from(parent?.context)
                        .inflate(R.layout.chat_cloud_out_coming, parent, false)
                return OutComingMessageViewHolder(itemView)
            }
            INCOMING_MSG_VIEW_TYPE -> {
                val itemView = LayoutInflater.from(parent?.context)
                        .inflate(R.layout.chat_cloud_incoming, parent, false)
                return IncomingMessageViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent?.context)
                        .inflate(R.layout.chat_cloud_incoming, parent, false)
                return IncomingMessageViewHolder(itemView)
            }
        }

    }


}