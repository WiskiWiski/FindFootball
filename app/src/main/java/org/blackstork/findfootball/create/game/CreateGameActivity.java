package org.blackstork.findfootball.create.game;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import org.blackstork.findfootball.R;
import org.blackstork.findfootball.app.App;
import org.blackstork.findfootball.app.BaseActivity;
import org.blackstork.findfootball.auth.UserAuth;
import org.blackstork.findfootball.create.game.fragments.CGDescriptionFragment;
import org.blackstork.findfootball.create.game.fragments.CGLocationFragment;
import org.blackstork.findfootball.create.game.fragments.CGTempFragment;
import org.blackstork.findfootball.create.game.fragments.CGTitleFragment;
import org.blackstork.findfootball.create.game.view.CreateGameViewPager;
import org.blackstork.findfootball.firebase.database.FBFootballDatabase;
import org.blackstork.findfootball.firebase.database.FBUserDatabase;
import org.blackstork.findfootball.objects.GameObj;

import java.util.Formatter;

public class CreateGameActivity extends BaseActivity implements
        BaseCGFragment.CGTabEditListener {

    private static final String TAG = App.G_TAG + ":CreateGameAct";

    private GameObj thisGameObj;

    private FrameLayout leftButton;
    private FrameLayout rightButton;

    private LinearLayout backTabBtn;
    private LinearLayout nextTabBtn;
    private LinearLayout finishBtn;

    private CreateGameViewPager viewPager;
    private PagerAdapter adapter;

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        initToolbar();

        thisGameObj = new GameObj();
        adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addNext(new CGTitleFragment());
        adapter.addNext(new CGDescriptionFragment());
        adapter.addNext(new CGLocationFragment());
        adapter.addNext(new CGTempFragment());

        viewPager = (CreateGameViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        leftButton = (FrameLayout) findViewById(R.id.left_btn);
        rightButton = (FrameLayout) findViewById(R.id.right_btn);


        backTabBtn = (LinearLayout) findViewById(R.id.back_btn);
        nextTabBtn = (LinearLayout) findViewById(R.id.next_btn);
        finishBtn = (LinearLayout) findViewById(R.id.finish_btn);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewClick();
            }
        });


        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryNextButtonClick();
            }
        });

    }

    private void onGameCreated() {
        // сохраняем данных с предыдущего таба
        if (!viewPager.getCurrentFragment().saveResult(true, thisGameObj)) {
            return;
        }
        FirebaseUser user = UserAuth.getUser(this);
        if (user == null) {
            UserAuth.requestUser(this);
        } else {
            Formatter formatter = new Formatter();
            formatter.format(getString(R.string.cg_game_created_msg), thisGameObj.getTitle());

            Log.d(TAG, "onGameCreated: " + formatter);

            Context context = getApplicationContext();
            Toast.makeText(context, formatter.toString(), Toast.LENGTH_LONG).show();

            FBFootballDatabase.newInstance(context).saveGame(thisGameObj, user.getUid());
            FBUserDatabase.newInstance(context, user.getUid())
                    .addFootballEvent(thisGameObj.getEid(), FBUserDatabase.USER_ROLE_OWNER);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UserAuth.AUTH_REQUEST_CODE) {
            switch (resultCode) {
                case UserAuth.RESULT_SUCCESS:
                    onGameCreated();
                    break;
                case UserAuth.RESULT_FAILED:

                    break;
                case UserAuth.RESULT_CANCEL:

                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonsVisibility();
    }

    private boolean previewClick() {
        // return: был ли сменен таб
        viewPager.getCurrentFragment().saveResult(false, thisGameObj); // сохраняем данных с предыдущего таба
        if (viewPager.goBack()) {
            updateButtonsVisibility();
            // обновляем данные в следующем табе
            viewPager.getCurrentFragment().updateView(thisGameObj);
            return true;
        }
        return false;
    }

    private void tryNextButtonClick() {
        if (viewPager.getCurrentItem() == adapter.getCount() - 1) {
            onGameCreated();
        } else {
            nextClick();
        }
    }

    private void nextClick() {
        // сохраняем данных с предыдущего таба
        if (!viewPager.getCurrentFragment().saveResult(true, thisGameObj)) {
            return;
        }
        if (viewPager.goNext()) {
            updateButtonsVisibility();
            // обновляем данные в следующем табе
            viewPager.getCurrentFragment().updateView(thisGameObj);
        }
    }

    private void updateButtonsVisibility() {
        int current = viewPager.getCurrentItem();
        if (current == adapter.getCount() - 1) {
            // последний таб
            showPreviewBtn(true);
            showNextBtn(false);
            showFinishBtn(true);
        } else if (current == 0) {
            // первый таб
            showPreviewBtn(false);
            showNextBtn(true);
            showFinishBtn(false);
        } else {
            showPreviewBtn(true);
            showNextBtn(true);
            showFinishBtn(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.hasPreview()) {
            previewClick();
        } else {
            super.onBackPressed();
        }
    }

    private void showPreviewBtn(boolean bool) {
        backTabBtn.setEnabled(bool);
        //backTabBtn.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);
        backTabBtn.setAlpha(bool ? 1f : 0.2f);
        leftButton.setEnabled(bool);
    }

    private void showNextBtn(boolean bool) {
        nextTabBtn.setEnabled(bool);
        nextTabBtn.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);

        if (bool) {
            finishBtn.setEnabled(false);
            finishBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void showFinishBtn(boolean bool) {
        finishBtn.setEnabled(bool);
        finishBtn.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);

        if (bool) {
            nextTabBtn.setEnabled(false);
            nextTabBtn.setVisibility(View.INVISIBLE);
        }
    }

    // Create Game Tab Callback
    @Override
    public void onDoneEdit() {
        tryNextButtonClick();
    }
}
