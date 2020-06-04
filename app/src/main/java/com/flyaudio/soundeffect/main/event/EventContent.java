package com.flyaudio.soundeffect.main.event;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-6-4
 * email wangdongping@flyaudio.cn
 */
public final class EventContent {

    private static final List<EventController> events = new ArrayList<>();

    public static boolean addEventController(EventController controller) {
        return events.add(controller);
    }

    public static boolean removeEventController(EventController controller) {
        return events.remove(controller);
    }

    public static void sendDataRestore() {
        for (EventController event : events) {
            event.onRestoreData();
        }
    }
}
