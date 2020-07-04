package me.xdark.streams;

import java.util.Collection;

public final class SingleListProducer extends StreamListProducer {
    private final StreamList<?> list = new StreamList<>();
    private final StreamListBuilder<?> builder = new StreamListBuilder<>();

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

    @Override
    protected <E> StreamListBuilder<E> _newBuilder() {
        StreamListBuilder<E> builder = (StreamListBuilder<E>) this.builder;
        builder.list = newList(0);
        return builder;
    }
}
