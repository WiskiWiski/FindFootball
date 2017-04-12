package org.blackstork.findfootball.app;

import android.app.ActivityManager;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.blackstork.findfootball.R;

import java.util.HashSet;

/**
 * Created by WiskiW on 16.03.2017.
 */

public class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = App.G_TAG + ":BaseActivity";

    private static int currentMenuItemId;
    private static int defaultMenuItemId;

    private static OnRootActivity rootActivity;
    private static HashSet<Integer> rootChildes;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    protected MenuItem getMenuItemById(int id) {
        return navigationView.getMenu().findItem(id);
    }

    public void registerRootActivity(OnRootActivity rootActivity) {
        BaseActivity.rootActivity = rootActivity;
    }

    public void setDefaultMenuItemId(int itemId) {
        uncheckedMenu();
        updateMenuItemId(0);
        BaseActivity.defaultMenuItemId = itemId;
        rootActivity.onNavigationItemSelected(getMenuItemById(getDefaultMenuItemId()));
        //updateMenuItemId(itemId);
        //getMenuItemById(itemId).setChecked(true);
    }

    private void uncheckedMenu() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    public int getDefaultMenuItemId() {
        return defaultMenuItemId;
    }

    public static void addRootActivityChildes(Integer... itemIds) {
        if (rootChildes == null) {
            rootChildes = new HashSet<>();
        }
        for (int id : itemIds) {
            rootChildes.add(id);
        }
    }

    private boolean isRootChild(int id) {
        for (int child : rootChildes) {
            if (id == child) {
                return true;
            }
        }
        return false;
    }

    protected void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    public static int getCurrentMenuItemId() {
        return currentMenuItemId;
    }

    protected void updateMenuItemId(int clickedMenuItemId) {
        currentMenuItemId = clickedMenuItemId;
    }

    protected void updateMenuItemSelection(MenuItem clickedMenuItem) {
        // Убирает выделение текущего меню-элемента
        getMenuItemById(currentMenuItemId).setChecked(false);

        // Устанавливает выделение на нажатом меню-элементе
        getMenuItemById(clickedMenuItem.getItemId()).setChecked(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO : allow changing screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String title = getResources().getString(R.string.app_name);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            int color = ContextCompat.getColor(this, R.color.colorPrimary600);

            ActivityManager.TaskDescription taskDescription =
                    new ActivityManager.TaskDescription(title, icon, color);
            setTaskDescription(taskDescription);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) drawerLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getMenuItemById(currentMenuItemId).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
            return;
        } else if (!isRootChild(currentMenuItemId)) {
            MenuItem menuItem = getMenuItemById(rootActivity.getCurrentViewItemId());
            updateMenuItemSelection(menuItem);
            rootActivity.onNavigationItemSelected(menuItem);
        }
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeDrawer();
        int id = menuItem.getItemId();
        if (getCurrentMenuItemId() == id) {
            // Если нажали то, что сейчас открыто
            return false;
        } else {
            if (!isRootChild(currentMenuItemId)) {
                finish();
            }
            return rootActivity.onNavigationItemSelected(menuItem);
        }
    }

    @Override
    public void finish() {
        if (!isRootChild(currentMenuItemId)) {
            MenuItem menuItem = getMenuItemById(rootActivity.getCurrentViewItemId());
            updateMenuItemSelection(menuItem);
            rootActivity.onNavigationItemSelected(menuItem);
        }
        super.finish();
    }

    public interface OnRootActivity {
        int getCurrentViewItemId();

        boolean onNavigationItemSelected(MenuItem menuItem);
    }

}
