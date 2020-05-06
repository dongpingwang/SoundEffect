package com.flyaudio.soundeffect.attenuator.logic;

/**
 * @author Dongping Wang
 * @date 20-5-6
 * email wangdongping@flyaudio.cn
 */
public final class AttenuatorLogic {

    private static final String KEY_TOUCH_VALUE = "touch_value_%d";

    private AttenuatorLogic() {

    }

    private static class InstanceHolder {
        private static AttenuatorLogic instance = new AttenuatorLogic();
    }

}
