package online.findfootball.android.game.football.screen.create;

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

import java.util.Formatter;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.app.BaseActivity;
import online.findfootball.android.background.tasks.BgTaskMaker;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.object.FootballPlayer;
import online.findfootball.android.game.football.screen.create.fragments.CGDescriptionFragment;
import online.findfootball.android.game.football.screen.create.fragments.CGLocationFragment;
import online.findfootball.android.game.football.screen.create.fragments.CGTitleFragment;
import online.findfootball.android.game.football.screen.create.fragments.team.size.CGTeamSizeFragment;
import online.findfootball.android.game.football.screen.create.fragments.time.CGGameTimeFragment;
import online.findfootball.android.game.football.screen.create.view.CreateGameViewPager;
import online.findfootball.android.user.AppUser;

public class CreateGameActivity extends BaseActivity implements
        BaseCGFragment.CGTabEditListener,
        CreateGameViewPager.CreateGameListener {

    private static final String TAG = App.G_TAG + ":CreateGameAct";

    public static final String INTENT_GAME_KEY = "game_in_intent";
    private static final float ENABLE_BUTTON_VIEW_ALPHA = 1f;
    private static final float DISABLE_BUTTON_VIEW_ALPHA = 0.2f;

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


        viewPager = (CreateGameViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addNext(new CGTitleFragment());
        adapter.addNext(new CGDescriptionFragment());
        adapter.addNext(new CGLocationFragment());
        adapter.addNext(new CGTeamSizeFragment());
        adapter.addNext(new CGGameTimeFragment());
        viewPager.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            viewPager.setGameObj((GameObj) intent.getParcelableExtra(INTENT_GAME_KEY));
        }
        if (viewPager.getGameObj() == null) {
            viewPager.setGameObj(new GameObj());
        }
        viewPager.setCreateGameListener(this);


        leftButton = (FrameLayout) findViewById(R.id.left_btn);
        rightButton = (FrameLayout) findViewById(R.id.right_btn);


        backTabBtn = (LinearLayout) findViewById(R.id.back_btn);
        nextTabBtn = (LinearLayout) findViewById(R.id.next_btn);
        finishBtn = (LinearLayout) findViewById(R.id.finish_btn);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.tryGoBack();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.tryGoNext();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppUser.AUTH_REQUEST_CODE) {
            switch (resultCode) {
                case AppUser.RESULT_SUCCESS:
                    onGameCreated(viewPager.getGameObj());
                    break;
                case AppUser.RESULT_FAILED:

                    break;
                case AppUser.RESULT_CANCEL:

                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonViews();
    }

    private LinearLayout getNextButtonView() {
        if (!viewPager.hasNext()) {
            // последний таб
            return finishBtn;
        } else {
            // другой таб
            return nextTabBtn;
        }
    }

    private void updateButtonViews() {
        if (!viewPager.hasNext()) {
            // последний таб
            showPreviewBtn(true);
            showNextBtn(false);
            showFinishBtn(true);
        } else if (!viewPager.hasPreview()) {
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
        if (!viewPager.tryGoBack()) {
            super.onBackPressed();
        }
    }

    private void showPreviewBtn(boolean bool) {
        backTabBtn.setEnabled(bool);
        backTabBtn.setAlpha(bool ? ENABLE_BUTTON_VIEW_ALPHA : DISABLE_BUTTON_VIEW_ALPHA);
        leftButton.setEnabled(bool);
    }

    private void showNextBtn(boolean bool) {
        nextTabBtn.setEnabled(bool);
        nextTabBtn.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);

        if (viewPager != null) {
            // проверяем состояние данных в табе
            // в завистимости от корректности включаем/выключаем кнопку
            nextTabBtn.setAlpha(viewPager.getCurrentFragment().verifyData(false) ?
                    ENABLE_BUTTON_VIEW_ALPHA : DISABLE_BUTTON_VIEW_ALPHA);
        }

        if (bool) {
            finishBtn.setEnabled(false);
            finishBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void showFinishBtn(boolean bool) {
        finishBtn.setEnabled(bool);
        finishBtn.setVisibility(bool ? View.VISIBLE : View.INVISIBLE);

        if (viewPager != null) {
            // проверяем состояние данных в табе
            // в завистимости от корректности включаем/выключаем кнопку
            finishBtn.setAlpha(viewPager.getCurrentFragment().verifyData(false) ?
                    ENABLE_BUTTON_VIEW_ALPHA : DISABLE_BUTTON_VIEW_ALPHA);
        }

        if (bool) {
            nextTabBtn.setEnabled(false);
            nextTabBtn.setVisibility(View.INVISIBLE);
        }
    }

    // Create Game Tab Callback
    @Override
    public void onDoneEdit() {
        viewPager.tryGoNext();
    }

    public void onDataStateChange(boolean correct) {
        getNextButtonView().setAlpha(correct ? ENABLE_BUTTON_VIEW_ALPHA : DISABLE_BUTTON_VIEW_ALPHA);
    }

    @Override
    public void onGameCreated(final GameObj game) {
        final AppUser appUser = AppUser.getInstance(this, true);
        if (appUser != null) {
            Formatter formatter = new Formatter();
            formatter.format(getString(R.string.cg_game_created_msg), viewPager.getGameObj().getTitle());

            Log.d(TAG, "onGameCreated: " + formatter);
            Context context = getApplicationContext();
            Toast.makeText(context, formatter.toString(), Toast.LENGTH_LONG).show();

            game.setOwnerUser(appUser);


            FootballPlayer player = new FootballPlayer(appUser);
            // TODO: выбор позиции для владельца
            game.getTeams().getTeamA().add(player); // добавляем игрока в команду
            appUser.joinGame(game); // добавляем игру к пользователю
            BgTaskMaker.newMaker(getApplicationContext())
                    .setCompleteListener(new BgTaskMaker.OnBgCompleteListener() {
                        @Override
                        public void onComplete() {
                            game.save();
                            finish();
                        }
                    }).make(game.getLocation());
        }
    }

    @Override
    public void onNextTab() {
        updateButtonViews();
    }

    @Override
    public void onPreviewTab() {
        updateButtonViews();
    }
}
