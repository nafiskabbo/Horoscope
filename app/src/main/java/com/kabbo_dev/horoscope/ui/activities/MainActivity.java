package com.kabbo_dev.horoscope.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.kabbo_dev.horoscope.utils.Functions;
import com.kabbo_dev.horoscope.R;
import com.kabbo_dev.horoscope.ui.fragments.FamilyMembersFragment;
import com.kabbo_dev.horoscope.ui.fragments.HomeFragment;
import com.kabbo_dev.horoscope.ui.fragments.MyAccountFragment;

public class MainActivity extends AppCompatActivity {

    public static ConstraintLayout mainActivityLayout;
    public static Window window;
    Toolbar toolbar;
    AppBarLayout toolbarLayout;

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarLayout = findViewById(R.id.toolbar);

        toolbar = findViewById(R.id.toolbar_widget);
        setSupportActionBar(toolbar);

        window = getWindow();
        mainActivityLayout = findViewById(R.id.cons_layout);

        chipNavigationBar = findViewById(R.id.navigation_bar);
        chipNavigationBar.setItemSelected(R.id.home_menu, true);

        Functions.setDefaultFragment(MainActivity.this, R.id.main_layout, new HomeFragment());

        bottomMenu();
    }

    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(menu -> {
            Fragment fragment = null;

            switch (menu) {
                case R.id.home_menu:
                    toolbarLayout.setVisibility(View.GONE);

                    fragment = new HomeFragment();
                    break;

                case R.id.family_members:
                    window.setStatusBarColor(getColor(R.color.colorPrimary));
                    mainActivityLayout.setBackgroundColor(getColor(R.color.background));
                    toolbarLayout.setVisibility(View.VISIBLE);

                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    getSupportActionBar().setTitle(getString(R.string.family_members));

                    fragment = new FamilyMembersFragment();
                    break;

                case R.id.my_account:
                    window.setStatusBarColor(getColor(R.color.colorPrimary));
                    mainActivityLayout.setBackgroundColor(getColor(R.color.background));
                    toolbarLayout.setVisibility(View.VISIBLE);

                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    getSupportActionBar().setTitle(getString(R.string.my_account));

                    fragment = new MyAccountFragment();
                    break;
            }

            Functions.setFragment(MainActivity.this, R.id.main_layout, fragment, R.anim.fade_in, R.anim.fade_out);

        });
    }


}