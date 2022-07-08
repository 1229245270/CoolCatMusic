package com.hzc.coolCatMusic.event;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class ChangeFragmentEvent {
    private Fragment showFragment;
    private Bundle bundle;

    public ChangeFragmentEvent(Fragment showFragment, Bundle bundle) {
        this.showFragment = showFragment;
        this.bundle = bundle;
    }

    public Fragment getShowFragment() {
        return showFragment;
    }

    public void setShowFragment(Fragment showFragment) {
        this.showFragment = showFragment;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
