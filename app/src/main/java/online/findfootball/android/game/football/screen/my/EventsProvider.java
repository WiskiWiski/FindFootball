package online.findfootball.android.game.football.screen.my;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import online.findfootball.android.app.App;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseLoader;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.time.TimeProvider;
import online.findfootball.android.user.AppUser;

/**
 * Created by WiskiW on 12.04.2017.
 */

public class EventsProvider {

    public static final String TAG = App.G_TAG + ":FindGameProvider";

    private AppUser appUser;
    private EventsProviderListener listener;

    private EVENTS_TYPE requestType;
    private LOADING_STATUS loadingStatus;

    private DatabaseLoader userLoader;
    private DatabaseLoader gameLoader;

    private enum EVENTS_TYPE {
        Upcoming, Archived
    }

    private enum LOADING_STATUS {
        IN_PROGRESS, NON, ABORTED
    }

    private ArrayList<GameObj> gameList;

    public EventsProvider(AppUser appUser, EventsProviderListener listener) {
        this.appUser = appUser;
        this.listener = listener;
    }


    private void processData(final Iterator<GameObj> iterator) {
        if (loadingStatus != LOADING_STATUS.IN_PROGRESS) {
            return;
        }
        if (iterator.hasNext()) {
            final GameObj game = iterator.next();
            if (game != null) {
                gameLoader = DatabaseLoader.newLoader();
                gameLoader.load(game, false, new DatabaseLoader.OnLoadListener() {
                    @Override
                    public void onComplete(DataInstanceResult result, DatabasePackable packable) {
                        if (loadingStatus != LOADING_STATUS.IN_PROGRESS) {
                            return;
                        }
                        GameObj game = (GameObj) packable;
                        switch (result.getCode()) {
                            case DataInstanceResult.CODE_SUCCESS:
                                if (game.getEventTime() > TimeProvider.getUtcTime() - 1000) { // - 1000 - просто так
                                    if (requestType == EVENTS_TYPE.Upcoming) {
                                        gameList.add(game);
                                        listener.onProgress(game);
                                    }
                                } else {
                                    if (requestType == EVENTS_TYPE.Archived) {
                                        gameList.add(game);
                                        listener.onProgress(game);
                                    }
                                }
                                break;
                            case DataInstanceResult.CODE_HAS_REMOVED:
                                appUser.leaveGame(game);
                                break;
                            default:
                                Log.d(TAG, "onComplete [" + result.getCode() + "]: " + result.getMessage());
                                break;
                        }
                        processData(iterator);
                    }

                });
            } else {
                processData(iterator);
            }
            iterator.remove();
        } else {
            AppUser.updateInstance(appUser);
            listener.onSuccess(gameList);
        }
    }


    private void getGames() {
        loadingStatus = LOADING_STATUS.IN_PROGRESS;
        gameList = new ArrayList<>();
        userLoader = DatabaseLoader.newLoader();
        userLoader.load(appUser, false, new DatabaseLoader.OnLoadListener() {
            @Override
            public void onComplete(DataInstanceResult result, DatabasePackable packable) {
                if (result.getCode() == DataInstanceResult.CODE_SUCCESS) {
                    appUser = (AppUser) packable;
                    AppUser.updateInstance(appUser);
                    processData(appUser.getGameList().iterator());
                } else {
                    listener.onFailed(result.getCode(), "not success: " + result.getMessage());
                }
            }
        });

    }

    public void getUpcomingGames() {
        requestType = EVENTS_TYPE.Upcoming;
        getGames();
    }


    public void getArchivedGames() {
        requestType = EVENTS_TYPE.Archived;
        getGames();
    }

    public void abortLoading() {
        loadingStatus = LOADING_STATUS.ABORTED;
        if (userLoader != null) {
            userLoader.abortAllLoadings();
            //listener.onComplete(gameList);
        }
        if (gameLoader != null) {
            gameLoader.abortAllLoadings();
        }
    }


    public interface EventsProviderListener {

        void onProgress(GameObj gameObj);

        void onSuccess(List<GameObj> gameList);

        void onFailed(int code, String msg);
    }

}
