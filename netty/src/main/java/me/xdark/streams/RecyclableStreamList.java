package me.xdark.streams;

import io.netty.util.Recycler;

public final class RecyclableStreamList<E> extends StreamList<E> {
    private final Recycler.Handle<RecyclableStreamList<?>> handle;

    public RecyclableStreamList(Recycler.Handle<RecyclableStreamList<?>> handle) {
        super(0);
        this.handle = handle;
    }

    @Override
    public void close() {
        super.close();
        clear();
        handle.recycle(this);
    }
}
