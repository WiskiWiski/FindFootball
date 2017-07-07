package online.findfootball.android.user.auth

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import online.findfootball.android.user.UserObj

/**
 * Created by WiskiW on 06.07.2017.
 * AuthUserObj объект-обёртка Firebase пользователя, проходящего авторизацию
 */
class AuthUserObj : UserObj {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<AuthUserObj> =
                object : Parcelable.Creator<AuthUserObj> {
                    override fun createFromParcel(source: Parcel): AuthUserObj {
                        return AuthUserObj(source)
                    }

                    override fun newArray(size: Int): Array<AuthUserObj?> {
                        return arrayOfNulls(size)
                    }
                }
    }

    var password = ""
    var providers: List<String> = ArrayList()

    constructor() : super()

    constructor(fbUser: FirebaseUser) : super(fbUser)

    constructor(source: Parcel) : super(source) {
        password = source.readString()
        source.readStringList(providers)
    }

    override fun initByFirebaseUser(fUser: FirebaseUser?) {
        super.initByFirebaseUser(fUser)
        this.providers = fUser?.providers as List<String>
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        super.writeToParcel(dest, flags)
        dest?.writeString(password)
        dest?.writeStringList(providers)
    }

}