package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.instagram.adapters.MainActivityPagerAdapter;
import com.example.instagram.databinding.ActivityMainBinding;
import com.example.instagram.interfaces.ProfileViewRequestListener;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements ProfileViewRequestListener {
    private final static String TAG = "MainActivity";

    ActivityMainBinding binding;
    MainActivityPagerAdapter mainActivityPagerAdapter;
    FragmentManager fragmentManager;
    private static ProfileViewRequestListener nullableProfileViewRequestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityPagerAdapter = new MainActivityPagerAdapter(this);
        binding.pager.setAdapter(mainActivityPagerAdapter);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragmentManager = getSupportFragmentManager();

        nullableProfileViewRequestListener = this;


        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: " + position);
                switch (position) {
                    case 0:
                        binding.bottomNavigation.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        binding.bottomNavigation.setSelectedItemId(R.id.create);
                        break;
                    case 2:
                        binding.bottomNavigation.setSelectedItemId(R.id.profile);
                        break;
                }
                super.onPageSelected(position);
            }
        });

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fragmentManager.popBackStack();
                boolean smoothScrollEnabled = fragmentManager.getFragments().size() <= MainActivityPagerAdapter.MAIN_ACTIVITY_SCREEN_COUNT;
                int id = item.getItemId();
                if (id == R.id.home) {
                    binding.pager.setCurrentItem(0, smoothScrollEnabled);
                    return true;
                }
                else if (id == R.id.create) {
                    binding.pager.setCurrentItem(1, smoothScrollEnabled);
                    return true;
                }
                else if (id == R.id.profile) {
                    binding.pager.setCurrentItem(2, smoothScrollEnabled);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Nullable
    public static ProfileViewRequestListener tryToGetProfileLauncher() {
        return nullableProfileViewRequestListener;
    }

    @Override
    public void viewProfile(ParseUser user) {
        if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            binding.bottomNavigation.setSelectedItemId(R.id.profile);
            fragmentManager.popBackStack();
            return;
        }
        fragmentManager
                .beginTransaction()
                .add(binding.mainFragmentHolder.getId(), ProfileFragment.newInstance(user))
                .addToBackStack(user.getObjectId())
                .commit();
    }
}