package org.blackstork.findfootball.user.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseUser;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.firebase.database.FBDatabase;
import org.blackstork.findfootball.user.auth.providers.MyEmailAuthAuthProvider;
import org.blackstork.findfootball.user.auth.providers.MyFacebookAuthProvider;
import org.blackstork.findfootball.user.auth.providers.MyGoogleAuthProvider;
import org.blackstork.findfootball.user.auth.providers.MyVkontakteAuthAuthProvider;

public class AuthUiActivity extends AppCompatActivity {

    private static final String TAG = App.G_TAG + ":AuthAct";

    public static final String SIGN_IN_MSG_INTENT_KEY = "sign_in_msg";

    private EditText inputEmail, inputPassword;
    private Button btnSignIn;
    private Button btnSignUp;
    private Button btnGoogleSignIn;
    private Button btnVKSignIn;


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
        // TODO : Show sign in message

        initProviders();


        inputEmail = (EditText) findViewById(R.id.email_et);
        inputPassword = (EditText) findViewById(R.id.password_et);
        btnSignUp = (Button) findViewById(R.id.sign_up_btn);

        btnSignIn = (Button) findViewById(R.id.sign_in_btn);
        btnGoogleSignIn = (Button) findViewById(R.id.google_sign_in_btn);
        btnVKSignIn = (Button) findViewById(R.id.vk_sign_in_btn);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty() && password.length() >= 6) {
                    emailAuthProvider.signIn(email, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Check Input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleAuthProvider.signIn();
            }
        });

        fbAuthProvider.setupButtonListener();

        btnVKSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vkAuthProvider.signIn();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                // TODO: Fix multiple clicks
                emailAuthProvider.signUp(email, password);
            }
        });
    }

    private void initProviders() {
        ProviderCallback providerCallback = new ProviderCallback() {
            @Override
            public void onResult(FirebaseUser user) {
                Log.d(TAG, "Authentication success: " + user.getEmail());
                Toast.makeText(getApplicationContext(), user.getEmail(), Toast.LENGTH_LONG).show();
                FBDatabase.signUpUser(user);
                setResult(UserAuth.RESULT_SUCCESS);
                finish();
            }

            @Override
            public void onFailed(FailedResult result) {
                setResult(UserAuth.RESULT_FAILED);
                Log.d(TAG, "Authentication failed. " + result.toString());
                Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
            }
        };
        emailAuthProvider = new MyEmailAuthAuthProvider(providerCallback);
        googleAuthProvider = new MyGoogleAuthProvider(this, providerCallback);
        fbAuthProvider = new MyFacebookAuthProvider(this,
                (LoginButton) findViewById(R.id.facebook_sign_in_btn), providerCallback);
        vkAuthProvider = new MyVkontakteAuthAuthProvider(this, providerCallback);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleAuthProvider.onActivityResult(requestCode, resultCode, data);
        fbAuthProvider.onActivityResult(requestCode, resultCode, data);
        vkAuthProvider.onActivityResult(requestCode, resultCode, data);
    }
}
