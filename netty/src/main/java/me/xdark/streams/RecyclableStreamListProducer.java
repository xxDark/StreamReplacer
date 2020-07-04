package me.xdark.streams;

import io.netty.util.Recycler;

import java.util.Collection;

public final class RecyclableStreamListProducer extends StreamListProducer {
    private static final Recycler<RecyclableStreamList<?>> LIST_RECYCLER = new Recycler<RecyclableStreamList<?>>() {
        @Override
        protected RecyclableStreamList<?> newObject(Handle<RecyclableStreamList<?>> handle) {
            return new RecyclableStreamList<>(handle);
        }
    };
    private static final Recycler<RecyclableStreamListBuilder<?>> BUILDER_RECYCLER = new Recycler<RecyclableStreamListBuilder<?>>() {
        @Override
        protected RecyclableStreamListBuilder<?> newObject(Handle<RecyclableStreamListBuilder<?>> handle) {
            return new RecyclableStreamListBuilder<>(handle);
        }
    };

    @Override
    protected <E> StreamList<E> _newList(int initialCapacity) {
        StreamList<E> list = (StreamList<E>) LIST_RECYCLER.get();
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

    @Override
    protected <E> StreamList<E> _newList(E[] es) {
        StreamList<E> list = newList(es.length);
        list.addAll(es);
        return list;
    }

    @Override
    protected <E> StreamListBuilder<E> _newBuilder() {
        RecyclableStreamListBuilder<E>  builder = (RecyclableStreamListBuilder<E>) BUILDER_RECYCLER.get();
        builder.replaceToNewList();
        return builder;
    }
}