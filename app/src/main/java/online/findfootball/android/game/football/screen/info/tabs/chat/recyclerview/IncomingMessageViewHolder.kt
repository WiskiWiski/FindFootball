package online.findfootball.android.game.football.screen.info.tabs.chat.recyclerview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import online.findfootball.android.R
import online.findfootball.android.firebase.database.DataInstanceResult
import online.findfootball.android.game.chat.MessageObj
import online.findfootball.android.user.ProfileActivity
import online.findfootball.android.user.UserObj

/**
 * Created by WiskiW on 24.06.2017.
 */
class IncomingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val msgTextView: TextView = itemView.findViewById(R.id.msg_edit_text) as TextView
    val userPhotoView: ImageView = itemView.findViewById(R.id.user_photo) as ImageView

    var message: MessageObj = MessageObj()
        set(value) {
            setMsgText(value.text)

            if (value.userFrom.hasUnpacked()) {
                setUserPhoto(value.userFrom)
            } else {
                value.userFrom.load { result, packable ->
                    if (result.code != DataInstanceResult.CODE_LOADING_FAILED) {
                        value.userFrom = packable as UserObj
                        setUserPhoto(value.userFrom)
                    }
                }
            }
        }

    fun setMsgText(text: String) {
        msgTextView.text = text
    }

    fun setUserPhoto(user: UserObj) {
        val context = userPhotoView.context
        val uri: Uri? = user.photoUrl
        if (uri == null) {
            // make sure Glide doesn't load anything into this view until told otherwise
            Glide.clear(userPhotoView)
            // remove the placeholder (optional); read comments below
            userPhotoView.setImageDrawable(null)
        } else {
            Glide
                    .with(context)
                    .load(uri)
                    .asBitmap()
                    .centerCrop()
                    .error(android.R.drawable.ic_delete)
                    .into(object : BitmapImageViewTarget(userPhotoView) {
                        override fun setResource(resource: Bitmap) {
                            val circularBitmapDrawable = RoundedBitmapDrawableFactory.
                                    create(context.resources, resource)
                            circularBitmapDrawable.isCircular = true
                            userPhotoView.setImageDrawable(circularBitmapDrawable)
                        }
                    })

            userPhotoView.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra(ProfileActivity.INTENT_USER_KEY, user as Parcelable)
                ContextCompat.startActivity(context, intent, null)
            }

        }
    }

}