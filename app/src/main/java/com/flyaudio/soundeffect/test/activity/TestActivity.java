package com.flyaudio.soundeffect.test.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.TextView;

import com.flyaudio.lib.sp.SPCacheHelper;
import com.flyaudio.lib.toast.Toaster;
import com.flyaudio.lib.utils.AppUtils;
import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;
import com.flyaudio.soundeffect.attenuator.logic.AttenuatorManager;
import com.flyaudio.soundeffect.comm.base.AbstractActivity;
import com.flyaudio.soundeffect.comm.view.CommTitleBar;
import com.flyaudio.soundeffect.delay.logic.DelayManager;
import com.flyaudio.soundeffect.equalizer.bean.EqDataBean;
import com.flyaudio.soundeffect.equalizer.bean.EqMode;
import com.flyaudio.soundeffect.equalizer.logic.EqManager;
import com.flyaudio.soundeffect.filter.bean.EqFilterParam;
import com.flyaudio.soundeffect.filter.logic.EqFilterManager;
import com.flyaudio.soundeffect.main.event.Event;
import com.flyaudio.soundeffect.main.event.EventPoster;
import com.flyaudio.soundeffect.position.logic.Constants;
import com.flyaudio.soundeffect.position.logic.ListenPositionManager;
import com.flyaudio.soundeffect.speaker.logic.SpeakerVolumeManager;
import com.flyaudio.soundeffect.speaker.logic.VolumeManager;
import com.flyaudio.soundeffect.trumpet.logic.BackRowManager;
import com.flyaudio.soundeffect.trumpet.logic.SubwooferManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dongping Wang
 * date 2020/5/10  13:15
 * email wangdongping@flyaudio.cn
 */
public class TestActivity extends AbstractActivity {

    private static final String TAG = "SoundEffect.Test";

    private List<Integer> positionNames = Arrays.asList(
            R.string.position_driver,
            R.string.position_copilot,
            R.string.position_front_row,
            R.string.position_back_row,
            R.string.position_all,
            R.string.position_close
    );

    private CommTitleBar titleBar;
    private EqManager eqManager;
    private ListenPositionManager positionManager;
    private SpeakerVolumeManager speakerVolumeManager;
    private DelayManager delayManager;
    private SubwooferManager subwooferManager;
    private BackRowManager backRowManager;
    private AttenuatorManager attenuatorManager;
    private VolumeManager volumeManager;
    private EqFilterManager eqFilterManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void onInit() {
        initTitleBar();
        getEqModeData();
        getListenPositionData();
        getTrumpetSettingData();
        getBalanceData();
        getVolumeData();
        getEqFilterData();
    }

