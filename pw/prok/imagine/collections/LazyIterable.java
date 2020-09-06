package pw.prok.imagine.collections;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LazyIterable<T> implements Iterable<T> {
    public interface LazyAction<T> {
        T acquire();
    }

    private final LazyAction<T> mAction;
    private final boolean mOneTime;
    private Iterator<T> mIterator;
    private List<T> mCacheList;

    public LazyIterable(LazyAction<T> action) {
        this(action, true);
    }

    public LazyIterable(LazyAction<T> action, boolean oneTime) {
        mAction = action;
        mOneTime = oneTime;
    }

    @Override
    public Iterator<T> iterator() {
        if (mOneTime) {
            if (mIterator == null) mIterator = new OneTimeIterator<T>(mAction);
            return mIterator;
        }
        if (mIterator == null) return mIterator = new CacheIterator<T>(mAction, mCacheList = new LinkedList<T>());
        return mCacheList.iterator();
    }

    private static class OneTimeIterator<T> implements Iterator<T> {
        private final LazyAction<T> mAction;
        private T mNext;
        private boolean mEnd = false;

        public OneTimeIterator(LazyAction<T> action) {
            mAction = action;
        }

        @Override
        public boolean hasNext() {
            if (mEnd) return false;
            if (mNext != null) return true;
            mNext = mAction.acquire();
            if (mNext == null) {
                mEnd = true;
                return false;
            }
            return true;
        }

        @Override
        public T next() {
            T object = hasNext() ? mNext : null;
            mNext = null;
            return object;
        }

        @Override
        public void remove() {

        }
    }

    private static class CacheIterator<T> extends OneTimeIterator<T> {
        private final List<T> mCache;
        private T mPrev;

        public CacheIterator(LazyAction<T> action, List<T> cache) {
            super(action);
            mCache = cache;
        }

        @Override
        public T next() {
            T object = mPrev = super.next();
            if (object != null) mCache.add(object);
            return object;
        }

        @Override
        public void remove() {
            if (mPrev == null) throw new IllegalStateException();
            if (mCache.remove(mCache.size() - 1) != mPrev) throw new IllegalStateException();
            mPrev = null;
        }
    }
}
