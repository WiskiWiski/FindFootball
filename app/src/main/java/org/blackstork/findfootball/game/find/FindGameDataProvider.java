package org.blackstork.findfootball.game.find;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.firebase.database.FBFootballDatabase;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.time.TimeProvider;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by WiskiW on 14.04.2017.
 */

class FindGameDataProvider {

    private static final String TAG = App.G_TAG + ":FindGameProv";

    private static final int TOTAL_ITEM_EACH_LOAD = 5;

    private Context context;
    private EventsProviderListener callbackListener;
    private DatabaseReference databaseReference;

    private boolean inProgress = false;
    private int previewCacheSize = 0;
    private LinkedHashSet<GameObj> gamesCache;

    private int searchLevel = 1;

    private String uCity;
    private String uCountry;


    private long lastEventTime;

    public FindGameDataProvider(Context context, EventsProviderListener callbackListener) {
        this.context = context;
        this.callbackListener = callbackListener;
        gamesCache = new LinkedHashSet<>();
        lastEventTime = TimeProvider.getUtcTime();
    }

    public void setCity(String uCity) {
        this.uCity = uCity;
    }

    public void setCountry(String uCountry) {
        this.uCountry = uCountry;
    }

    private void levelUp() {
        searchLevel++;
        previewCacheSize = gamesCache.size();
        lastEventTime = TimeProvider.getUtcTime();
        Log.w(TAG, "levelUp: search level updated: " + searchLevel);
    }

    private void moreData() {
          /*
        SEARCH LEVELS:
            1: Time -> City
            TODO: Time -> Closes cites
            2: Time -> Country

        */
        inProgress = true;
        switch (searchLevel) {
            case 1:
                if (uCity != null) {
                    searchLevelOne_Two(FBFootballDatabase.KEY_LOCATION_CITY_NAME, uCity);
                } else {
                    Log.w(TAG, "moreData: Set city before call loadData!");
                }
                break;
            case 2:
                if (uCountry != null) {
                    searchLevelOne_Two(FBFootballDatabase.KEY_LOCATION_COUNTRY_NAME, uCountry);
                } else {
                    Log.w(TAG, "moreData: Set country before call loadData!");
                }
                break;
            default:
                callbackListener.onFailed(2, "No more data!");
                break;
        }
    }

    public void loadData() {
        if (inProgress) {
            Log.d(TAG, "loadData: inProgress = true!");
            return;
        }
        moreData();
    }

    private void searchLevelOne_Two(final String orderPath, final String requiredString) {
        DatabaseReference databaseReference = getDatabaseReference();
        databaseReference
                .orderByChild(FBFootballDatabase.KEY_EVENT_TIME)
                .startAt(lastEventTime)
                .limitToFirst(TOTAL_ITEM_EACH_LOAD)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Log.d(TAG, "onDataChange: dataSnapshot [" + dataSnapshot.getChildrenCount() + "] :" + dataSnapshot.getValue());
                        if (dataSnapshot.hasChildren()) {
                            GameObj game;
                            for (DataSnapshot gameSnapshot : dataSnapshot.getChildren()) {
                                String eventDataString = (String) gameSnapshot.child(orderPath).getValue();
                                if (eventDataString != null && eventDataString.equals(requiredString)) {
                                    game = new GameObj(gameSnapshot);

                                    if (gamesCache.add(game)) {
                                        // игры еще нет в кэше
                                        Log.d(TAG, "searchLevelOne_Two: added: " + game.getTitle());
                                        callbackListener.onProgress(game);
                                    }
                                }
                                long newTime = (long) gameSnapshot.child(FBFootballDatabase.KEY_EVENT_TIME).getValue();
                                lastEventTime = newTime != 0 ? newTime + 1 : lastEventTime;
                            }
                            if (itsEnough()) {
                                callbackListener.onSuccess(new ArrayList<>(gamesCache));
                            } else {
                                moreData();
                                return;
                            }
                        } else {
                            levelUp();
                            moreData();
                            return;
                        }
                        inProgress = false;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callbackListener.onFailed(1, databaseError.getMessage());
                        inProgress = false;
                    }
                });
    }

    private boolean itsEnough() {
        return gamesCache.size() >= TOTAL_ITEM_EACH_LOAD + previewCacheSize;
    }

    private DatabaseReference getDatabaseReference() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child(FBFootballDatabase.FOOTBALL_PATH);
        }
        return databaseReference;
    }

    public interface EventsProviderListener {

        void onProgress(GameObj gameObj);

        void onSuccess(List<GameObj> gameList);

        void onFailed(int code, String msg);

    }

}
