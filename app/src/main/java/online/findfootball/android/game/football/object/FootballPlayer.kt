package online.findfootball.android.game.football.`object`

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import online.findfootball.android.firebase.database.DataInstanceResult
import online.findfootball.android.firebase.database.children.PackableObject
import online.findfootball.android.user.AppUser
import online.findfootball.android.user.UserObj
import java.util.*


/**
 * Created by WiskiW on 30.06.2017.
 */
class FootballPlayer : PackableObject {

    var user: UserObj = UserObj.EMPTY
    var teamName = ""
    var chatNotifications = true

    companion object {
        private val KEY_TEAM = "team_name"
        private val KEY_CHAT_NOTIFICATIONS = "chat_notifications"

        @JvmField val CREATOR: Parcelable.Creator<FootballPlayer> =
                object : Parcelable.Creator<FootballPlayer> {
                    override fun createFromParcel(source: Parcel): FootballPlayer {
                        return FootballPlayer(source)
                    }

                    override fun newArray(size: Int): Array<FootballPlayer?> {
                        return arrayOfNulls(size)
                    }
                }
    }

    constructor(source: Parcel) : super(source) {
        user = source.readParcelable<UserObj>(UserObj::class.java.classLoader)
        teamName = source.readString()
        chatNotifications = source.readByte() != 0.toByte()
    }

    constructor(user: UserObj) {
        if (user is AppUser) {
            this.user = UserObj(user.uid)
        } else {
            this.user = user
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        super.writeToParcel(dest, flags)
        dest?.writeParcelable(user, flags)
        dest?.writeString(teamName)
        dest?.writeByte((if (chatNotifications) 1 else 0).toByte())
    }

    override fun pack(databaseMap: HashMap<String, Any>): DataInstanceResult {
        databaseMap.put(KEY_TEAM, teamName)
        databaseMap.put(KEY_CHAT_NOTIFICATIONS, chatNotifications)
        return DataInstanceResult.onSuccess()
    }

    override fun unpack(dataSnapshot: DataSnapshot): DataInstanceResult {
        user.uid = dataSnapshot.key
        teamName = dataSnapshot.child(KEY_TEAM).value as String
        chatNotifications = dataSnapshot.child(KEY_CHAT_NOTIFICATIONS).value as Boolean
        return DataInstanceResult.onSuccess()
    }

    override fun getPackableKey(): String = user.uid

    override fun hasUnpacked(): Boolean = !teamName.isEmpty() && user != UserObj.EMPTY

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

        if (other is FootballPlayer) {
            return other.user == user
        } else {
            return false
        }
    }

    override fun hashCode(): Int = user.hashCode() * teamName.hashCode()

    override fun toString(): String = "FootballPlayer:${user.uid} in team:$teamName"
}