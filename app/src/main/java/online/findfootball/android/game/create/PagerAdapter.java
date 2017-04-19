package online.findfootball.android.game.create;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WiskiW on 02.04.2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    private List<BaseCGFragment> list;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        list = new ArrayList<>();
    }

    public void addNext(BaseCGFragment tabFragment) {
        list.add(tabFragment);
    }

    @Override
    public BaseCGFragment getItem(int position) {
        if (position >= getCount()) return null;
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BaseCGFragment fragment = (BaseCGFragment) super.instantiateItem(container, position);
        list.set(position, fragment);
        return fragment;
    }

}