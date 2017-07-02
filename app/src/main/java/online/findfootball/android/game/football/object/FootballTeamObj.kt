package online.findfootball.android.game.football.`object`

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import online.findfootball.android.firebase.database.children.PackableArrayList
import online.findfootball.android.user.UserObj

/**
 * Created by WiskiW on 30.06.2017.
 */
class FootballTeamObj : PackableArrayList<FootballPlayer> {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<FootballTeamObj> =
                object : Parcelable.Creator<FootballTeamObj> {
                    override fun createFromParcel(source: Parcel): FootballTeamObj = FootballTeamObj(source)
                    override fun newArray(size: Int): Array<FootballTeamObj?> = arrayOfNulls(size)
                }
    }

    var teamName = "nun"
    var capacity: Int = 0 // максимальная вместимость команды

    constructor(source: Parcel) : super(source) {
        teamName = source.readString()
        capacity = source.readInt()
    }

    constructor(teamName: String) {
        this.teamName = teamName
    }

    fun getTeamOccupancy(): Int = this.size

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeString(teamName)
        dest.writeInt(capacity)
    }

    override fun toString(): String = "name:$teamName " + super.toString()

    override fun newItem(itemSnapshot: DataSnapshot): FootballPlayer {
        val player = FootballPlayer(UserObj(itemSnapshot.key))
        player.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey())
        return player
    }

    override fun unpackItem(item: FootballPlayer, dataSnapshot: DataSnapshot): Boolean {
        super.unpackItem(item, dataSnapshot)
        return item.teamName == this.teamName
    }

    private fun initNewPlayer(player: FootballPlayer?) {
        player?.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey())
        player?.teamName = this.teamName
    }

    override fun add(element: FootballPlayer?): Boolean {
        var result = true
        if (element == null || this.size >= capacity) {
            result = false
        } else {
            initNewPlayer(element)
            val index = this.indexOf(element)
            if (index == -1) {
                super.add(element)
                result = true
            } else if (element.hasUnpacked()) {
                this[index] = element
                result = true
            }
        }
        return result
    }

    fun enrollPlayer(player: FootballPlayer?): Boolean {
        val result = this.add(player)
        if (result) {
            player?.save()
        }
        return result
    }

    fun unrollPlayer(player: FootballPlayer?): Boolean {
        val result = this.remove(player)
        if (result) {
            initNewPlayer(player)
            player?.delete()
        }
        return result
    }

    override fun add(index: Int, element: FootballPlayer?) {
        if (super.size < capacity) {
            initNewPlayer(element)
            super.add(index, element)
        }
    }

    override fun addAll(elements: Collection<FootballPlayer>): Boolean {
        if (capacity - this.size >= elements.size) {
            return super.addAll(elements)
        } else {
            return false
        }
    }

    override fun addAll(index: Int, elements: Collection<FootballPlayer>): Boolean {
        if (capacity - this.size >= elements.size) {
            return super.addAll(index, elements)
        } else {
            return false
        }
    }

}