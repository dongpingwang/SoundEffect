package com.flyaudio.soundeffect.main.event;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongping Wang
 * @date 20-6-4
 * email wangdongping@flyaudio.cn
 */
public final class EventPoster {

    private EventPoster() {
        // singleton
    }

    private static class InstanceHolder {
        private static EventPoster poster = new EventPoster();
    }

    public static EventPoster getDefaultPoster() {
        return InstanceHolder.poster;
    }

    private static List<Event> events = new ArrayList<>();

    public boolean addEventController(Event controller) {
        return events.add(controller);
    }

    public boolean removeEventController(Event controller) {
        return events.remove(controller);
    }

    public void post(String tag) {
        for (Event event : events) {
            event.onEvent(tag);
        }
    }
}