    private void initTitleBar() {
        titleBar = getView(R.id.eq_title_bar);
        titleBar.setTitleName(ResUtils.getString(R.string.test_app_data));
        titleBar.setActionName(ResUtils.getString(R.string.clear_app_data));
        titleBar.setListener(new CommTitleBar.TitleBarActionListener() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onReset() {
                SPCacheHelper.getInstance().clear();
                EventPoster.getDefaultPoster().post(Event.RESTORE_DATA);
                Toaster.show(ResUtils.getString(R.string.app_data_has_been_cleared));
                onBack();
            }
        });
    }

    private void getEqModeData() {
        eqManager = EqManager.getInstance();
        EqMode eqMode = new EqMode();
        eqMode.setId(eqManager.getCurrentEq());
        List<EqMode> eqList = eqManager.getEqList();
        for (int i = 0; i < eqList.size(); i++) {
            if (eqMode.getId() == eqList.get(i).getId()) {
                eqMode = eqList.get(i);
                break;
            }
        }
        EqDataBean eqModeData = eqManager.getEqModeData(eqMode.getId());
        String eqDataSurmise = ResUtils.getString(R.string.eq_mode_data_surmise, eqMode.getName(), eqMode.getId(),
                Arrays.toString(eqModeData.frequencies), Arrays.toString(eqModeData.gains), Arrays.toString(eqModeData.qValues), eqModeData.current);
        // Logger.d(TAG, eqDataSurmise);
        ((TextView) (getView(R.id.tv_eq_mode))).setText(eqDataSurmise);
    }

    private void getListenPositionData() {
        positionManager = ListenPositionManager.getInstance();
        speakerVolumeManager = SpeakerVolumeManager.getInstance();
        delayManager = DelayManager.getInstance();
        int listenPosition = positionManager.getListenPosition();
        String positionName = ResUtils.getString(positionNames.get(positionManager.listenPosition2Index(listenPosition)));
        int[] speakerVolumes = new int[Constants.SPEAKER_TYPES.length];
        int[] delayValues = new int[Constants.SPEAKER_TYPES.length];
        for (int i = 0; i < Constants.SPEAKER_TYPES.length; i++) {
            speakerVolumes[i] = speakerVolumeManager.getSpeakerVolume(listenPosition, Constants.SPEAKER_TYPES[i]);
            delayValues[i] = delayManager.getDelay(listenPosition, Constants.SPEAKER_TYPES[i]);
        }
        String positionDataSurmise = ResUtils.getString(R.string.listen_position_data_surmise, positionName,
                Arrays.toString(speakerVolumes), Arrays.toString(delayValues));
        // Logger.d(TAG, positionDataSurmise);
        ((TextView) (getView(R.id.tv_listen_position))).setText(positionDataSurmise);
    }

    private void getTrumpetSettingData() {
        subwooferManager = SubwooferManager.getInstance();
        backRowManager = BackRowManager.getInstance();
        boolean subwooferOn = subwooferManager.isSubwooferOn();
        boolean subwooferReverse = subwooferManager.isSubwooferReverse();
        boolean backRowOn = backRowManager.isBackRowOn();

        String trumpetDataSurmise = ResUtils.getString(R.string.trumpet_setting_data_surmise, subwooferOn + "",
                subwooferReverse + "", backRowOn + "");
        // Logger.d(TAG, trumpetDataSurmise);
        ((TextView) (getView(R.id.tv_trumpet))).setText(trumpetDataSurmise);
    }

    private void getBalanceData() {
        attenuatorManager = AttenuatorManager.getInstance();
        int[] balanceValues = new int[Constants.SPEAKER_TYPES.length - 1];
        for (int i = 0; i < Constants.SPEAKER_TYPES.length - 1; i++) {
            balanceValues[i] = attenuatorManager.getBalance(Constants.SPEAKER_TYPES[i]);
            boolean backRowOff = (Constants.SPEAKER_TYPES[i] == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_LEFT
                    || Constants.SPEAKER_TYPES[i] == Constants.ListenPositionSpeakerType.LISTEN_POSITION_SPEAKER_BACK_RIGHT)
                    && !backRowManager.isBackRowOn();
            if (backRowOff) {
                balanceValues[i] = AttenuatorManager.BALANCE_MIN;
            }
        }
        String balanceDataSurmise = ResUtils.getString(R.string.attenuator_data_surmise, Arrays.toString(balanceValues));
        // Logger.d(TAG, balanceDataSurmise);
        ((TextView) (getView(R.id.tv_balance))).setText(balanceDataSurmise);
    }

    private void getVolumeData() {
        volumeManager = VolumeManager.getInstance();
        int[] volumeValues = new int[Constants.SPEAKER_TYPES.length];
        for (int i = 0; i < Constants.SPEAKER_TYPES.length; i++) {
            volumeValues[i] = volumeManager.getVolume(Constants.SPEAKER_TYPES[i]);
        }
        String volumeDataSurmise = ResUtils.getString(R.string.volume_data_surmise, Arrays.toString(volumeValues));
        // Logger.d(TAG, balanceDataSurmise);
        ((TextView) (getView(R.id.tv_volume))).setText(volumeDataSurmise);
    }

    private void getEqFilterData() {
        eqFilterManager = EqFilterManager.getInstance();
        List<EqFilterParam> eqFilterParams = new ArrayList<>(EqFilterManager.FILTER_CHANNELS.length);
        for (int channel : EqFilterManager.FILTER_CHANNELS) {
            EqFilterParam param = new EqFilterParam(eqFilterManager.getFilterFreq(channel),
                    eqFilterManager.getFilterSlope(channel), eqFilterManager.isFilterEnable(channel));
            param.channel = channel;
            eqFilterParams.add(param);
        }

        String eqFilterDataSurmise = ResUtils.getString(R.string.eq_filter_data_surmise, eqFilterParams.get(0).toString(),
                eqFilterParams.get(1).toString(), eqFilterParams.get(2).toString());
        // Logger.d(TAG, eqFilterDataSurmise);
        ((TextView) (getView(R.id.tv_eq_filter))).setText(eqFilterDataSurmise);
    }

}
