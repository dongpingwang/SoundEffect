package com.flyaudio.soundeffect.comm.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;


/**
 * @author Dongping Wang
 * date 2020/4/28  0:00
 * email wangdongping@flyaudio.cn
 */
public class NoScrollLinearLayoutManager extends LinearLayoutManager {

    private boolean canScroll = false;

    public NoScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }


    @Override
    public boolean canScrollHorizontally() {
        return canScroll && super.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return canScroll && super.canScrollVertically();
    }

}
