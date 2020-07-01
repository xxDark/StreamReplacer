package me.xdark.streams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class StreamList<E> extends ArrayList<E> implements AutoCloseable {
    protected StreamList<Runnable> close;
    protected boolean closed;

    public StreamList(int initialCapacity) {
        super(initialCapacity);
    }

    public StreamList(Collection<? extends E> c) {
        super(c);
    }

    public StreamList() { }

    @Override
    public boolean add(E e) {
        checkClosed();
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        checkClosed();
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        checkClosed();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkClosed();
        return super.addAll(index, c);
    }

    @Override
    public E remove(int index) {
        checkClosed();
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        checkClosed();
        return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkClosed();
        return super.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        checkClosed();
        return super.removeIf(filter);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        checkClosed();
        super.replaceAll(operator);
    }

    @Override
    public void clear() {
        checkClosed();
        if (!isEmpty()) {
            super.clear();
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        checkClosed();
        super.forEach(action);
    }

    @Override
    public boolean contains(Object o) {
        checkClosed();
        return super.contains(o);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        checkClosed();
        return super.retainAll(c);
    }

    @Override
    public boolean isEmpty() {
        checkClosed();
        return super.isEmpty();
    }

    @Override
    public int indexOf(Object o) {
        checkClosed();
        return super.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        checkClosed();
        return super.lastIndexOf(o);
    }

    @Override
    public int size() {
        checkClosed();
        return super.size();
    }

    @Override
    public Iterator<E> iterator() {
        checkClosed();
        return super.iterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        checkClosed();
        return super.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        checkClosed();
        return super.listIterator(index);
    }

    @Override
    public Spliterator<E> spliterator() {
        checkClosed();
        return super.spliterator();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        checkClosed();
        return super.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        checkClosed();
        return super.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        checkClosed();
        return super.toArray(a);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        checkClosed();
        super.sort(c);
    }

    @Override
    public void trimToSize() {
        checkClosed();
        super.trimToSize();
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
        }  else if (closed) {
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
}
