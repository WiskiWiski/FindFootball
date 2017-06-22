package online.findfootball.android.game.chat

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import online.findfootball.android.firebase.database.DataInstanceResult
import online.findfootball.android.firebase.database.children.PackableObject
import online.findfootball.android.time.TimeProvider
import online.findfootball.android.user.UserObj

/**
 * Created by WiskiW on 22.06.2017.
 */
class MessageObj() : PackableObject(), Parcelable {

    val PATH_MESSAGE = "body/"
    val PATH_USER_FROM = "from/"

    var time: Long = TimeProvider.getUtcTime()
    var userFrom: UserObj = UserObj.EMPTY
    var message: String = ""

    override fun pack(databaseReference: DatabaseReference): DataInstanceResult {
        val result = DataInstanceResult.onSuccess()

        databaseReference.child(PATH_MESSAGE).setValue(message)

        if (userFrom != UserObj.EMPTY) {
            databaseReference.child(PATH_USER_FROM).setValue(userFrom.uid)
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

        val uncheckedMessage = dataSnapshot.child(PATH_MESSAGE)
        if (uncheckedMessage != null) {
            message = uncheckedMessage.toString()
        } else {
            DataInstanceResult.calculateResult(result,
                    DataInstanceResult.notComplete("Message is empty!"))
        }


        val uncheckedUidFrom = dataSnapshot.child(PATH_USER_FROM)
        if (uncheckedUidFrom != null) {
            userFrom = UserObj(uncheckedUidFrom.toString())
        } else return DataInstanceResult(DataInstanceResult.CODE_PARSING_FAILED, "User-from not found!")

        return DataInstanceResult.onSuccess()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(message)
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
        message = incoming.readString()
        time = incoming.readLong()
        userFrom = incoming.readParcelable(UserObj::class.java.classLoader)
    }

    override fun describeContents(): Int = 0

}
