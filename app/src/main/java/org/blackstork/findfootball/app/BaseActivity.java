package org.blackstork.findfootball.app;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.blackstork.findfootball.MainActivity;
import org.blackstork.findfootball.R;

/**
 * Created by WiskiW on 16.03.2017.
 */

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static int menuItemId = R.id.nav_main;
    private DrawerLayout drawerLayout;

    protected void onNavigationClick(MenuItem menuItem) {
        getCurrentMenuItem().setChecked(false);
        menuItem.setChecked(true);
        BaseActivity.menuItemId = menuItem.getItemId();
        closeDrawer();
    }

    protected MenuItem getCurrentMenuItem() {
        return ((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(menuItemId);
    }

    protected void closeDrawer() {
        if (drawerLayout != null)
            drawerLayout.closeDrawers();
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(menuItemId).setChecked(true);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        onNavigationClick(item);

        int id = item.getItemId();
        if (id == R.id.nav_main) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        } else {
            return false;
        }
        return true;
    }
}
