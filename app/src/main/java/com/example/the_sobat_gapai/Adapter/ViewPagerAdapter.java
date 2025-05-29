package com.example.the_sobat_gapai.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.the_sobat_gapai.Fragment.FriendFragment;
import com.example.the_sobat_gapai.Fragment.HomeFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final String[] tabTitles = new String[]{"Akun", "Post"};
    private final Fragment[] fragments = new Fragment[]{new FriendFragment(), new HomeFragment()};

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
