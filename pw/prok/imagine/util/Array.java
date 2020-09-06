package pw.prok.imagine.util;

import pw.prok.imagine.asm.ImagineDesc;

import java.util.*;

public class Array {
    public static <T> T[] newArray(Class<T> clazz, int length) {
        return (T[]) java.lang.reflect.Array.newInstance(clazz, length);
    }

    public static <T> T[] newArrayUnchecked(Class<?> clazz, int length) {
        return newArray((Class<T>) clazz, length);
    }

    public static <T> T[] appendToArray(T[] array, T... objects) {
        if (array == null || array.length == 0) {
            return objects;
        }
        if (objects == null || objects.length == 0) {
            return array;
        }
        final int i1 = array.length, i2 = objects.length;
        T[] newArray = newArrayUnchecked(array.getClass().getComponentType(), i1 + i2);
        System.arraycopy(array, 0, newArray, 0, i1);
        System.arraycopy(objects, 0, newArray, i1, i2);
        return newArray;
    }

    public static <T> T[] mergeArrays(T[]... arrays) {
        if (arrays == null) return null;
        int length = 0;
        Class<T> componentClass = null;
        for (T[] array : arrays) {
            if (array != null) {
                if (componentClass == null) {
                    componentClass = (Class<T>) array.getClass().getComponentType();
                }
                length += array.length;
            }
        }
        if (componentClass == null) return null;
        T[] newArray = newArray(componentClass, length);
        int index = 0;
        for (T[] array : arrays) {
            if (array != null) {
                System.arraycopy(array, 0, newArray, index, array.length);
                index += array.length;
            }
        }
        return newArray;
    }

    public static <T> T[] asArray(Collection<T> collection) {
        return asArray(collection, null);
    }

    public static <T> T[] asArray(Collection<T> collection, Class<T> clazz) {
        final int size = collection.size();
        Class<? extends T> component = clazz;
        if (clazz == null) {
            if (size == 0) {
                return null;
            }
            T t = collection.iterator().next();
            component = (Class<? extends T>) t.getClass();
        }
        T[] array = newArray(component, size);
        collection.toArray(array);
        return array;
    }

    public static <T> T[] keys(Map<T, ?> map) {
        return keys(map, null);
    }

    public static <T> T[] keys(Map<T, ?> map, Class<T> clazz) {
        return asArray(map.keySet(), clazz);
    }

    public static <T> T[] values(Map<?, T> map) {
        return values(map, null);
    }

    public static <T> T[] values(Map<?, T> map, Class<T> clazz) {
        return asArray(map.values(), clazz);
    }

    public static <T> List<T> asList(T... values) {
        List<T> list = new ArrayList<>(values.length);
        Collections.addAll(list, values);
        return list;
    }
}
