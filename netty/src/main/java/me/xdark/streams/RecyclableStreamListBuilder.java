package me.xdark.streams;

import io.netty.util.Recycler;

public final class RecyclableStreamListBuilder<E> extends StreamListBuilder<E> {
    private static final StreamList<?> UNSET = new StreamList<>(0);

    private final Recycler.Handle<RecyclableStreamListBuilder<?>> handle;

    public RecyclableStreamListBuilder(Recycler.Handle<RecyclableStreamListBuilder<?>> handle) {
        super(StreamListBuilder.INITIAL_CAPACITY);
        this.handle = handle;
    }

    void replaceToNewList() {
        if (list == UNSET) {
            list = StreamListProducer.newList(StreamListBuilder.INITIAL_CAPACITY);
        }
    }

    @Override
    public StreamList<E> build() {
        StreamList<E> built = super.build();
        list = (StreamList<E>) UNSET;
        handle.recycle(this);
        return built;
    }
}
