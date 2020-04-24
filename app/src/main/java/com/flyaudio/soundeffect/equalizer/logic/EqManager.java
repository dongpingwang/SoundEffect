package com.flyaudio.soundeffect.equalizer.logic;

import com.flyaudio.lib.json.handler.GsonHandler;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.lib.utils.StringUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.equalizer.bean.EqMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-4-24
 * email wangdongping@flyaudio.cn
 */
public final class EqManager {

    // eq列表数据
    private static final String KEY_EQ_LIST = "eq_list";

    private EqManager() {

    }

    private static class InstanceHolder {
        private static EqManager instance = new EqManager();
    }

    public static EqManager getInstance() {
        return InstanceHolder.instance;
    }


    public List<EqMode> getEqList() {
        List<EqMode> result;
        if (SPCacheHelper.getInstance().contains(KEY_EQ_LIST)) {
            result = getEqListFromSp();
        } else {
            result = getPresetEqList();
            saveEqList(result);
        }
        return result;
    }


    // 从sp中获取eq列表数据
    private List<EqMode> getEqListFromSp() {
        List<EqMode> list;
        String listStr = SPCacheHelper.getInstance().getString(KEY_EQ_LIST);
        if (!StringUtils.isEmpty(listStr)) {
            GsonHandler handler = new GsonHandler(new Gson());
            list = handler.gson().fromJson(listStr, new TypeToken<List<EqMode>>() {
            }.getType());
        } else {
            list = new ArrayList<>();
        }
        return list;
    }

    // 保存eq列表数据
    public void saveEqList(List<EqMode> list) {
        GsonHandler handler = new GsonHandler(new Gson());
        String data = handler.toJson(list);
        SPCacheHelper.getInstance().put(KEY_EQ_LIST, data);
    }

    // 从xml中获取预置的eq列表数据
    private List<EqMode> getPresetEqList() {
        List<EqMode> list = new ArrayList<>();
        String[] names = ResUtils.getStringArray(R.array.eq_names);
        for (int i = 0; i < names.length; i++) {
            EqMode mode = new EqMode(i, names[i]);
            list.add(mode);
        }
        return list;
    }


}
