package pw.prok.imagine.collections;

import pw.prok.imagine.util.Array;

import java.util.*;

public abstract class AbstractIndirectList<T> implements List<T>, Indirect {
    private T[] mObjects;
    private int mActualEnd, mInitialSize;
    private int mLastAvailable;

    public AbstractIndirectList() {
        this(100);
    }

    public AbstractIndirectList(int initialSize) {
        mInitialSize = initialSize;
        clear();
    }

    @Override
    public int size() {
        return mActualEnd;
    }

    @Override
    public boolean isEmpty() {
        return mActualEnd == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        return mObjects;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (a.length < mActualEnd) {
            a = Array.newArray((Class<T1>) a.getClass().getComponentType(), mActualEnd);
        }
        System.arraycopy(mObjects, 0, a, 0, mActualEnd);
        return a;
    }

    @Override
    public boolean add(T t) {
        if (t == null)
            return false;
        for (int i = mLastAvailable; i < mActualEnd; i++) {
            if (mObjects[i] == null) {
                set(mLastAvailable = i, t);
                checkEnd(i + 1);
                return true;
            }
        }
        final int index = mActualEnd;
        if (index < mObjects.length) {
            set(mLastAvailable = index, t);
            checkEnd(index + 1);
            return true;
        }
        for (int i = 0; i < mLastAvailable && i < mActualEnd; i++) {
            if (mObjects[i] == null) {
                set(i, t);
                checkEnd(i + 1);
                return true;
            }
        }
        grow(t);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        final int index = indexOf(o);
        return index >= 0 && remove(index) == o;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c)
            if (!contains(o))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c == null || c.size() == 0) return false;
        grow(mObjects.length + c.size());
        boolean changed = false;
        for (T o : c)
            changed |= add(o);
        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null || c.size() == 0) return false;
        boolean changed = false;
        for (Object o : c)
            changed |= remove(o);
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        mObjects = (T[]) new Object[mInitialSize];
        mActualEnd = 0;
        mLastAvailable = 0;
    }

    @Override
    public T get(int index) {
        return mObjects[index];
    }

    @Override
    public T set(int index, T element) {
        T old = mObjects[index];
        mObjects[index] = element;
        return old;
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        return set(index, null);
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < mActualEnd; i++)
            if (mObjects[i] == o)
                return i;
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = mActualEnd - 1; i >= 0; i--)
            if (mObjects[i] == o)
                return i;
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new IndirectIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return new IndirectIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        List<T> subList = new ArrayList<T>(toIndex - fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            T t = mObjects[i];
            if (t != null) subList.add(t);
        }
        return subList;
    }

    @Override
    public void compat(boolean trim) {
        int last = -1;
        int count = 0;
        for (int i = 0; i < mActualEnd; i++) {
            T item = mObjects[i];
            if (item == null) {
                if (last == -1)
                    last = i;
            } else {
                count++;
                if (last != -1) {
                    mObjects[last++] = mObjects[i];
                    mObjects[i] = null;
                }
            }
        }
        mActualEnd = count;
        if (trim) {
            trim();
        }
    }

    @Override
    public void trim() {
        if (mActualEnd <= 0) return;
        T[] newObjects = (T[]) new Object[mActualEnd];
        System.arraycopy(mObjects, 0, newObjects, 0, mActualEnd);
        mObjects = newObjects;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append('[');
        for (int i = 0; i < mObjects.length; i++) {
            if (i != 0)
                builder.append(',');
            builder.append(mObjects[i]);
        }
        builder.append(']');
        return builder.toString();
    }

    private void grow(T element) {
        final int currentLength = mObjects.length;
        final int newSize = Math.max(currentLength * 3 / 2, 10);
        final T[] newData = (T[]) new Object[newSize];
        System.arraycopy(mObjects, 0, newData, 0, currentLength);
        newData[currentLength] = element;
        mObjects = newData;
        checkEnd(currentLength + 1);
    }

    private void grow(int newSize) {
        final int currentLength = mObjects.length;
        final T[] newData = (T[]) new Object[newSize];
        System.arraycopy(mObjects, 0, newData, 0, currentLength);
        mObjects = newData;
        mLastAvailable = currentLength;
    }

    private void checkEnd(int i) {
        if (i > mActualEnd) {
            mActualEnd = i;
        }
    }

    private final class IndirectIterator implements ListIterator<T> {
        int mIndex;
        boolean mIndexSetted;

        public IndirectIterator() {
            mIndex = -1;
            mIndexSetted = false;
        }

        public IndirectIterator(int index) {
            mIndex = index;
            mIndexSetted = true;
        }

        @Override
        public boolean hasNext() {
            ensureIndex(true);
            return mIndex < mActualEnd;
        }

        private void ensureIndex(boolean forward) {
            if (!mIndexSetted) {
                mIndex = forward ? 0 : mActualEnd - 1;
                mIndexSetted = true;
            }
        }

        @Override
        public T next() {
            ensureIndex(true);
            return mObjects[mIndex++];
        }

        @Override
        public boolean hasPrevious() {
            ensureIndex(false);
            return mIndex >= 0;
        }

        @Override
        public T previous() {
            ensureIndex(false);
            return mObjects[mIndex--];
        }

        @Override
        public int nextIndex() {
            if (!mIndexSetted) return 0;
            return mIndex + 1;
        }

        @Override
        public int previousIndex() {
            if (!mIndexSetted) return mActualEnd - 1;
            return mIndex - 1;
        }

        @Override
        public void remove() {
            AbstractIndirectList.this.remove(mIndex);
        }

        @Override
        public void set(T t) {
            AbstractIndirectList.this.set(mIndex, t);
        }

        @Override
        public void add(T t) {
            throw new UnsupportedOperationException();
        }
    }
}
