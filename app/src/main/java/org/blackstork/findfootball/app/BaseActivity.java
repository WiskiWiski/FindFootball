package org.blackstork.findfootball.app;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.blackstork.findfootball.R;

/**
 * Created by WiskiW on 16.03.2017.
 */

public class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = App.G_TAG + ":BaseActivity";

    private static int currentMenuItemId;

    private static NavigationView.OnNavigationItemSelectedListener rootDrawerListener;
    private static int rootMenuItemId;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    protected MenuItem getMenuItemById(int id) {
        return navigationView.getMenu().findItem(id);
    }

    public void registerRootActivity(NavigationView.OnNavigationItemSelectedListener rootDrawerListener,
                                            int rootMenuItemId) {
        BaseActivity.rootDrawerListener = rootDrawerListener;
        BaseActivity.rootMenuItemId = rootMenuItemId;
        getMenuItemById(rootMenuItemId).setChecked(true);
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
        } else if (currentMenuItemId != rootMenuItemId) {
            MenuItem menuItem = getMenuItemById(rootMenuItemId);
            updateMenuItemSelection(menuItem);
            //updateMenuItemId(menuItem);
            rootDrawerListener.onNavigationItemSelected(menuItem);
            super.onBackPressed();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && currentMenuItemId != rootMenuItemId) {
            MenuItem menuItem = getMenuItemById(rootMenuItemId);
            updateMenuItemSelection(menuItem);
            //updateMenuItemId(menuItem);
            rootDrawerListener.onNavigationItemSelected(menuItem);
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeDrawer();
        if (getCurrentMenuItemId() == menuItem.getItemId()) {
            // Если нажали то, что сейчас открыто
            return false;
        } else {
            finish();
            return rootDrawerListener.onNavigationItemSelected(menuItem);
        }
    }

    @Override
    public void finish() {
        if (currentMenuItemId != rootMenuItemId) {
            MenuItem menuItem = getMenuItemById(rootMenuItemId);
            updateMenuItemSelection(menuItem);
            rootDrawerListener.onNavigationItemSelected(menuItem);
        }
        super.finish();
    }
}
