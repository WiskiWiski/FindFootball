package online.findfootball.android.game.chat

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import online.findfootball.android.firebase.database.DataInstanceResult
import online.findfootball.android.firebase.database.children.PackableObject
import online.findfootball.android.time.TimeProvider
import online.findfootball.android.user.UserObj
import java.util.*


/**
 * Created by WiskiW on 22.06.2017.
 */
open class MessageObj() : PackableObject(), Parcelable {

    val PATH_MESSAGE = "text"
    val PATH_USER_FROM = "from"

    var time: Long = TimeProvider.getUtcTime()
    var userFrom: UserObj = UserObj.EMPTY
    var text: String = ""

    override fun pack(databaseReference: DatabaseReference): DataInstanceResult {
        val result = DataInstanceResult.onSuccess()

        val map = HashMap<String, kotlin.Any>()
        map.put(PATH_MESSAGE, text)

        if (userFrom != UserObj.EMPTY) {
            map.put(PATH_USER_FROM, userFrom.uid)
        } else {
            DataInstanceResult.calculateResult(result,
                    DataInstanceResult.notComplete("User-from is empty!"))
        }

        databaseReference.setValue(map)
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

    override fun equals(other: Any?): Boolean {
        return other is MessageObj && other.time == this.time && other.text == this.text
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
        dest.writeString(text)
        dest.writeLong(time)
        dest.writeParcelable(userFrom, flags)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<MessageObj> = object : Parcelable.Creator<MessageObj> {
            override fun createFromParcel(incoming: Parcel): MessageObj {
                return MessageObj(incoming)
            }

            override fun newArray(size: Int): Array<MessageObj?> {
                return arrayOfNulls(size)
            }
        }

    }

    constructor(incoming: Parcel) : this() {
        text = incoming.readString()
        time = incoming.readLong()
        userFrom = incoming.readParcelable(UserObj::class.java.classLoader)
    }

    override fun describeContents(): Int = 0

}
