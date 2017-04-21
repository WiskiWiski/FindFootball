package online.findfootball.android.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.app.BaseActivity;
import online.findfootball.android.firebase.database.DatabaseInstance;

public class ProfileActivity extends BaseActivity
        implements DatabaseInstance.OnLoadListener {

    private static final String TAG = App.G_TAG + ":ProfileGameAct";
    public static final String INTENT_USER_KEY = "user_key_bla_bla";

    private UserObj thisUserObj;

    private ProgressBar progressBarView;
    private View userContainerView;
    private TextView nameView;
    private TextView emailView;
    private ImageView photoView;
    private Button signOutBtn;


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initToolbar();

        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);
        userContainerView = findViewById(R.id.user_container);
        nameView = (TextView) findViewById(R.id.user_name);
        emailView = (TextView) findViewById(R.id.user_email);
        photoView = (ImageView) findViewById(R.id.user_photo);
        signOutBtn = (Button) findViewById(R.id.sign_out_btn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUser.signOut();
                finish();
            }
        });


        Intent intent = getIntent();
        if (intent != null) {
            thisUserObj = intent.getParcelableExtra(INTENT_USER_KEY);
            if (thisUserObj == null) {
                intentDataNotFound();
                return;
            }
            showProgress(true);
            thisUserObj.load(this);


        } else {
            intentDataNotFound();
        }
    }


    private void intentDataNotFound() {
        Log.e(TAG, "intentDataNotFound: you must put user object into intent!");
        finish();
    }

    private void showProgress(boolean inProgress) {
        userContainerView.setVisibility(inProgress ? View.GONE : View.VISIBLE);
        progressBarView.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        signOutBtn.setEnabled(!inProgress);
    }

    @Override
    public void onSuccess(DatabaseInstance instance) {
        showProgress(false);
        thisUserObj = (UserObj) instance;
        nameView.setText(thisUserObj.getDisplayName());
        emailView.setText(thisUserObj.getEmail());

        if (thisUserObj.getPhotoUrl() != null) {
            Glide
                    .with(this)
                    .load(thisUserObj.getPhotoUrl())
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(photoView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            photoView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

    }

    @Override
    public void onFailed(int code, String msg) {
        showProgress(false);
        Log.w(TAG, "onFailed[" + code + "]: " + msg);
        Toast.makeText(this, "onFailed[" + code + "]: " + msg, Toast.LENGTH_LONG).show();
        finish();
    }
}
