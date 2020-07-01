package me.xdark.streams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class StreamList<E> extends ArrayList<E> implements AutoCloseable {
    private Runnable runnable;
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
    public void close() {
        checkClosed();
        closed = true;
        runnable.run();
    }

    public StreamList<E> onClose(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    protected void checkClosed() {
        if (closed) {
            throw new IllegalStateException("StreamList is closed");
        }
    }
}
