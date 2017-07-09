package online.findfootball.android.game.football.screen.create;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import online.findfootball.android.game.football.screen.create.fragments.team.size.CGTeamSizeFragment;
import online.findfootball.android.game.football.screen.create.fragments.time.CGGameTimeFragment;
import online.findfootball.android.app.view.verify.view.pager.VerifyTabViewPager;
import online.findfootball.android.app.view.verify.view.pager.VerifyTabsParent;
import online.findfootball.android.app.view.verify.view.pager.VerifycapableTab;
import online.findfootball.android.user.AppUser;

public class CreateGameActivity extends BaseActivity implements
        VerifyTabsParent {

    private static final String TAG = App.G_TAG + ":CreateGameAct";

    public static final String INTENT_GAME_KEY = "game_in_intent";
    private static final float ENABLE_BUTTON_VIEW_ALPHA = 1f;
    private static final float DISABLE_BUTTON_VIEW_ALPHA = 0.2f;

    private FloatingActionButton btnLeft;
    private FloatingActionButton btnRight;

    private VerifyTabViewPager viewPager;

    private GameObj thisGameObject;


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


        viewPager = (VerifyTabViewPager) findViewById(R.id.pager);
        CGPagerAdapter adapter = new CGPagerAdapter(getSupportFragmentManager());
        adapter.addNext(new CGTitleFragment());
        adapter.addNext(new CGDescriptionFragment());
        adapter.addNext(new CGLocationFragment());
        adapter.addNext(new CGTeamSizeFragment());
        adapter.addNext(new CGGameTimeFragment());
        viewPager.setAdapter(adapter);
        viewPager.setParent(this);
        adapter.getItem(0).updateView(thisGameObject);

        Intent intent = getIntent();
        if (intent != null) {
            thisGameObject = intent.getParcelableExtra(INTENT_GAME_KEY);
        }
        if (thisGameObject == null) {
            thisGameObject = new GameObj();
        }

        btnLeft = (FloatingActionButton) findViewById(R.id.fab_left);
        btnRight = (FloatingActionButton) findViewById(R.id.fab_right);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLeftSwipe();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryRightSwipe();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppUser.AUTH_REQUEST_CODE) {
            switch (resultCode) {
                case AppUser.RESULT_SUCCESS:
                    onFinish(thisGameObject);
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
            correct = viewPager.getCurrentTab().verifyData(false);
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
        if (!tryLeftSwipe()) {
            super.onBackPressed();
        }
    }

    public void onFinish(final GameObj game) {
        final AppUser appUser = AppUser.getInstance(this, true);
        if (appUser != null) {
            Formatter formatter = new Formatter();
            formatter.format(getString(R.string.cg_game_created_msg), thisGameObject.getTitle());

            Log.d(TAG, "onFinish: " + formatter);
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

    private void setBtnEnable(FloatingActionButton btn, boolean val) {
        btn.setAlpha(val ? ENABLE_BUTTON_VIEW_ALPHA : DISABLE_BUTTON_VIEW_ALPHA);
        btn.setEnabled(val);
    }

    @Override
    public boolean tryRightSwipe() {
        // Возвращает был ли сменен таб
        if (viewPager.getCurrentTab().verifyData(true)) {
            // сохраняем данных с таба
            saveTabData(viewPager.getCurrentTab());
            if (viewPager.hasNext()) {
                viewPager.goNext();
                viewPager.getCurrentTab().updateView(thisGameObject);
                //onNextTab();
                return true;
            } else {
                onFinish(thisGameObject);
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean tryLeftSwipe() {
        if (!viewPager.hasPreview()) {
            return false;
        }
        // сохраняем данных с предыдущего таба
        saveTabData(viewPager.getCurrentTab());

        viewPager.goBack();
        // обновляем данные в следующем табе
        viewPager.getCurrentTab().updateView(thisGameObject);
        //onPreviewTab();
        return true;
    }

    @Override
    public void onRightSwipe() {
        if (viewPager.hasPreview()) {
            setBtnEnable(btnLeft, true);
        }
        VerifycapableTab tab = viewPager.getCurrentTab();
        if (tab instanceof CGLocationFragment) {
            // TODO : Create onMarkerLocationChange listener in CGLocationFragment
            // временный кусок кода, разрещающий next click на фрагменте выбора местоположения
            setBtnEnable(btnRight, true);
        } else {
            setBtnEnable(btnRight, viewPager.getCurrentTab().verifyData(false));
        }
        if (!viewPager.hasNext()) {
            // последний таб
            btnRight.setImageResource(R.drawable.ic_check);
        } else {
            // не устанавливаем ic_arrow_right, т к она стои по умолчанию
            // btnRight.setImageResource(R.drawable.ic_arrow_right);
        }
    }

    @Override
    public void onLeftSwipe() {
        if (!viewPager.hasPreview()) {
            // первый таб
            setBtnEnable(btnLeft, false);
        }
        boolean correct = true;
        if (viewPager != null) {
            correct = viewPager.getCurrentTab().verifyData(false);
        }
        setBtnEnable(btnRight, correct);
        btnRight.setImageResource(R.drawable.ic_arrow_right);
    }

    @Override
    public void saveTabData(@NonNull VerifycapableTab tab) {
        tab.saveResult(thisGameObject);
    }
}
