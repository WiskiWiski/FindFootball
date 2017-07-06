package online.findfootball.android.user.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.app.BaseActivity;
import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseLoader;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.firebase.database.FBDatabase;
import online.findfootball.android.time.TimeProvider;
import online.findfootball.android.user.AppUser;
import online.findfootball.android.user.UserObj;
import online.findfootball.android.user.auth.providers.MyEmailAuthAuthProvider;
import online.findfootball.android.user.auth.providers.MyFacebookAuthProvider;
import online.findfootball.android.user.auth.providers.MyGoogleAuthProvider;
import online.findfootball.android.user.auth.providers.MyVkontakteAuthAuthProvider;

public class AuthUiActivity extends BaseActivity {

    private static final String TAG = App.G_TAG + ":AuthAct";

    public static final String SIGN_IN_MSG_INTENT_KEY = "sign_in_msg";

    private EditText inputEmail, inputPassword;
    private Button btnSignIn;
    private ImageButton btnGoogleSignIn;
    private ImageButton btnVKSignIn;
    private ImageButton btnFBSignIn;
    private Button btnSignUp;


    private boolean inProgress;
    private MyGoogleAuthProvider googleAuthProvider;
    private MyEmailAuthAuthProvider emailAuthProvider;
    private MyFacebookAuthProvider fbAuthProvider;
    private MyVkontakteAuthAuthProvider vkAuthProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_ui);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String signInMsg = getIntent().getStringExtra(SIGN_IN_MSG_INTENT_KEY);
        if (signInMsg != null) {
            ((TextView) findViewById(R.id.message)).setText(signInMsg);
        }

        initProviders();


        inputEmail = (EditText) findViewById(R.id.email_et);
        inputPassword = (EditText) findViewById(R.id.password_et);
        btnSignUp = (Button) findViewById(R.id.sign_up_btn);

        btnSignIn = (Button) findViewById(R.id.sign_in_btn);
        btnGoogleSignIn = (ImageButton) findViewById(R.id.google_sign_in_btn);
        btnFBSignIn = (ImageButton) findViewById(R.id.facebook_sign_in_btn);
        btnVKSignIn = (ImageButton) findViewById(R.id.vk_sign_in_btn);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (validation(email, password)) {
                    disableButtons();
                    emailAuthProvider.signIn(email, password);
                }
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }
        });

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableButtons();
                googleAuthProvider.signIn();
            }
        });

        btnFBSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons();
                fbAuthProvider.signIn();
            }
        });

        btnVKSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableButtons();
                vkAuthProvider.signIn();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (validation(email, password)) {
                    disableButtons();
                    emailAuthProvider.signUp(email, password);
                }
            }
        });
    }

    private void initProviders() {
        ProviderCallback providerCallback = new ProviderCallback() {
            @Override
            public void onResult(final AuthUserObj user) {
                Log.d(TAG, "Authentication success: " + user.getEmail());
                Toast.makeText(getApplicationContext(), user.getEmail(), Toast.LENGTH_LONG).show();

                // пытаемся подгрузить данные пользователя из firebase бд
                new UserObj(user.getFbUser().getUid()).load(new DatabaseLoader.OnLoadListener() {
                    @Override
                    public void onComplete(DataInstanceResult result, DatabasePackable packable) {
                        if (packable.hasUnpacked()) {
                            // Если пользователь уже есть в firebase бд
                            signInUser(user); // обновляем данные пользователя в БД
                        } else {
                            // пользователь только зарегистрировался
                            signUpUser(user); // сохраняем данные пользователя в БД
                        }
                        setResult(AppUser.RESULT_SUCCESS);
                        finish();

                        // учедомления слушателя о входе пользователя
                        AppUser.UserStateListener userStateListener = AppUser.getUserStateListener();
                        if (userStateListener != null) {
                            AppUser appUser = AppUser.getUser();
                            if (appUser != null) {
                                userStateListener.onLogin(AppUser.getUser());
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailed(FailedResult result) {
                setResult(AppUser.RESULT_FAILED);
                Log.d(TAG, "Authentication failed. " + result.toString());
                Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                enableButtons();
            }
        };
        emailAuthProvider = new MyEmailAuthAuthProvider(this, providerCallback);
        googleAuthProvider = new MyGoogleAuthProvider(this, providerCallback);
        fbAuthProvider = new MyFacebookAuthProvider(this, providerCallback);
        vkAuthProvider = new MyVkontakteAuthAuthProvider(this, providerCallback);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleAuthProvider.onActivityResult(requestCode, resultCode, data);
        fbAuthProvider.onActivityResult(requestCode, resultCode, data);
        vkAuthProvider.onActivityResult(requestCode, resultCode, data);
        emailAuthProvider.onActivityResult(requestCode, resultCode, data);
    }


    public static void signUpUser(AuthUserObj user) {
        // Записывает данные в бд после регистрации пользователя
        signInUser(user);
        final DatabaseReference thisUserReference = FirebaseDatabase.getInstance().getReference()
                .child(FBDatabase.PATH_USERS).child(user.getFbUser().getUid());
        thisUserReference.child(UserObj.PATH_REGISTER_TIME).setValue(TimeProvider.getUtcTime());

        List<String> providers = user.getFbUser().getProviders();
        if (providers != null && providers.size() > 0) {
            thisUserReference.child(UserObj.PATH_AUTH_PROVIDER).setValue(providers.get(0));
        } else {
            thisUserReference.child(UserObj.PATH_AUTH_PROVIDER).setValue("unknown");
        }

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            thisUserReference.child(UserObj.PATH_CLOUDE_MESSAGE_TOKEN).setValue(token);
        }
    }

    public static void signInUser(AuthUserObj user) {
        // Обновляет поля пользователя в бд по данным FirebaseUser после авторизации
        final DatabaseReference thisUserReference = FirebaseDatabase.getInstance().getReference()
                .child(FBDatabase.PATH_USERS).child(user.getFbUser().getUid());
        if (!user.getEmail().isEmpty()) {
            thisUserReference.child(UserObj.PATH_EMAIL).setValue(user.getEmail());
        }

        if (!user.getDisplayName().isEmpty()) {
            thisUserReference.child(UserObj.PATH_DISPLAY_NAME).setValue(user.getDisplayName());
        }

        Uri photoUri = user.getPhotoUrl();
        if (photoUri != Uri.EMPTY) {
            thisUserReference.child(UserObj.PATH_PHOTO_URL).setValue(String.valueOf(photoUri));
        }

        thisUserReference.child(UserObj.PATH_LAST_ACTIVITY_TIME).setValue(TimeProvider.getUtcTime());
    }

    private void enableButtons() {
        btnSignIn.setEnabled(true);
        btnSignUp.setEnabled(true);
        btnGoogleSignIn.setEnabled(true);
        btnFBSignIn.setEnabled(true);
        //btnVKSignIn.setEnabled(true);
    }

    private void disableButtons() {
        btnSignIn.setEnabled(false);
        btnSignUp.setEnabled(false);
        btnGoogleSignIn.setEnabled(false);
        btnFBSignIn.setEnabled(false);
        //btnVKSignIn.setEnabled(false);
    }

    private boolean validation(String email, String password) {
        Context c = getApplicationContext();
        if (email.isEmpty()) {
            Toast.makeText(c, getString(R.string.auth_activity_empty_email), Toast.LENGTH_SHORT).show();
        } else if (!email.contains("@")) {
            Toast.makeText(c, getString(R.string.auth_activity_bad_email), Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(c, getString(R.string.auth_activity_empty_password), Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(c, getString(R.string.auth_activity_password_too_short), Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }
}
