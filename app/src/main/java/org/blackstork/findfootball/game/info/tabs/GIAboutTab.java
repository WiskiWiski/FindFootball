package org.blackstork.findfootball.game.info.tabs;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.time.TimeProvider;
import org.blackstork.findfootball.user.UserObj;

/**
 * A simple {@link Fragment} subclass.
 */
public class GIAboutTab extends Fragment {

    private static final String TAG = App.G_TAG + ":GIAboutTab";


    private GameObj thisGameObj;
    private UserObj thisGameOwnerUser;

    private TextView gameDescription;
    private TextView gameDate;
    private TextView userName;
    private ImageView userImage;

    public GIAboutTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gi_tab_about, container, false);
        gameDescription = (TextView) rootView.findViewById(R.id.game_description);
        gameDate = (TextView) rootView.findViewById(R.id.game_date);
        userName = (TextView) rootView.findViewById(R.id.user_name);
        userImage = (ImageView) rootView.findViewById(R.id.user_photo);


        return rootView;
    }

    public void setData(GameObj game, UserObj gameOwner) {
        this.thisGameObj = game;
        this.thisGameOwnerUser = gameOwner;
        updateView();
    }

    private void updateView() {
        if (thisGameOwnerUser != null && thisGameOwnerUser.hasLoaded()) {
            if (userName != null) {
                userName.setText(thisGameOwnerUser.getDisplayName());
            }
            if (userImage != null && thisGameOwnerUser.getPhotoUrl() != null) {
                Glide
                        .with(this)
                        .load(thisGameOwnerUser.getPhotoUrl())
                        .asBitmap()
                        .centerCrop()
                        .into(new BitmapImageViewTarget(userImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                userImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        }

        if (gameDescription != null) {
            gameDescription.setText(thisGameObj.getDescription());
        }

        if (gameDate != null) {
            long localDate = TimeProvider.convertToLocal(thisGameObj.getEventTime());
            gameDate.setText(TimeProvider.getStringDate(TimeProvider.FORMAT_LONG, localDate));
        }
    }
}
