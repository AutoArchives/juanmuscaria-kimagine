package pw.prok.imagine.pool;

public interface Pool<T> extends Iterable<T> {
    T obtain();

    void release(T object);
}
