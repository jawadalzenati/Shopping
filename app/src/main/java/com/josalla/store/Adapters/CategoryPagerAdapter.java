package com.josalla.store.Adapters;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.josalla.store.Fragment.HomeFragment;
import com.josalla.store.R;
import com.josalla.store.TabFragment.BeautyFragment;
import com.josalla.store.TabFragment.ElectronicsFragment;
import com.josalla.store.TabFragment.ShoosFragment;
import com.josalla.store.TabFragment.WomenFragment;
import com.josalla.store.TabFragment.RecentFragment;
import com.josalla.store.TabFragment.MothersFragment;
import com.josalla.store.TabFragment.CartsyFragment;
import com.josalla.store.TabFragment.HomesFragment;
import com.josalla.store.TabFragment.MenFragment;


public class CategoryPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.recent,
            R.string.women, R.string.men, R.string.shoos, R.string.mothers, R.string.beauty, R.string.home, R.string.cartsy
            , R.string.electronic};


    private final HomeFragment mContext;
    private Activity activity;

    public CategoryPagerAdapter(HomeFragment context, FragmentManager fm) {
        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new RecentFragment();
                break;
            case 1:
                fragment = new WomenFragment();
                break;
            case 2:
                fragment = new MenFragment();
                break;
            case 3:
                fragment = new ShoosFragment();
                break;
            case 4:
                fragment = new MothersFragment();
                break;
            case 5:
                fragment = new BeautyFragment();
                break;
            case 6:
                fragment = new HomesFragment();
                break;
            case 7:
                fragment = new CartsyFragment();
                break;
            case 8:
                fragment = new ElectronicsFragment();
                break;
            default:
                fragment = new RecentFragment();
                break;
        }


        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {


        return mContext.getResources().getString(TAB_TITLES[position]);


    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 9;
    }
}