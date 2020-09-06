package pw.prok.imagine.collections;

import pw.prok.imagine.util.Array;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CuttableList<T> implements List<T> {
    private final List<T> mList;
    private int mStartOffset;
    private int mEndOffset;

    public CuttableList(List<T> list) {
        mList = list;
        mStartOffset = 0;
        mEndOffset = 0;
    }

    public CuttableList(List<T> list, int startOffset, int endOffset) {
        assert startOffset >= 0 && endOffset >= 0;
        mList = list;
        mStartOffset = startOffset;
        mEndOffset = endOffset;
    }

    @Override
    public int size() {
        return Math.max(mList.size() - mStartOffset - mEndOffset, 0);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    public int startOffset() {
        return mStartOffset;
    }

    public int startOffset(int startOffset) {
        assert startOffset >= 0;
        int oldStartOffset = mStartOffset;
        mStartOffset = startOffset;
        return oldStartOffset;
    }

    public int endOffset() {
        return mEndOffset;
    }

    public int endOffset(int endOffset) {
        assert endOffset >= 0;
        int oldEndOffset = mEndOffset;
        mEndOffset = endOffset;
        return oldEndOffset;
    }

    @Override
    public T get(int index) {
        if (valid(index)) {
            return mList.get(privateIndex(index));
        }
        throw new IndexOutOfBoundsException("Index out of bounds: " + index + ", size: " + size());
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    private boolean valid(int index) {
        return index >= 0 && index < size();
    }

    private int privateIndex(int index) {
        return index + mStartOffset;
    }

    private int publicIndex(int index) {
        return index - mStartOffset;
    }

    @Override
    public boolean contains(Object o) {
        return valid(indexOf(o));
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        ListIterator<T> iterator = listIterator();
        while (iterator.hasNext())
            if (iterator.next() == o) return index;
            else index++;
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = 0;
        ListIterator<T> iterator = listIterator();
        while (iterator.hasPrevious())
            if (iterator.previous() == o) return size() - index - 1;
            else index++;
        return -1;
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        final int size = size();
        if (size > a.length) {
            a = Array.newArray((Class<T1>) a.getClass().getComponentType(), size);
        }
        int i = 0;
        for (Object o : this) {
            a[i++] = (T1) o;
        }
        return a;
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return new ListIterator<T>() {
            private final ListIterator<T> iterator = mList.listIterator(index);
            private final int size = size();
            private int nextIndex = 0;
            private int previousIndex = size - 1;

            @Override
            public boolean hasNext() {
                return (nextIndex < size - 1) && iterator.hasNext();
            }

            @Override
            public T next() {
                if (!hasNext()) return null;
                nextIndex++;
                return iterator.next();
            }

            @Override
            public boolean hasPrevious() {
                return (previousIndex > 0) && iterator.hasPrevious();
            }

            @Override
            public T previous() {
                if (!hasPrevious()) return null;
                previousIndex--;
                return iterator.previous();
            }

            @Override
            public int nextIndex() {
                return nextIndex;
            }

            @Override
            public int previousIndex() {
                return previousIndex;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(T t) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new CuttableList<>(mList, privateIndex(fromIndex), privateIndex(size() - toIndex));
    }
}
