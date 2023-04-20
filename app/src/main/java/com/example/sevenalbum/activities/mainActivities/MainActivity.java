package com.example.sevenalbum.activities.mainActivities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sevenalbum.R;
import com.example.sevenalbum.adapters.ViewPagerAdapter;
import com.example.sevenalbum.utility.GetAllPhotoFromGallery;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.karan.churi.PermissionManager.PermissionManager;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager;
    PermissionManager permission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Hide status bar

        /*getSupportActionBar();*/
        setContentView(R.layout.activity_main);



        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.view_pager);

        permission = new PermissionManager() {
            @Override
            public void ifCancelledAndCannotRequest(Activity activity) {
            }
        };

        permission.checkAndRequestPermissions(this);
        setUpViewPager();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.photo:
                        viewPager.setCurrentItem(0, false);
                        break;

                    case R.id.album:
                        viewPager.setCurrentItem(1, false);
                        break;


                    case R.id.favorite:
                        viewPager.setCurrentItem(2, false);
                        break;

                    case R.id.secret:
                        viewPager.setCurrentItem(3, false);
                        break;

                }
                return true;
            }
        });



    }


    // Toolbar handle


    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPagerAdapter.setContext(getApplicationContext());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.photo).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.album).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.favorite).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.secret).setChecked(true);
                        break;
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permission.checkResult(requestCode, permissions, grantResults);
        GetAllPhotoFromGallery.refreshAllImages();

    }

}