package com.dldud.riceapp;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by dldud on 2018-03-24.
 * 소셜페이지 pageAdapter 입니다.
 */

public class PageAdapter extends FragmentPagerAdapter {

    private int tabCount;

    public PageAdapter(FragmentManager fm, int tabCount){
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch(position){
            case 0:
                MyViewFragment myViewFragment = new MyViewFragment();
                return myViewFragment;
            case 1:
                ConnectViewFragment connectViewFragment = new ConnectViewFragment();
                return connectViewFragment;
            case 2:
                PingFragment pingFragment = new PingFragment();
                return pingFragment;
            case 3:
                PingConnectFragment pingConnectFragment = new PingConnectFragment();
                return pingConnectFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
