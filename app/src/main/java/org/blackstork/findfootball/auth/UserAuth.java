package org.blackstork.findfootball.auth;

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
import com.vk.sdk.VKSdk;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.firebase.database.FBUserDatabase;

/**
 * Created by WiskiW on 11.03.2017.
 */

public class UserAuth {

    private static final String TAG = App.G_TAG + ":UserAuth";

    public final static int AUTH_REQUEST_CODE = 101;
    public final static int RESULT_SUCCESS = 10;
    public final static int RESULT_FAILED = 11;
    public final static int RESULT_CANCEL = 12;


    public static FirebaseUser getUser(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FBUserDatabase.newInstance(context, user.getUid()).updateLastUserOnline();
        }
        return user;
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

    public static void requestUser(Context context) {
        requestUser(context, null);
    }

    public static void updateLastUserOnline(Context context) {
        FirebaseUser user = getUser(context);
        if (user != null) {
            FBUserDatabase.newInstance(context, user.getUid()).updateLastUserOnline();
        }
    }

    public static void checkForAccountAvailability(final Context context) {
        // Проверка доступа к аккаунту в App
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

                        UserAuth.signOut();
                        UserAuth.requestUser(context);
                    }
                }
            });
        }
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut(); // for Facebook
        VKSdk.logout(); // VK
    }

    public static void addAuthStateListener(FirebaseAuth.AuthStateListener authStateListener) {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    public static void removeAuthStateListener(FirebaseAuth.AuthStateListener authStateListener) {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

}
