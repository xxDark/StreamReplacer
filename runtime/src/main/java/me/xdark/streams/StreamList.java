package me.xdark.streams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

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
