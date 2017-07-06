package online.findfootball.android.user.auth

import android.net.Uri
import com.google.firebase.auth.FirebaseUser

/**
 * Created by WiskiW on 06.07.2017.
 * AuthUserObj объект-обёртка Firebase пользователя, проходящего авторизацию
 */
class AuthUserObj() {

    constructor(fbUser: FirebaseUser) : this() {
        this.fbUser = fbUser
    }

    var fbUser: FirebaseUser? = null
    var password = ""
    var email: String = ""
        get() {
            if (!field.isEmpty()) {
                return field
            } else {
                val email = fbUser?.email
                if (email != null) {
                    field = email
                }
                return field
            }
        }

    var displayName: String = ""
        get() {
            if (!field.isEmpty()) {
                return field
            } else {
                val name = fbUser?.displayName
                if (name != null) {
                    field = name
                }
                return field
            }
        }

    var photoUrl: Uri = Uri.EMPTY
        get() {
            if (field != Uri.EMPTY) {
                return field
            } else {
                val url = fbUser?.photoUrl
                if (url != null) {
                    field = url
                }
                return field
            }
        }

}