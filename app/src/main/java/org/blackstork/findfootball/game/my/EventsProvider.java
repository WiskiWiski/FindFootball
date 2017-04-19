package org.blackstork.findfootball.game.my;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.firebase.database.DatabaseInstance;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.time.TimeProvider;
import org.blackstork.findfootball.user.AppUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by WiskiW on 12.04.2017.
 */

public class EventsProvider {

    public static final String TAG = App.G_TAG + ":FindGameProvider";

    private AppUser appUser;
    private EventsProviderListener listener;

    private EVENTS_TYPE requestType;
    private LOADING_STATUS loadingStatus;

    private enum EVENTS_TYPE {
        Upcoming, Archived
    }

    private enum LOADING_STATUS {
        IN_PROGRESS, NON, ABORTED
    }

    private List<GameObj> gameList;

    public EventsProvider(AppUser appUser, EventsProviderListener listener) {
        this.appUser = appUser;
        this.listener = listener;
    }


    private void processData(final Iterator<GameObj> iterator) {
        if (loadingStatus != LOADING_STATUS.IN_PROGRESS){
            return;
        }
        if (iterator.hasNext()) {
            final GameObj game = iterator.next();
            if (game != null) {
                game.load(new DatabaseInstance.OnLoadListener() {
                    @Override
                    public void onSuccess(DatabaseInstance instance) {
                        if (loadingStatus != LOADING_STATUS.IN_PROGRESS){
                            return;
                        }
                        if (instance != null) {
                            GameObj game = (GameObj) instance;
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
                        }
                        processData(iterator);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        if (loadingStatus != LOADING_STATUS.IN_PROGRESS){
                            return;
                        }
                        if (code == DatabaseInstance.OnLoadListener.FAILED_HAS_REMOVED) {
                            appUser.removeFootballGame(game);
                        }
                        processData(iterator);
                    }
                });
            } else {
                processData(iterator);
            }
            iterator.remove();
        } else {
            appUser.setGameSet(gameList);
            AppUser.updateInstance(appUser);
            listener.onSuccess(gameList);
        }
    }


    private void getGames() {
        loadingStatus = LOADING_STATUS.IN_PROGRESS;
        gameList = new ArrayList<>();
        appUser.load(new DatabaseInstance.OnLoadListener() {
            @Override
            public void onSuccess(DatabaseInstance instance) {
                if (instance == null) {
                    listener.onFailed(11, "user instance is null!");
                    return;
                }
                AppUser appUser = (AppUser) instance;
                AppUser.updateInstance(appUser);
                processData(appUser.getGameSet().iterator());
            }

            @Override
            public void onFailed(int code, String msg) {
                listener.onFailed(code, msg);
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
        if (appUser != null) {
            appUser.abortLoading();
            listener.onSuccess(gameList);
        }
    }


    public interface EventsProviderListener {

        void onProgress(GameObj gameObj);

        void onSuccess(List<GameObj> gameList);

        void onFailed(int code, String msg);
    }


}
