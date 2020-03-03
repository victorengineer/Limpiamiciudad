package com.victorengineer.limpiamiciudad.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.victorengineer.limpiamiciudad.BaseActivity;
import com.victorengineer.limpiamiciudad.R;
import com.victorengineer.limpiamiciudad.models.Report;
import com.victorengineer.limpiamiciudad.models.Result;
import com.victorengineer.limpiamiciudad.util.ResultListener;

import java.util.Date;

public class MapActivity extends BaseActivity implements  BottomNavigationView.OnNavigationItemSelectedListener{

    private MapFragment mapFragment;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        bottomNavigationView = findViewById(R.id.bottom_nav_home);
        setBottomNavView();

        MapFragment mapFragment = MapFragment.newInstance();
        addOrReplaceFragment(mapFragment, R.id.fragment_map_container);


    }

    private void setBottomNavView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            iconView.setPadding(0,0,0,0);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,0);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            iconView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.menu_home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == R.id.menu_map) {
            startActivity(new Intent(this, MapActivity.class));
            finish();
        } else if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
        return false;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}
