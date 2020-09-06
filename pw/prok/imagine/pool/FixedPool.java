package pw.prok.imagine.pool;

import java.util.BitSet;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedPool<T> implements Pool<T> {
    private final T[] mPool;
    private final PoolFactory<T> mFactory;
    private final BitSet mStateBits = new BitSet();
    private final int mMaxCount;
    private int mCount = 0;
    private int mLastPos = 0;
    private volatile Lock mLock = new ReentrantLock(true);

    public FixedPool(PoolFactory<T> factory, int count) {
        this(factory, count, count);
    }

    public FixedPool(PoolFactory<T> factory, int initialCount, int maxCount) {
        mFactory = factory;
        mPool = (T[]) new Object[mMaxCount = maxCount];
        mStateBits.clear(0, maxCount);
        grow(initialCount);
    }

    private void grow(int count) {
        if (count > mMaxCount) {
            count = mMaxCount;
        }
        for (int i = mCount; i < count; i++) {
            T object = mFactory.create();
            assert object != null;
            mPool[i] = object;
        }
        mCount = count;
    }

    @Override
    public T obtain() {
        mLock.lock();
        try {
            for (int i = mLastPos; i < mMaxCount; i++) {
                if (!mStateBits.get(i)) {
                    mStateBits.set(mLastPos = i);
                    return mPool[i];
                }
            }
            for (int i = 0; i < mLastPos; i++) {
                if (!mStateBits.get(i)) {
                    mStateBits.set(mLastPos = i);
                    return mPool[i];
                }
            }
            if (mCount < mMaxCount) {
                int index = mCount;
                grow(mCount + 1);
                mStateBits.set(index);
                return mPool[index];
            }
            throw new IllegalStateException("No available objects in pool!");
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void release(T object) {
        assert object != null;
        mLock.lock();
        try {
            for (int i = mLastPos; i < mMaxCount; i++) {
                if (mPool[i] == object) {
                    mStateBits.clear(i);
                    return;
                }
            }
            for (int i = mLastPos; i >= 0; i--) {
                if (mPool[i] == object) {
                    mStateBits.clear(i);
                    return;
                }
            }
            throw new IllegalStateException("Object not assigned to this pool: " + object);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < mMaxCount;
            }

            @Override
            public T next() {
                return mPool[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}