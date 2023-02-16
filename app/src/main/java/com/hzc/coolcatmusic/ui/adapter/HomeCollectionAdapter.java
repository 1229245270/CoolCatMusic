package com.hzc.coolcatmusic.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hzc.coolcatmusic.ui.main.HomeFragment1;
import com.hzc.coolcatmusic.ui.main.HomeFragment2;
import com.hzc.coolcatmusic.ui.main.HomeFragment3;

public class HomeCollectionAdapter extends FragmentStateAdapter {

    public HomeCollectionAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public HomeCollectionAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public HomeCollectionAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    private final Fragment[] fragments = {
            new HomeFragment1(),
            new HomeFragment2(),
            new HomeFragment3(),
    };

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}
