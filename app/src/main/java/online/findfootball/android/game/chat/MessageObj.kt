package online.findfootball.android.game.chat

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import online.findfootball.android.firebase.database.DataInstanceResult
import online.findfootball.android.firebase.database.children.PackableObject
import online.findfootball.android.firebase.messaging.RemoteMessageContent
import online.findfootball.android.game.football.screen.my.MyGamesActivity
import online.findfootball.android.notification.Notificatable
import online.findfootball.android.notification.NotificationObj
import online.findfootball.android.time.TimeProvider
import online.findfootball.android.user.UserObj
import java.util.*


/**
 * Created by WiskiW on 22.06.2017.
 */
open class MessageObj : PackableObject, RemoteMessageContent, Notificatable {


    companion object {
        private val PATH_MESSAGE = "text"
        private val PATH_USER_FROM = "from"
        @JvmField val CREATOR: Parcelable.Creator<MessageObj> =
                object : Parcelable.Creator<MessageObj> {
                    override fun createFromParcel(source: Parcel): MessageObj {
                        return MessageObj(source)
                    }

                    override fun newArray(size: Int): Array<MessageObj?> {
                        return arrayOfNulls(size)
                    }
                }
    }

    var time: Long = TimeProvider.getUtcTime()
    var userFrom: UserObj = UserObj.EMPTY
    var text: String = ""

    constructor()

    constructor(source: Parcel) : super(source) {
        text = source.readString()
        time = source.readLong()
        userFrom = source.readParcelable(UserObj::class.java.classLoader)
    }

    override fun setPackableKey(key: String?) {
        if (key != null) {
            this.time = key.toLong()
        } else {
            super.setPackableKey(key)
        }
    }

    override fun getPackableKey(): String? {
        return time.toString()
    }

    override fun pack(databaseMap: HashMap<String, Any>): DataInstanceResult {
        val result = DataInstanceResult.onSuccess()

        databaseMap.put(PATH_MESSAGE, text)

        if (userFrom != UserObj.EMPTY) {
            databaseMap.put(PATH_USER_FROM, userFrom.uid)
        } else {
            DataInstanceResult.calculateResult(result,
                    DataInstanceResult.notComplete("User-from is empty!"))
        }
        return result
    }

    override fun unpack(dataSnapshot: DataSnapshot): DataInstanceResult {
        val result = DataInstanceResult.onSuccess()

        val uncheckedTime = dataSnapshot.key
        if (uncheckedTime != null) {
            time = (uncheckedTime).toLong()
        } else {
            return DataInstanceResult(DataInstanceResult.CODE_PARSING_FAILED, "Message time not found!")
        }

        val uncheckedMessage = dataSnapshot.child(PATH_MESSAGE).value
        if (uncheckedMessage != null) {
            text = uncheckedMessage.toString()
        } else {
            DataInstanceResult.calculateResult(result,
                    DataInstanceResult.notComplete("Message is empty!"))
        }


        val uncheckedUidFrom = dataSnapshot.child(PATH_USER_FROM).value
        if (uncheckedUidFrom != null) {
            userFrom = UserObj(uncheckedUidFrom.toString())
        } else
            return DataInstanceResult(DataInstanceResult.CODE_PARSING_FAILED, "User-from not found!")

        return DataInstanceResult.onSuccess()
    }

    override fun hasUnpacked(): Boolean {
        return userFrom.hasUnpacked() && !text.isEmpty()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }

        if (other is MessageObj) {
            return other.time == this.time && other.text == this.text
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        var result = 1
        result = 31 * result + time.hashCode()
        result = 31 * result + userFrom.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }

    override fun toString(): String {
        return "MessageObj: $userFrom in ${time}ms say '$text'"
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(text)
        dest.writeLong(time)
        dest.writeParcelable(userFrom, flags)
    }

    override fun rebuildByMessageData(contentMap: Map<String, String>): Boolean {
        var result: Boolean = true

        val uidFrom: String? = contentMap["from"]
        if (uidFrom != null) {
            this.userFrom = UserObj(uidFrom)
        } else {
            result = false
        }

        val timeString: String? = contentMap["time"]
        if (timeString != null) {
            this.time = timeString.toLong()
        } else {
            result = false
        }

        val text: String? = contentMap["text"]
        if (text != null) {
            this.text = text
        } else {
            result = false
        }

        return result
    }

    override fun generateNotificationData(context: Context): NotificationObj {
        val nObject = NotificationObj()
        nObject.nId = 2
        nObject.nTitle = "New Message from ${userFrom.email}"
        nObject.nText = "Text: $text"
        nObject.clickAction = Intent(context, MyGamesActivity::class.java)
        return nObject
    }

}
