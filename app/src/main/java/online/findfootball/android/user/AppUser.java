package online.findfootball.android.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.vk.sdk.VKSdk;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.FBDatabase;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.time.TimeProvider;
import online.findfootball.android.user.auth.AuthUiActivity;

/**
 * Created by WiskiW on 17.04.2017.
 */

public class AppUser extends UserObj {

    private static final String TAG = App.G_TAG + ":AppUser";

    private static AppUser currentUser;

    private static UserStateListener userStateListener;

    public final static int AUTH_REQUEST_CODE = 101;
    public final static int RESULT_SUCCESS = 10;
    public final static int RESULT_FAILED = 11;
    public final static int RESULT_CANCEL = 12;


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
        AppUser appUser = getUser();
        if (requestAuth && appUser == null) {
            requestUser(context, singInMsg);
            return null;
        }
        if (appUser != null)
            appUser.updateLastActivity();
        if (userStateListener != null) {
            userStateListener.onLogin(appUser);
        }
        return appUser;

    }

    public void updateLastActivity() {
        setLastActivityTime(TimeProvider.getUtcTime());
        FBDatabase.getDatabaseReference(this)
                .child(PATH_LAST_ACTIVITY_TIME).setValue(getLastActivityTime());
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut(); // for Facebook
        VKSdk.logout(); // VK
        if (userStateListener != null) {
            userStateListener.onSignOut();
        }
        currentUser = null;
    }

    public static void checkForAccountAvailability(final Context context) {
        // Слушатель доступа к аккаунту в App
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    try {
                        Exception ex = task.getException();
                        if (ex != null) {
                            throw ex;
                        }
                    } catch (Exception e) {
                        String msg = context.getString(R.string.auth_required_re_authorization);
                        Toast.makeText(context, msg + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.w(TAG, msg + e.getMessage());

                        signOut();
                        requestUser(context, null);
                    }
                }
            });
        }
    }

    public static AppUser getUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            return new AppUser(firebaseUser);
        } else {
            return null;
        }
    }

    public static void requestUser(Context context) {
        requestUser(context, null);
    }

    public static void requestUser(Context context, String singInMsg) {
        Intent intent = new Intent(context, AuthUiActivity.class);
        if (singInMsg != null) {
            intent.putExtra(AuthUiActivity.SIGN_IN_MSG_INTENT_KEY, singInMsg);
        }
        if (!(context instanceof Activity)) {
            Log.i(TAG, "RequestUser: Context must be instance of Activity to use onActivityResult");
            context.startActivity(intent);
        } else {
            ((Activity) context).startActivityForResult(intent, AUTH_REQUEST_CODE);
        }
    }

    public void joinGame(GameObj game) {
        // Добавляем игру в список ивентов пользователя
        addGame(game);

        // Сохраняем в бд
        String eid = game.getEid();
        FBDatabase.getDatabaseReference(getGameList()).child(eid).setValue(eid);
    }

    public void leaveGame(GameObj game) {
        // Убираем игру из ивентов пользователя
        removeGame(game);

        // Удаляем из бд
        FBDatabase.getDatabaseReference(getGameList()).child(game.getEid()).removeValue();

        if (game.getOwnerUid().equals(getUid())) {
            // пользователь владелец ивента
            // TODO : уведомление о удалении
            //game.delete();
        }
    }

    @Override
    public DataInstanceResult pack(DatabaseReference databaseReference) {
        databaseReference.child(PATH_DISPLAY_NAME).setValue(getDisplayName());
        databaseReference.child(PATH_PHOTO_URL).setValue(getPhotoUrl().toString());
        databaseReference.child(PATH_EMAIL).setValue(getEmail());
        databaseReference.child(PATH_REGISTER_TIME).setValue(getRegisterTime());
        databaseReference.child(PATH_LAST_ACTIVITY_TIME).setValue(getLastActivityTime());

        if (getGameList() != null) {
            for (GameObj game : getGameList()) {
                databaseReference.child(PATH_GAMES_FOOTBALL).child(game.getEid()).setValue(game.getEid());
            }
        }
        return DataInstanceResult.onSuccess();
    }

    public static UserStateListener getUserStateListener() {
        return userStateListener;
    }

    public static void setUserStateListener(UserStateListener userStateListener) {
        AppUser.userStateListener = userStateListener;
    }

    public interface UserStateListener {

        void onLogin(AppUser appUser);

        void onSignOut();

    }

}
