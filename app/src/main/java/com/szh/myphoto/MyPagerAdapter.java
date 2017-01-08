package com.szh.myphoto;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by szh on 2017/1/8.
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter {
    private int size;

    private List<BaseFragment> list;

    public MyPagerAdapter(FragmentManager fm, int size, List<BaseFragment> list) {
        super(fm);
        this.size = size;
        this.list = list;
    }

    @Override
    public BaseFragment getItem(int position) {
        BaseFragment fragment = list.get(position % 4);
        fragment.setPosition(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return size;
    }
}
