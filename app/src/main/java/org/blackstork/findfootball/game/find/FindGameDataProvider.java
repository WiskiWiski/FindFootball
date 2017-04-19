package org.blackstork.findfootball.game.find;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.firebase.database.FBDatabase;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.location.LocationObj;
import org.blackstork.findfootball.time.TimeProvider;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by WiskiW on 14.04.2017.
 */

class FindGameDataProvider {

    private static final String TAG = App.G_TAG + ":FindGameProv";

    public static final int CODE_NO_DATA = 2;
    public static final int CODE_NO_MORE_DATA = 3;


    private static final int TOTAL_ITEM_EACH_LOAD = 7;

    private LocationObj location;
    private EventsProviderListener callbackListener;
    private DatabaseReference databaseReference;

    private boolean inProgress = false;
    private int previewCacheSize = 0;
    private LinkedHashSet<GameObj> gamesCache;
    private ValueEventListener baseSearchEventListener;

    private int searchLevel = 1;

    private long lastEventTime;

    public FindGameDataProvider(EventsProviderListener callbackListener) {
        this.callbackListener = callbackListener;
        gamesCache = new LinkedHashSet<>();
        lastEventTime = TimeProvider.getUtcTime();
    }

    private void levelUp() {
        searchLevel++;
        previewCacheSize = gamesCache.size();
        lastEventTime = TimeProvider.getUtcTime();
        //Log.w(TAG, "levelUp: baseSearch level updated: " + searchLevel);
    }

    private void moreData() {
          /*
        SEARCH LEVELS:
            1: Time -> City
            TODO: Time -> Closes cites
            2: Time -> Country
            3: Time -> all

        */

        switch (searchLevel) {
            case 1:
                if (location == null || location.getCityName() == null) {
                    Log.w(TAG, "moreData: skip search level I !");
                    levelUp();
                    loadData();
                } else {
                    searchLevelI(location.getCityName());
                }
                break;
            case 2:
                if (location == null || location.getCountryName() == null) {
                    Log.w(TAG, "moreData: skip search level II !");
                    levelUp();
                    loadData();
                } else {
                    searchLevelII(location.getCountryName());
                }
                break;
            case 3:
                searchLevelIII();
                break;
            default:
                inProgress = false;
                if (gamesCache.size() == 0) {
                    callbackListener.onFailed(CODE_NO_DATA, "No data found");
                } else {
                    callbackListener.onFailed(CODE_NO_MORE_DATA, "No more data!");
                }
                break;
        }
    }

    public void setLocation(LocationObj location) {
        this.location = location;
    }

    public void loadData() {
        if (inProgress) {
            Log.i(TAG, "loadData: inProgress = true!");
            return;
        }
        moreData();
    }

    public void reset() {
        gamesCache.clear();
        searchLevel = 1;
        previewCacheSize = 0;
    }

    private void baseSearch(final GameElementSearchListener gameElementSearchListener) {
        inProgress = true;
        baseSearchEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d(TAG, "onDataChange: dataSnapshot [" + dataSnapshot.getChildrenCount() + "] :" + dataSnapshot.getValue());
                if (dataSnapshot.hasChildren()) {
                    GameObj game;
                    for (DataSnapshot gameSnapshot : dataSnapshot.getChildren()) {
                        if (gameSnapshot == null) {
                            continue;
                        }
                        boolean toAdd = gameElementSearchListener.onSnapshotReceived(gameSnapshot);
                        if (toAdd) {
                            game = new GameObj(gameSnapshot);
                            if (gamesCache.add(game)) {
                                // игры еще нет в кэше
                                //Log.d(TAG, "searchLevel_One_Two_Three: added: " + game.getTitle());
                                callbackListener.onProgress(game);
                            }
                        }
                        long newTime = (long) gameSnapshot.child(GameObj.PATH_EVENT_TIME).getValue();
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
                callbackListener.onFailed(databaseError.getCode(), databaseError.getMessage());
                inProgress = false;
            }
        };
        DatabaseReference databaseReference = getDatabaseReference();
        databaseReference
                .orderByChild(GameObj.PATH_EVENT_TIME)
                .startAt(lastEventTime)
                .limitToFirst(TOTAL_ITEM_EACH_LOAD)
                .addListenerForSingleValueEvent(baseSearchEventListener);
    }

    private void searchLevelI(final String requiredCity) {
        // ивенты города
        baseSearch(new GameElementSearchListener() {
            @Override
            public boolean onSnapshotReceived(DataSnapshot gameSnapshot) {
                String eventDataString = (String)
                        gameSnapshot.child(GameObj.PATH_LOCATION_CITY_NAME).getValue();
                return eventDataString != null && eventDataString.equals(requiredCity);
            }
        });
    }

    private void searchLevelII(final String requiredCountry) {
        // ивенты страны
        baseSearch(new GameElementSearchListener() {
            @Override
            public boolean onSnapshotReceived(DataSnapshot gameSnapshot) {
                String eventDataString = (String)
                        gameSnapshot.child(GameObj.PATH_LOCATION_COUNTRY_NAME).getValue();
                return eventDataString != null && eventDataString.equals(requiredCountry);
            }
        });
    }

    private void searchLevelIII() {
        // любые ивенты
        baseSearch(new GameElementSearchListener() {
            @Override
            public boolean onSnapshotReceived(DataSnapshot gameSnapshot) {
                return true;
            }
        });
    }

    private boolean itsEnough() {
        return gamesCache.size() >= TOTAL_ITEM_EACH_LOAD + previewCacheSize;
    }

    private DatabaseReference getDatabaseReference() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child(FBDatabase.PATH_FOOTBALL_GAMES);
        }
        return databaseReference;
    }


    public void abortLoading() {
        databaseReference = null;
        //callbackListener.onSuccess(new ArrayList<>(gamesCache));
        reset();
        inProgress = false;
    }


    public interface EventsProviderListener {

        void onProgress(GameObj gameObj);

        void onSuccess(List<GameObj> gameList);

        void onFailed(int code, String msg);

    }

    private interface GameElementSearchListener {

        boolean onSnapshotReceived(DataSnapshot gameSnapshot);
    }

}
