package com.flyaudio.soundeffect.comm.util;

import android.graphics.Color;

/**
 * @author Dongping Wang
 * date 2020/4/27  23:36
 * email wangdongping@flyaudio.cn
 */
public final class ColorUtils {

    private ColorUtils() {

    }

    public static int getGradientColor(float fraction, int startColor, int endColor) {
        int redCurrent, blueCurrent, greenCurrent, alphaCurrent;

        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int alphaStart = Color.alpha(startColor);

        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int alphaEnd = Color.alpha(endColor);

        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaDifference = alphaEnd - alphaStart;

        redCurrent = (int) (redStart + fraction * redDifference);
        blueCurrent = (int) (blueStart + fraction * blueDifference);
        greenCurrent = (int) (greenStart + fraction * greenDifference);
        alphaCurrent = (int) (alphaStart + fraction * alphaDifference);

        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }
}
