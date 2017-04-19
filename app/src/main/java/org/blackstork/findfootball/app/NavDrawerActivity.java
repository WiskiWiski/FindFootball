package org.blackstork.findfootball.app;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.blackstork.findfootball.R;

import java.util.HashSet;

/**
 * Created by WiskiW on 16.03.2017.
 */

public class NavDrawerActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = App.G_TAG + ":NavDrawerActivity";

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
        NavDrawerActivity.rootActivity = rootActivity;
    }

    public void setDefaultMenuItemId(int itemId) {
        uncheckedMenu();
        updateMenuItemId(0);
        NavDrawerActivity.defaultMenuItemId = itemId;
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
    public void setContentView(int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_navigation_drawer, null);
        FrameLayout activityContainer = (FrameLayout) drawerLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getMenuItemById(currentMenuItemId).setChecked(true);
    }

    public boolean isDrawerOpen(){
        return drawerLayout.isDrawerOpen(Gravity.START);
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            drawerLayout.closeDrawers();
            return;
        } else if (!isRootChild(currentMenuItemId)) {
            MenuItem menuItem = getMenuItemById(rootActivity.getCurrentViewItemId());
            updateMenuItemSelection(menuItem);
            rootActivity.onNavigationItemSelected(menuItem);
        }
        super.onBackPressed();
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
