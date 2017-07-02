package online.findfootball.android.game.football.screen.info.tabs.about;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseLoader;
import online.findfootball.android.firebase.database.DatabasePackable;
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

    private ProgressBar userProgressBar;
    private View userContainerView;

    private TextView gameDescriptionView;
    private TextView gameDateView;
    private TextView userNameView;
    private TextView userDescriptionView;
    private TextView gameLocationView;
    private ImageView userImageView;

    private Switch notificationSwitch;

    private DatabaseLoader loader;
    private DatabaseLoader.OnLoadListener loadListener;

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
        notificationSwitch = (Switch) rootView.findViewById(R.id.notification_switch);

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (thisGameOwnerUser != null && thisGameObj != null) {
                  /*
                    if (thisGameObj.getTeams().getTeamA().getPlayerList().indexOf(thisGameOwnerUser) != -1){
                        FBDatabase.getDatabaseReference()
                    }
                    ArrayList<FootballPlayer> playerList = thisGameObj.getTeams().getTeamA().getPlayerList();
                    int ownerPlayerIndex = playerList.indexOf(new FootballPlayer(thisGameOwnerUser.getUid()));
                    if (ownerPlayerIndex != -1) {
                        FootballPlayer thisPlayer = playerList.get(ownerPlayerIndex);
                        thisPlayer.setChatNotificationsEnable(isChecked);
                        thisPlayer.save();
                    }
                    */
                }
            }
        });

        userProgressBar = (ProgressBar) rootView.findViewById(R.id.user_progressbar);
        userContainerView = rootView.findViewById(R.id.user_container);

        if (thisGameOwnerUser == null || !thisGameOwnerUser.hasUnpacked()) {
            showUserLoading(true);
        }
        loadListener = new DatabaseLoader.OnLoadListener() {
            @Override
            public void onComplete(DataInstanceResult result, DatabasePackable packable) {
                if (result.getCode() == DataInstanceResult.CODE_SUCCESS) {
                    thisGameOwnerUser = (UserObj) packable;
                    if (thisGameOwnerUser.hasUnpacked()) {
                        updateOwnerView();
                    }
                    showUserLoading(false);
                }
            }
        };
        return rootView;
    }

    private void showUserLoading(boolean load) {
        userProgressBar.setVisibility(load ? View.VISIBLE : View.GONE);
        userContainerView.setVisibility(load ? View.GONE : View.VISIBLE);
        notificationSwitch.setEnabled(!load);
    }

    public void setData(GameObj game) {
        this.thisGameObj = game;
        thisGameOwnerUser = thisGameObj.getOwnerUser();
        if (thisGameOwnerUser.hasUnpacked()) {
            updateOwnerView();
            showUserLoading(false);
        } else {
            if (loader == null) {
                loader = DatabaseLoader.newLoader();
            }
            loader.load(thisGameOwnerUser, false, loadListener);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (thisGameObj != null && thisGameObj.hasUnpacked()) {
            updateGameView();
        }

        if (thisGameOwnerUser != null) {
            if (thisGameOwnerUser.hasUnpacked()) {
                updateOwnerView();
            } else if (!loader.isLoading()) {
                showUserLoading(true);
                loader.load(thisGameOwnerUser, false, loadListener);
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        showUserLoading(false);
        if (loader != null) {
            loader.abortAllLoadings();
        }
    }

    private void updateGameView() {
        if (thisGameObj == null) {
            return;
        }
        if (gameDescriptionView != null) {
            gameDescriptionView.setText(thisGameObj.getDescription());
        }

        if (gameLocationView != null) {
            LocationObj location = thisGameObj.getLocation();
            if (location != null) {
                String addressCity = location.getCityName();
                String addressCountry = location.getCountryName();
                if (addressCity != null && addressCountry != null) {
                    gameLocationView.setText(addressCity + ", " + addressCountry);
                } else {
                    if (addressCity != null) {
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

    private void updateOwnerView() {
        if (userNameView != null) {
            userNameView.setText(thisGameOwnerUser.getDisplayName());
        }

        if (userDescriptionView != null) {
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
}
