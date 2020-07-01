package me.xdark.streams;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StreamList<E> extends AbstractList<E> implements AutoCloseable {
    static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private Object[] elementData;
    private int size;
    protected StreamList<Runnable> close;
    protected boolean closed;

    public StreamList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    public StreamList(Collection<? extends E> c) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }
    public StreamList(E[] es) {
        elementData = Arrays.copyOf(es, es.length, Object[].class);
        size = es.length;
    }

    public StreamList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    public void ensureCapacity(int minCapacity) {
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
                ? 0
                : 10;

        if (minCapacity > minExpand) {
            if (minCapacity - elementData.length > 0)
                grow(minCapacity);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public E set(int index, E element) {
        E prev = (E) elementData[index];
        elementData[index] = element;
        return prev;
    }

    @Override
    public E get(int index) {
        return (E) elementData[index];
    }

    @Override
    public E remove(int index) {
        E oldValue = (E) elementData[index];

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index,
                    numMoved);
        elementData[--size] = null;
        return oldValue;
    }

    @Override
    public void clear() {
        if (size != 0) {
            Arrays.fill(elementData, null);
            size = 0;
        }
    }

    public boolean add(E e) {
        ensureCapacityInternal(size + 1);
        elementData[size++] = e;
        return true;
    }

    public boolean addAll(Object[] a) {
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                numMoved);
        int newSize = size - (toIndex - fromIndex);
        Arrays.fill(elementData, newSize, size, null);
        size = newSize;
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        for (int i = 0, j = size; i < j; action.accept((E) elementData[i++])) ;
    }

    @Override
    public Stream<E> stream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<E> parallelStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws Exception {
        closeImpl(true);
    }

    public StreamList<E> onClose(Runnable runnable) {
        StreamList<Runnable> close = this.close;
        if (close == null) {
            close = this.close = StreamListProducer.newList(1);
        }
        close.add(runnable);
        return this;
    }

    void setElementData(Object[] elementData) {
        this.elementData = elementData;
    }

    void setSize(int size) {
        this.size = size;
    }

    @Override
    protected void finalize() throws Throwable {
        closeImpl(false);
        super.finalize();
    }

    protected void checkClosed() {
        if (closed) {
            throw new IllegalStateException("StreamList is closed");
        }
    }

    private void closeImpl(boolean checkClosed) throws Exception {
        if (checkClosed) {
            checkClosed();
        } else if (closed) {
            return;
        }
        closed = true;
        StreamList<Runnable> close = this.close;
        this.close = null;
        if (close != null) {
            Exception ex = null;
            for (int i = 0, j = close.size(); i < j; i++) {
                try {
                    close.get(i).run();
                } catch (Exception e) {
                    if (ex == null) {
                        ex = e;
                    } else {
                        ex.addSuppressed(e);
                    }
                }
            }
            close.close();
            if (ex != null) {
                throw ex;
            }
        }
    }

    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0)
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    private void ensureExplicitCapacity(int minCapacity) {
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }

    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(10, minCapacity);
        }
        return minCapacity;
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<E> {
        int cursor;
        int lastRet = -1;

        Itr() {
        }

        public boolean hasNext() {
            return cursor != size;
        }

        public E next() {
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = StreamList.this.elementData;
            cursor = i + 1;
            return (E) elementData[lastRet = i];
        }

        public void remove() {
            StreamList.this.remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
        }
    }

    @Override
    public Spliterator<E> spliterator() {
        return new ArrayListSpliterator<>(this, 0, -1);
    }

    static final class ArrayListSpliterator<E> implements Spliterator<E> {

        private final StreamList<E> list;
        private int index;
        private int fence;

        ArrayListSpliterator(StreamList<E> list, int origin, int fence) {
            this.list = list;
            this.index = origin;
            this.fence = fence;
        }

        private int getFence() {
            int hi;
            StreamList<E> lst;
            if ((hi = fence) < 0) {
                if ((lst = list) == null)
                    hi = fence = 0;
                else {
                    hi = fence = lst.size;
                }
            }
            return hi;
        }

        public ArrayListSpliterator<E> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null :
                    new ArrayListSpliterator<E>(list, lo, index = mid);
        }

        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null)
                throw new NullPointerException();
            int hi = getFence(), i = index;
            if (i < hi) {
                index = i + 1;
                E e = (E) list.elementData[i];
                action.accept(e);
                return true;
            }
            return false;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi;
            StreamList<E> lst;
            Object[] a;
            if (action == null)
                throw new NullPointerException();
            if ((lst = list) != null && (a = lst.elementData) != null) {
                if ((hi = fence) < 0) {
                    hi = lst.size;
                }
                if ((i = index) >= 0 && (index = hi) <= a.length) {
                    for (; i < hi; ++i) {
                        E e = (E) a[i];
                        action.accept(e);
                    }
                }
            }
        }

        public long estimateSize() {
            return (getFence() - index);
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }
}
