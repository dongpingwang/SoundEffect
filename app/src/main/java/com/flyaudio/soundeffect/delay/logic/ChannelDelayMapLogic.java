package com.flyaudio.soundeffect.delay.logic;

import android.content.res.XmlResourceParser;
import android.util.SparseArray;

import com.flyaudio.lib.utils.ResUtils;
import com.flyaudio.soundeffect.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * date 2020/5/5  20:29
 * email wangdongping@flyaudio.cn
 */
final class ChannelDelayMapLogic {

    private volatile static SparseArray<SparseArray<ChannelDelaySpeakerBean>> channelDelayMap;

     static SparseArray<SparseArray<ChannelDelaySpeakerBean>> getChannelDelayMap() {
        if (channelDelayMap == null) {
            synchronized (ChannelDelayMapLogic.class) {
                if (channelDelayMap == null) {
                    channelDelayMap = parserChannelDelayXml();
                }
            }
        }
        return channelDelayMap;
    }

    /**
     * 解析通道延时的xml，以时间为标准，单位为0.1ms
     */
    private static SparseArray<SparseArray<ChannelDelaySpeakerBean>> parserChannelDelayXml() {
        SparseArray<SparseArray<ChannelDelaySpeakerBean>> channelDelayLinkage = new SparseArray<>();
        XmlResourceParser parser = ResUtils.getResources().getXml(R.xml.channel_delay_linkage);
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.next() != XmlResourceParser.START_TAG || !"listen_position".equals(parser.getName())) {
                    continue;
                }
                SparseArray<ChannelDelaySpeakerBean> listenPosition = new SparseArray<>();
                channelDelayLinkage.put(parser.getAttributeIntValue(null, "position", -1), listenPosition);

                while (parser.next() != XmlResourceParser.END_TAG || !"listen_position".equals(parser.getName())) {
                    if (parser.getEventType() != XmlResourceParser.START_TAG || !"speaker".equals(parser.getName())) {
                        continue;
                    }
                    ChannelDelaySpeakerBean channelDelaySpeakerBean = new ChannelDelaySpeakerBean();
                    listenPosition.put(parser.getAttributeIntValue(null, "type", -1), channelDelaySpeakerBean);
                    // xml 中配置的单位为 1ms，转换为 0.1ms
                    int defaultValue = (int) (parser.getAttributeFloatValue(null, "defaultValue", 0) * 10);
                    channelDelaySpeakerBean.setDefaultValue(defaultValue);
                    channelDelaySpeakerBean.setEnable(parser.getAttributeBooleanValue(null, "adjustAble", true));
                    while (parser.next() != XmlResourceParser.END_TAG || !"speaker".equals(parser.getName())) {
                        if (parser.getEventType() != XmlResourceParser.START_TAG || !"item".equals(parser.getName())) {
                            continue;
                        }
                        channelDelaySpeakerBean.addLinkageSpeaker(parser.getAttributeIntValue(null, "speaker", -1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelDelayLinkage;
    }

    public static class ChannelDelaySpeakerBean {
        private boolean enable;
        private int defaultValue;
        private List<Integer> linkageSpeakers;

        private ChannelDelaySpeakerBean() {
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
