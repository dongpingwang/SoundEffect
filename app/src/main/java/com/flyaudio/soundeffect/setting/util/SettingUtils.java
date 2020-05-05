package com.flyaudio.soundeffect.setting.util;

import android.content.Context;
import android.content.Intent;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.backup.activity.ExportEffectActivity;
import com.flyaudio.soundeffect.backup.activity.ImportEffectActivity;
import com.flyaudio.soundeffect.equilibrium.AttenuationEquilibriumActivity;
import com.flyaudio.soundeffect.filter.EqFilterActivity;
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
            clazz = AttenuationEquilibriumActivity.class;
        } else if (position == 2) {
            clazz = EqFilterActivity.class;
        } else if (position == 3) {
            clazz = ImportEffectActivity.class;
        } else {
            clazz = ExportEffectActivity.class;
        }
        context.startActivity(new Intent(context, clazz));
    }
}
