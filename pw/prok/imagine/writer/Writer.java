package pw.prok.imagine.writer;

import com.google.common.collect.Lists;
import pw.prok.imagine.api.ICopyable;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Writer {
    public static <T extends ICopyable<T>> T copy(T t) {
        return t != null ? t.copy() : null;
    }

    public static <T extends ICopyable<T>> List<T> copyList(List<T> list) {
        if (list == null) {
            return null;
        }
        List<T> result = Lists.newLinkedList(list);
        copyContent(result);
        return result;
    }

    public static <T extends ICopyable<T>> void copyContent(List<T> list) {
        ListIterator<T> iterator = list.listIterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            iterator.set(copy(t));
        }
    }

    public static <K extends ICopyable<K>, V extends ICopyable<V>> Map<K, V> copyMap(Map<K, V> map) {
        return copyMap(map, true, true);
    }

    public static <K extends ICopyable<K>, V extends ICopyable<V>> Map<K, V> copyMap(Map<K, V> map, boolean copyKey, boolean copyValue) {
        if (map == null) {
            return null;
        }
        Map<K, V> newMap = new HashMap<K, V>(map);
        copyContent(newMap, copyKey, copyValue);
        return newMap;
    }

    public static <K extends ICopyable<K>, V extends ICopyable<V>> void copyContent(Map<K, V> map, boolean copyKey, boolean copyValue) {
        if (!copyKey && !copyValue) {
            return;
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            final K key = entry.getKey();
            final V value = entry.getValue();
            map.remove(key);
            map.put(copyKey ? copy(key) : key, copyValue ? copy(value) : value);
        }
    }
}
