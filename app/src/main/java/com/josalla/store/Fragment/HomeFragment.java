package com.josalla.store.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josalla.store.Adapters.CategoryPagerAdapter;
import com.josalla.store.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View home = inflater.inflate(R.layout.fragment_home, container, false);

        preferences = getActivity().getSharedPreferences("MASTEKA" , Context.MODE_PRIVATE);
        editor = preferences.edit();




        //================================================ tab adapter =======================================
        CategoryPagerAdapter categoryPagerAdapter = new CategoryPagerAdapter(this, getChildFragmentManager());
        ViewPager viewPager = home.findViewById(R.id.view_pager);

        viewPager.setAdapter(categoryPagerAdapter);
        SmartTabLayout viewPagerTab =  home.findViewById(R.id.viewpagertab);
        viewPagerTab.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        viewPagerTab.setViewPager(viewPager);






        return home;
    }




}
