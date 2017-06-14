package online.findfootball.android.game.football.screen.info.tabs.players.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import online.findfootball.android.R;

/**
 * Created by WiskiW on 20.04.2017.
 */

public class PlayerViewHolder extends RecyclerView.ViewHolder {


    private ImageView userPhotoView;
    private TextView displayNameView;

    public PlayerViewHolder(View itemView) {
        super(itemView);
        displayNameView = (TextView) itemView.findViewById(R.id.user_display_name);
        userPhotoView = (ImageView) itemView.findViewById(R.id.user_photo);
    }


    public void setDisplayName(String name) {
        displayNameView.setText(name);
    }


    public void setPhotoUrl(Uri uri) {
        if (uri != null) {
            final Context context = userPhotoView.getContext();
            Glide
                    .with(context)
                    .load(uri)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(userPhotoView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            userPhotoView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }

}
