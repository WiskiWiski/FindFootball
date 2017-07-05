package online.findfootball.android.game.football.screen.create;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

    private FloatingActionButton btnLeft;
    private FloatingActionButton btnRight;

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


        btnLeft = (FloatingActionButton) findViewById(R.id.fab_left);
        btnRight = (FloatingActionButton) findViewById(R.id.fab_right);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.tryGoBack();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
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

    private void updateButtonViews() {
        boolean correct = true;
        if (viewPager != null) {
            correct = viewPager.getCurrentFragment().verifyData(false);
        }
        setBtnEnable(btnRight, correct);
        if (!viewPager.hasNext()) {
            // последний таб
            setBtnEnable(btnLeft, true);
            btnRight.setImageResource(R.drawable.ic_check);
        } else if (!viewPager.hasPreview()) {
            // первый таб
            setBtnEnable(btnLeft, false);
            btnRight.setImageResource(R.drawable.ic_arrow_right);
        } else {
            setBtnEnable(btnLeft, true);
            btnRight.setImageResource(R.drawable.ic_arrow_right);
        }
    }

    @Override
    public void onBackPressed() {
        if (!viewPager.tryGoBack()) {
            super.onBackPressed();
        }
    }

    // Create Game Tab Callback
    @Override
    public void onDoneEdit() {
        viewPager.tryGoNext();
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
    public void onDataStateChange(boolean correct) {
        setBtnEnable(btnRight, correct);
    }

    @Override
    public void onNextTab() {
        if (viewPager.hasPreview()) {
            setBtnEnable(btnLeft, true);
        }
        setBtnEnable(btnRight, viewPager.getCurrentFragment().verifyData(false));
        if (!viewPager.hasNext()) {
            // последний таб
            btnRight.setImageResource(R.drawable.ic_check);
        } else {
            // не устанавливаем ic_arrow_right, т к она стои по умолчанию
            // btnRight.setImageResource(R.drawable.ic_arrow_right);
        }
    }

    @Override
    public void onPreviewTab() {
        if (!viewPager.hasPreview()) {
            // первый таб
            setBtnEnable(btnLeft, false);
        }
        boolean correct = true;
        if (viewPager != null) {
            correct = viewPager.getCurrentFragment().verifyData(false);
        }
        setBtnEnable(btnRight, correct);
        btnRight.setImageResource(R.drawable.ic_arrow_right);
    }

    private void setBtnEnable(FloatingActionButton btn, boolean val) {
        btn.setAlpha(val ? ENABLE_BUTTON_VIEW_ALPHA : DISABLE_BUTTON_VIEW_ALPHA);
        btn.setEnabled(val);
    }
}
