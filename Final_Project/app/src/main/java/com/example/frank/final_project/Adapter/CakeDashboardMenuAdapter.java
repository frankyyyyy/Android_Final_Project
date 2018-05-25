package com.example.frank.final_project.Adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CakeDashboardMenuAdapter extends PagerAdapter {

    private ArrayList<View> viewLists;
    private ArrayList<String> titleLists;

    public CakeDashboardMenuAdapter(ArrayList<View> viewLists,ArrayList<String> titleLists){
        this.viewLists = viewLists;
        this.titleLists = titleLists;
    }
    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewLists.get(position));
        return viewLists.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewLists.get(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleLists.get(position);
    }
}
