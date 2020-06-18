package com.flyaudio.soundeffect.equalizer.util;

import android.os.SystemClock;
import android.view.View;
import com.flyaudio.lib.constant.TimeUnit;
import com.flyaudio.soundeffect.config.AppPreferences;
import com.flyaudio.soundeffect.equalizer.bean.EqDataBean;
import com.flyaudio.soundeffect.equalizer.dialog.EqDataDetailDialog;

/**
 * @author Dongping Wang
 * @date 20-6-18
 * email wangdongping@flyaudio.cn
 */
public final class TestUtils {

    private TestUtils() {

    }

    private static long[] mHints = new long[5];

    public static void test(View view, final EqDataDetailDialog eqDataDetailDialog, final EqDataBean dataBean) {
        if (!AppPreferences.USER_DEBUG) {
            return;
        }
        // 点击5次打开eq数据详情
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
                mHints[mHints.length - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - mHints[0] <= TimeUnit.SEC) {
                    if (eqDataDetailDialog != null) {
                        eqDataDetailDialog.show();
                        eqDataDetailDialog.updateMsg(dataBean);
                    }
                }
            }
        });
    }
}
