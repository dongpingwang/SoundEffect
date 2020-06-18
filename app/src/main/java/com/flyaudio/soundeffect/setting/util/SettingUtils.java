package com.flyaudio.soundeffect.setting.util;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;

import com.flyaudio.comm.utils.AppUtils;
import com.flyaudio.lib.constant.TimeUnit;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.backup.activity.ExportEffectActivity;
import com.flyaudio.soundeffect.backup.activity.ImportEffectActivity;
import com.flyaudio.soundeffect.attenuator.activity.AttenuatorActivity;
import com.flyaudio.soundeffect.config.AppPreferences;
import com.flyaudio.soundeffect.filter.activity.EqFilterActivity;
import com.flyaudio.soundeffect.test.activity.TestActivity;
import com.flyaudio.soundeffect.trumpet.activity.TrumpetSettingActivity;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-4-27
 * email wangdongping@flyaudio.cn
 */

public final class SettingUtils {


    private SettingUtils() {

    }

    public static List<String> getNames() {
        return Arrays.asList(ResUtils.getStringArray(R.array.setting_items));
    }

    public static void startActivity(Context context, int position) {
        Class clazz;
        if (position == 0) {
            clazz = TrumpetSettingActivity.class;
        } else if (position == 1) {
            clazz = AttenuatorActivity.class;
        } else if (position == 2) {
            clazz = EqFilterActivity.class;
        } else if (position == 3) {
            clazz = ImportEffectActivity.class;
        } else {
            clazz = ExportEffectActivity.class;
        }
        context.startActivity(new Intent(context, clazz));
    }


    private static long[] mHints = new long[5];

    public static void test(final Context context, View view) {
        if (!AppPreferences.USER_DEBUG) {
            return;
        }
        // 点击5次打开test模块
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
                mHints[mHints.length - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - mHints[0] <= TimeUnit.SEC) {
                    context.startActivity(new Intent(context, TestActivity.class));
                }
            }
        });
    }
}
