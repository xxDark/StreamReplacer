package me.xdark.streams;

import io.netty.util.Recycler;

import java.util.Collection;

public final class RecyclableStreamListProducer extends StreamListProducer {
    private static final Recycler<RecyclableStreamList<?>> RECYCLER = new Recycler<RecyclableStreamList<?>>() {
        @Override
        protected RecyclableStreamList<?> newObject(Handle<RecyclableStreamList<?>> handle) {
            return new RecyclableStreamList<>(handle);
        }
    };

    @Override
    protected <E> StreamList<E> _newList(int initialCapacity) {
        StreamList<E> list = (StreamList<E>) RECYCLER.get();
        list.closed = false;
        list.ensureCapacity(initialCapacity);
        return list;
    }

    @Override
    protected <E> StreamList<E> _newList(Collection<E> collection) {
        StreamList<E> list = _newList(collection.size());
        list.addAll(collection);
        return list;
    }
}