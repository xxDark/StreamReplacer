package me.xdark.streams;

import java.util.Collection;

public final class SingleListProducer extends StreamListProducer {
    private final StreamList<?> list = new StreamList<>();

    @Override
    protected <E> StreamList<E> _newList(int initialCapacity) {
        StreamList<E> list = (StreamList<E>) this.list;
        list.closed = false;
        list.ensureCapacity(initialCapacity);
        return list;
    }

    @Override
    protected <E> StreamList<E> _newList(Collection<E> collection) {
        StreamList<E> list = (StreamList<E>) this.list;
        list.closed = false;
        list.addAll(collection);
        return list;
    }

    @Override
    protected <E> StreamList<E> _newList(E[] es) {
        StreamList<E> list = (StreamList<E>) this.list;
        list.closed = false;
        list.addAll(es);
        return list;
    }
}
