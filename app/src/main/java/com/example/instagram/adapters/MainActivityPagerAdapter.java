package com.example.instagram.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.instagram.CreatePostFragment;
import com.example.instagram.MainFeedFragment;
import com.example.instagram.ProfileFragment;

import java.util.InputMismatchException;

public class MainActivityPagerAdapter extends FragmentStateAdapter {
    private final static int MAIN_ACTIVITY_SCREEN_COUNT = 3;

    public MainActivityPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return MainFeedFragment.newInstance();
            case 1:
                return CreatePostFragment.newInstance();
            case 2:
                return ProfileFragment.newInstance();
            default:
                throw new InputMismatchException("Position value for Main Activity View Pager Invalid");
        }
    }

    @Override
    public int getItemCount() {
        return MAIN_ACTIVITY_SCREEN_COUNT;
    }
}
