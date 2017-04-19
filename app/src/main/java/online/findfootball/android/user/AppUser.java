package online.findfootball.android.user;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import online.findfootball.android.firebase.database.FBDatabase;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.time.TimeProvider;
import online.findfootball.android.user.auth.UserAuth;

/**
 * Created by WiskiW on 17.04.2017.
 */

public class AppUser extends UserObj {


    private static AppUser currentUser;


    public AppUser(String uid) {
        super(uid);
    }

    public AppUser(FirebaseUser firebaseUser) {
        super(firebaseUser);
    }

    public static void updateInstance(AppUser appUser) {
        if (appUser != null) {
            currentUser = appUser;
        }
    }

    public static AppUser getInstance(Context context) {
        return getInstance(context, true, null);
    }

    public static AppUser getInstance(Context context, boolean requestAuth) {
        return getInstance(context, requestAuth, null);
    }

    public static AppUser getInstance(Context context, boolean requestAuth, String singInMsg) {
        if (currentUser != null) {
            currentUser.updateLastActivity();
            return currentUser;
        }
        AppUser appUser = UserAuth.getUser();
        if (requestAuth && appUser == null) {
            UserAuth.requestUser(context, singInMsg);
            return null;
        }
        if (appUser != null)
            appUser.updateLastActivity();
        return appUser;

    }

    public void updateLastActivity() {
        setLastActivityTime(TimeProvider.getUtcTime());
        FBDatabase.getDatabaseReference(this)
                .child(PATH_LAST_ACTIVITY_TIME).setValue(getLastActivityTime());
    }

    public static void signOut() {
        UserAuth.signOut();
        currentUser = null;
    }

    public void joinToFootballGame(GameObj game, boolean isOwner) {
        // Auto saving to Firebase!
        // 1. Сохраняем в списки ивентов юзура
        pushGameToSet(game);
        FBDatabase.getDatabaseReference(this)
                .child(PATH_GAMES_FOOTBALL).child(game.getEid()).setValue(String.valueOf(isOwner));

        // 2. Добовляем юзера в список игроков ивента
        game.getPlayerList().addPlayer(this);
        FBDatabase.getDatabaseReference(game)
                .child(GameObj.PATH_PLAYERS).child(getUid()).setValue(String.valueOf(isOwner));
    }

    public void joinToFootballGame(GameObj game) {
        joinToFootballGame(game, false);
    }

    public void removeFootballGame(GameObj game) {
        // Auto saving to Firebase!
        getGameSet().remove(game);
        FBDatabase.getDatabaseReference(this)
                .child(PATH_GAMES_FOOTBALL).child(game.getEid()).removeValue();
        if (game.getOwnerUid().equals(getUid())) {
            // пользователь владелец ивента
            game.delete();
        } else {
            game.getPlayerList().removePlayer(this);
            FBDatabase.getDatabaseReference(game)
                    .child(GameObj.PATH_PLAYERS).child(game.getEid()).removeValue();
        }

    }

    @Override
    public int save(Context context) {
        DatabaseReference thisUserReference = FBDatabase.getDatabaseReference(this);
        thisUserReference.removeValue();
        thisUserReference.child(PATH_DISPLAY_NAME).setValue(getDisplayName());
        thisUserReference.child(PATH_PHOTO_URL).setValue(getPhotoUrl().toString());
        thisUserReference.child(PATH_EMAIL).setValue(getEmail());
        thisUserReference.child(PATH_REGISTER_TIME).setValue(getRegisterTime());
        thisUserReference.child(PATH_LAST_ACTIVITY_TIME).setValue(getLastActivityTime());


        for (GameObj game : getGameSet()) {
            thisUserReference.child(PATH_GAMES_FOOTBALL).child(game.getEid()).setValue(game.getEid());
            game.save(context);
        }

        return RESULT_SUCCESS;
    }
}
