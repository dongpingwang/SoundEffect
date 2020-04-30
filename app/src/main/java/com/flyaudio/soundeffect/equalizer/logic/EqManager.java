package com.flyaudio.soundeffect.equalizer.logic;

import android.text.TextUtils;

import com.flyaudio.lib.json.handler.GsonHandler;
import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.soundeffect.comm.config.EffectConfigUtils;
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
public final class EqManager extends EqLogic {

    /**
     * 记录eq列表：将Java bean集合转化为json String进行存储
     */
    private static final String KEY_EQ_LIST = "eq_list";


    /**
     * 记录eq列表进行删除新建后区分item的标识，模拟数据库删除后索引递增，从0开始
     */
    private static final String KEY_EQ_INDEX_MAX = "eq_max_index";

    /**
     * 记录当前调节的eq模式，在列表中的位置
     */
    private static final String KEY_EQ_CURRENT = "eq_current_index";


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


    /**
     * 从sp中获取eq列表数据
     */
    private List<EqMode> getEqListFromSp() {
        List<EqMode> list;
        String listStr = SPCacheHelper.getInstance().getString(KEY_EQ_LIST);
        if (!TextUtils.isEmpty(listStr)) {
            GsonHandler handler = new GsonHandler(new Gson());
            list = handler.gson().fromJson(listStr, new TypeToken<List<EqMode>>() {
            }.getType());
        } else {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 保存eq列表数据
     */
    public void saveEqList(List<EqMode> list) {
        GsonHandler handler = new GsonHandler(new Gson());
        String data = handler.toJson(list);
        SPCacheHelper.getInstance().put(KEY_EQ_LIST, data);
    }

    /**
     * 从xml中获取预置的eq列表数据
     */
    private List<EqMode> getPresetEqList() {
        List<EqMode> list = new ArrayList<>();
        String[] names = EffectConfigUtils.getEqNames();
        for (int i = 0; i < names.length; i++) {
            EqMode mode = new EqMode(i, names[i]);
            list.add(mode);
        }
        saveMaxEqId(names.length - 1);
        return list;
    }

    /**
     * 当前调节的eq模式的id，默认为0(即第一个eq模式)
     */
    public int getCurrentEq() {
        return SPCacheHelper.getInstance().getInt(KEY_EQ_CURRENT);
    }


    /**
     * 保存当前调节的eq模式的id
     */
    public void saveCurrentEq(int id) {
        SPCacheHelper.getInstance().put(KEY_EQ_CURRENT, id);
    }


    /**
     * 添加的自定义eq模式数量
     */
    public int getCustomEqCount() {
        return getEqList().size() - EQ_PRESET_COUNT;
    }

    /**
     * 获取最大的id
     */
    public int getMaxEqId() {
        return SPCacheHelper.getInstance().getInt(KEY_EQ_INDEX_MAX);
    }

    /**
     * 保存最大的id
     */
    public void saveMaxEqId(int id) {
        SPCacheHelper.getInstance().put(KEY_EQ_INDEX_MAX, id);
    }
}
