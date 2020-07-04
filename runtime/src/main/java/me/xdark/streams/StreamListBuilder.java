package me.xdark.streams;

public class StreamListBuilder<E> {
    public static final int INITIAL_CAPACITY = 4;
    protected StreamList<E> list;

    public StreamListBuilder(StreamList<E> list) {
        this.list = list;
    }

    public StreamListBuilder(int initialCapacity) {
        this(StreamListProducer.newList(initialCapacity));
    }

    public StreamListBuilder() {
        this(StreamListProducer.newList(INITIAL_CAPACITY));
    }

    public void add(E element) {
        list.add(element);
    }

    public StreamList<E> build() {
        StreamList<E> list = this.list;
        if (list == null) {
            throw new IllegalStateException("Already built!");
        }
        this.list = null;
        return list;
    }
}
