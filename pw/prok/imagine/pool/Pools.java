package pw.prok.imagine.pool;

public class Pools {
    public static <T> Pool<T> create(int initialCount, int maxCount, PoolFactory<T> factory) {
        return new FixedPool<>(factory, initialCount, maxCount);
    }

    public static <T> Pool<T> create(int initialCount, int maxCount, Class<T> clazz, Object... args) {
        return new FixedPool<>(new ReflectionFactory<>(clazz, args), initialCount, maxCount);
    }

    public static <T> Pool<T> create(int count, PoolFactory<T> factory) {
        return new FixedPool<>(factory, count);
    }

    public static <T> Pool<T> create(int count, Class<T> clazz, Object... args) {
        return new FixedPool<>(new ReflectionFactory<>(clazz, args), count);
    }

    public static <T> Pool<T> create(PoolFactory<T> factory) {
        return new DynamicPool<>(factory);
    }

    public static <T> Pool<T> create(Class<T> clazz, Object... args) {
        return new DynamicPool<>(new ReflectionFactory<>(clazz, args));
    }
}
