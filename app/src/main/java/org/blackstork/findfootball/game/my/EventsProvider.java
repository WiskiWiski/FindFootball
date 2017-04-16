package org.blackstork.findfootball.game.my;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.firebase.database.FBCompleteListener;
import org.blackstork.findfootball.firebase.database.FBFootballDatabase;
import org.blackstork.findfootball.firebase.database.FBUserDatabase;
import org.blackstork.findfootball.game.GameObj;
import org.blackstork.findfootball.time.TimeProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by WiskiW on 12.04.2017.
 */

public class EventsProvider {

    public static final String TAG = App.G_TAG + ":FindGameDataProvider";

    private Context context;
    private String uid;
    private EventsProviderListener listener;

    private EVENTS_TYPE requestType;

    private enum EVENTS_TYPE {
        Upcoming, Archived
    }

    private List<GameObj> gameList;

    public EventsProvider(Context context, String uid, EventsProviderListener listener) {
        this.context = context;
        this.uid = uid;
        this.listener = listener;
    }

    private Iterator<DataSnapshot> gamesIterator;

    private void getGames() {
        gameList = new ArrayList<>();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gamesIterator = dataSnapshot.getChildren().iterator();
                if (gamesIterator.hasNext()) {
                    processData(gamesIterator.next());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(1, databaseError.toException().getLocalizedMessage());
            }
        };
        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
        mPostReference = mPostReference.child(FBUserDatabase.USERS_PATH).child(uid).child("events").child("football");
        mPostReference.addListenerForSingleValueEvent(postListener);
    }

    public void getUpcomingGames() {
        requestType = EVENTS_TYPE.Upcoming;
        getGames();
    }


    public void getArchivedGames() {
        requestType = EVENTS_TYPE.Archived;
        getGames();
    }


    private void processData(DataSnapshot postSnapshot) {
        final String eid = postSnapshot.getKey();
        final FBFootballDatabase footballDatabase = FBFootballDatabase.newInstance(context);
        footballDatabase.readGame(new FBCompleteListener() {
            @Override
            public void onSuccess(Object object) {
                if (object != null) {
                    GameObj game = (GameObj) object;
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
                } else {
                    FBUserDatabase userDatabase = FBUserDatabase.newInstance(context, uid);
                    userDatabase.removeFootballEvent(null, eid);
                    Log.d(TAG, "game has been removed: " + eid);
                }
                if (gamesIterator.hasNext()) {
                    processData(gamesIterator.next());
                } else {
                    listener.onSuccess(gameList);
                }
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        }, eid);
    }


    public interface EventsProviderListener {

        void onProgress(GameObj gameObj);

        void onSuccess(List<GameObj> gameList);

        void onFailed(int code, String msg);
    }


}
