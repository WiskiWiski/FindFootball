package online.findfootball.android.game.info.tabs;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.location.LocationObj;
import online.findfootball.android.time.TimeProvider;
import online.findfootball.android.user.UserObj;

/**
 * A simple {@link Fragment} subclass.
 */
public class GIAboutTab extends Fragment {

    private static final String TAG = App.G_TAG + ":GIAboutTab";


    private GameObj thisGameObj;
    private UserObj thisGameOwnerUser;

    private TextView gameDescriptionView;
    private TextView gameDateView;
    private TextView userNameView;
    private TextView userDescriptionView;
    private TextView gameLocationView;
    private ImageView userImageView;

    public GIAboutTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gi_tab_about, container, false);
        gameDescriptionView = (TextView) rootView.findViewById(R.id.game_description);
        gameDateView = (TextView) rootView.findViewById(R.id.game_date);
        userNameView = (TextView) rootView.findViewById(R.id.user_name);
        userImageView = (ImageView) rootView.findViewById(R.id.user_photo);
        userDescriptionView = (TextView) rootView.findViewById(R.id.user_description);
        gameLocationView = (TextView) rootView.findViewById(R.id.game_location);


        return rootView;
    }

    public void setData(GameObj game, UserObj gameOwner) {
        this.thisGameObj = game;
        this.thisGameOwnerUser = gameOwner;
        updateView();
    }

    private void updateView() {
        if (thisGameOwnerUser != null && thisGameOwnerUser.hasLoaded()) {
            if (userNameView != null) {
                userNameView.setText(thisGameOwnerUser.getDisplayName());
            }

            if  (userDescriptionView != null){
                userDescriptionView.setText(thisGameOwnerUser.getEmail());
            }

            if (userImageView != null && thisGameOwnerUser.getPhotoUrl() != null) {
                Glide
                        .with(this)
                        .load(thisGameOwnerUser.getPhotoUrl())
                        .asBitmap()
                        .centerCrop()
                        .into(new BitmapImageViewTarget(userImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                userImageView.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        }

        if (gameDescriptionView != null) {
            gameDescriptionView.setText(thisGameObj.getDescription());
        }

        if (gameLocationView != null) {
            LocationObj location = thisGameObj.getLocation();
            Log.d(TAG, "updateView: location: " + location);
            if (location != null){
                String addressCity = location.getCityName();
                String addressCountry = location.getCountryName();
                if (addressCity != null && addressCountry != null){
                    gameLocationView.setText(addressCity + ", " + addressCountry);
                } else {
                    if (addressCity != null){
                        gameLocationView.setText(addressCity);
                    } else {
                        gameLocationView.setText(addressCountry);
                    }
                }
            }
        }

        if (gameDateView != null) {
            long localDate = TimeProvider.convertToLocal(thisGameObj.getEventTime());
            gameDateView.setText(TimeProvider.getStringDate(TimeProvider.FORMAT_LONG, localDate));
        }

    }
}
