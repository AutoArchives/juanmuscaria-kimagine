package pw.prok.imagine.pool;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DynamicPool<T> implements Pool<T> {
    private final PoolFactory<T> mFactory;
    private final Queue<T> mLocked, mUnlocked;
    private final Queue<T> mObjects = new ConcurrentLinkedQueue<>();
    private final Lock mLock = new ReentrantLock(true);

    public DynamicPool(PoolFactory<T> factory) {
        mFactory = factory;
        mLocked = new LinkedList<>();
        mUnlocked = new LinkedList<>();
    }

    @Override
    public T obtain() {
        mLock.lock();
        try {
            T object = mUnlocked.poll();
            if (object == null) {
                object = mFactory.create();
                mObjects.add(object);
            }
            mLocked.add(object);
            return object;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void release(T object) {
        mLock.lock();
        try {
            if (!mLocked.remove(object)) {
                throw new IllegalStateException("Object not assigned to this pool!");
            }
            mUnlocked.add(object);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        final List<T> objects;
        mLock.lock();
        try {
            objects = new ArrayList<>(mObjects);
        } finally {
            mLock.unlock();
        }
        return new Iterator<T>() {
            private final Iterator<T> iterator = objects.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
