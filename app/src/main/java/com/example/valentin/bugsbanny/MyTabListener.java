package com.example.valentin.bugsbanny;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

public class MyTabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment mFragment;
    private final Activity mActivity;
    private final Class<T> mClass;
    private int fragmentContainer;

    public MyTabListener(Activity activity, int fragmentContainer, Class<T> clz) {
        mActivity = activity;
        mClass = clz;
        this.fragmentContainer = fragmentContainer;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (mFragment == null) {
            String fragmentName = mClass.getName();
            mFragment = Fragment.instantiate(mActivity, fragmentName);
            ft.add(fragmentContainer, mFragment, fragmentName);
        } else {
            ft.attach(mFragment);
        }
        Log.d("myLog", "MyTabListener onTabSelected");
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            ft.detach(mFragment);
        }
        Log.d("myLog", "MyTabListener onTabUnselected");
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
        if (mFragment != null)
            ft.attach(mFragment);
        Log.d("myLog", "MyTabListener onTabReselected");
    }

    public Fragment getmFragment() {
        return mFragment;
    }

    public void setmFragment(Fragment fragment) {
        mFragment = fragment;
    }
}