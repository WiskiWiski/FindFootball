package online.findfootball.android.app

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import online.findfootball.android.user.AppUser

/**
 * Created by WiskiW on 22.06.2017.
 */
class FirebaseInstanceIDService : FirebaseInstanceIdService() {

    val TAG = App.G_TAG + "FBIDService"

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val appUser = AppUser.getInstance(applicationContext, false)
        if (appUser != null) {
            Log.i(TAG, "cm_token refresh for ${appUser.email}")
            appUser.cloudMessageToken = FirebaseInstanceId.getInstance().token
            appUser.save()
        }

    }
}