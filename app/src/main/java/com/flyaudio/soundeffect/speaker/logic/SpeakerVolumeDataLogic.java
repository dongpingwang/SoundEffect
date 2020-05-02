package com.flyaudio.soundeffect.speaker.logic;

import android.content.res.XmlResourceParser;
import android.util.SparseArray;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * date 2020/4/27  22:42
 * email wangdongping@flyaudio.cn
 */
final class SpeakerVolumeDataLogic {

    private SpeakerVolumeDataLogic() {

    }

    /**
     * 解析扬声器音量xml,单位1dB
     */
    static SparseArray<SparseArray<ChannelVolumeSpeakerBean>> parserSpeakerVolumeXml() {
        SparseArray<SparseArray<ChannelVolumeSpeakerBean>> channelDelayLinkage = new SparseArray<>();
        XmlResourceParser parser = ResUtils.getResources().getXml(R.xml.speaker_volume_default);
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.next() != XmlResourceParser.START_TAG || !"listen_position".equals(parser.getName())) {
                    continue;
                }
                SparseArray<ChannelVolumeSpeakerBean> listenPosition = new SparseArray<>();
                channelDelayLinkage.put(parser.getAttributeIntValue(null, "position", -1), listenPosition);
                while (parser.next() != XmlResourceParser.END_TAG || !"listen_position".equals(parser.getName())) {
                    if (parser.getEventType() != XmlResourceParser.START_TAG || !"speaker".equals(parser.getName())) {
                        continue;
                    }
                    ChannelVolumeSpeakerBean channelVolumeSpeakerBean = new ChannelVolumeSpeakerBean();
                    listenPosition.put(parser.getAttributeIntValue(null, "type", -1), channelVolumeSpeakerBean);

                    int defaultValue = parser.getAttributeIntValue(null, "defaultVolume", 0);
                    channelVolumeSpeakerBean.setDefaultValue(defaultValue);
                    while (parser.next() != XmlResourceParser.END_TAG || !"speaker".equals(parser.getName())) {
                        if (parser.getEventType() != XmlResourceParser.START_TAG || !"item".equals(parser.getName())) {
                            continue;
                        }
                        channelVolumeSpeakerBean.addLinkageSpeaker(parser.getAttributeIntValue(null, "speaker", -1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelDelayLinkage;
    }

    public static class ChannelVolumeSpeakerBean {
        private boolean enable;
        private int defaultValue;
        private List<Integer> linkageSpeakers;

        private ChannelVolumeSpeakerBean() {
            enable = true;
            linkageSpeakers = new ArrayList<>();
        }

        public boolean isEnable() {
            return enable;
        }

        private void setEnable(boolean enable) {
            this.enable = enable;
        }

        public int getDefaultValue() {
            return defaultValue;
        }

        private void setDefaultValue(int defaultValue) {
            this.defaultValue = defaultValue;
        }

        public List<Integer> getLinkageSpeakers() {
            return linkageSpeakers;
        }

        private void addLinkageSpeaker(int linkageSpeaker) {
            this.linkageSpeakers.add(linkageSpeaker);
        }
    }
}
