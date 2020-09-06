
package pw.prok.imagine.fan;


import pw.prok.imagine.inject.Creator;

import java.util.HashMap;
import java.util.Map;

public class FanLoader {
    private static class FanHolder<T> {
        private FanData fanData;
        private T fan;
        private State state;
    }

    public enum State {
        Unloaded, Found, Loaded, PreInitialized, Initialized, PostInitialized, Error;
    }

    private static Map<Class<?>, FanHolder<?>> sFanMap = new HashMap<>();
    private static State sState = State.Unloaded;

    public static <T> void loadFan(Class<T> fanClass) {
        if (sState != State.Unloaded) {
            throw new IllegalStateException("Attempt to load fan after initializing!");
        }
        Fan fanData = fanClass.getAnnotation(Fan.class);
        if (fanData == null) {
            throw new IllegalArgumentException("Illegal fan! No fan data found");
        }
        FanHolder<T> holder = new FanHolder<>();
        holder.fan = Creator.creator(fanClass).build();
        holder.fanData = new FanData(fanData);
        holder.state = State.Unloaded;
        sFanMap.put(fanClass, holder);
    }

    public static void migrate(State state) {
        switch (state) {
            case Unloaded:
                throw new IllegalArgumentException("Could not migrate to unloaded state!");
            case Found:
                break;
            case Loaded:
                break;
        }
        sState = state;
        for (FanHolder<?> holder : sFanMap.values()) {
            holder.state = state;
        }
    }

    public static <T> T getFan(Class<T> clazz) {
        FanHolder<T> holder = (FanHolder<T>) sFanMap.get(clazz);
        return holder != null ? (T) holder.fan : null;
    }
}
