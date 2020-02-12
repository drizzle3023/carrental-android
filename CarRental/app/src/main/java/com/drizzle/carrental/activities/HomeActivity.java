package com.drizzle.carrental.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.drizzle.carrental.fragments.CoverageFragmentEmpty;
import com.drizzle.carrental.fragments.CoverageFragmentFull;
import com.drizzle.carrental.fragments.HistoryFragmentEmpty;
import com.drizzle.carrental.fragments.HistoryFragmentFull;
import com.drizzle.carrental.fragments.ProfileFragmentFull;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.fragments.ProfileFragmentEmpty;
import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final int ADD_COVERAGE_ACTIVITY_REQUEST = 1;

    protected Fragment curFragment;
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coverage_ready);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        if (savedInstanceState == null) {

            Log.d("tiny-debug()", "onCreate: HomeActivity");
            if (Globals.isLoggedIn) {

                Log.d("tiny-debug()", "onCreate: isLoggedIn");
                if (Globals.profile.getPayState() == 1) {
                    Log.d("tiny-debug()", "onCreate: PayState = 1");
                    bottomNavigationView.setSelectedItemId(R.id.navigation_coverage);
                    //              showFragment(R.id.frame_coverage, CoverageFragmentFull.class);
                } else {
                    Log.d("tiny-debug()", "onCreate: PayState = 0");
//                showFragment(R.id.frame_coverage, ProfileFragmentFull.class);
                    bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
                }

            } else {

                Log.d("tiny-debug()", "onCreate: isLoggedInFalse");
                bottomNavigationView.setSelectedItemId(R.id.navigation_coverage);
                //        showFragment(R.id.frame_coverage, CoverageFragmentEmpty.class);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (Globals.isLoggedIn) {

            Log.d("tiny-debug()", "onNavigationItemSelected: Globals.isLoggedIn = 1");
            if (menuItem.getItemId() == R.id.navigation_history) {
                Log.d("tiny-debug()", "onNavigationItemSelected: R.id.navigation_history");
                showFragment(R.id.frame_history, HistoryFragmentFull.class);
            } else if (menuItem.getItemId() == R.id.navigation_coverage) {
                Log.d("tiny-debug()", "onNavigationItemSelected: R.id.navigation_coverage");
                if (Globals.profile.getPayState() == 1) {

                    Log.d("tiny-debug()", "onNavigationItemSelected: R.id.navigation_coverage : PayState = 1");
                    showFragment(R.id.frame_coverage, CoverageFragmentFull.class);
                } else {

                    Log.d("tiny-debug()", "onNavigationItemSelected: R.id.navigation_coverage : PayState = 0");
                    showFragment(R.id.frame_coverage, CoverageFragmentEmpty.class);
                }
            } else if (menuItem.getItemId() == R.id.navigation_profile) {

                Log.d("tiny-debug()", "onNavigationItemSelected: R.id.navigation_profile");
                showFragment(R.id.frame_profile, ProfileFragmentFull.class);
            }

        } else {

            Log.d("tiny-debug()", "onNavigationItemSelected: Globals.isLoggedIn = false");
            if (menuItem.getItemId() == R.id.navigation_history) {
                Log.d("tiny-debug()", "onNavigationItemSelected: menuItem.getItemId() == R.id.navigation_history");
                showFragment(R.id.frame_history, HistoryFragmentEmpty.class);
            } else if (menuItem.getItemId() == R.id.navigation_coverage) {
                Log.d("tiny-debug()", "onNavigationItemSelected: menuItem.getItemId() == R.id.navigation_coverage");
                showFragment(R.id.frame_coverage, CoverageFragmentEmpty.class);
            } else if (menuItem.getItemId() == R.id.navigation_profile) {
                Log.d("tiny-debug()", "onNavigationItemSelected: menuItem.getItemId() == R.id.navigation_profile");
                showFragment(R.id.frame_profile, ProfileFragmentEmpty.class);
            }

        }

        return true;

    }

    public void showFragment(int layoutId, Class fragClass) {

        showFragment(layoutId, fragClass, null);

    }

    public void showFragment(int layoutId, Class fragClass, Bundle bundle) {

        if (fragClass == null)
            return;

        if (fragClass.isInstance(curFragment))
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if (curFragment != null) {
            fragmentTransaction.hide(curFragment);
        }

        Fragment fragment = fragmentManager.findFragmentByTag(fragClass.toString());

        try {
            if (fragment == null) {
                fragment = (Fragment) fragClass.newInstance();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(layoutId, fragment, fragClass.toString());
            } else {
                fragment.setArguments(bundle);
                fragmentTransaction.show(fragment);
                if (layoutId == R.id.frame_history && Globals.isLoggedIn && Constants.needHistoryRefresh) {
                    fragment.onResume();
                }
            }
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
                e.printStackTrace();
        }

        curFragment = fragment;
        fragmentTransaction.commit();

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.press_back_again), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        updateView();

    }

    public void updateView() {

        if (Globals.isLoggedIn) {
            showFragment(R.id.frame_coverage, CoverageFragmentFull.class);
        }
    }
}
