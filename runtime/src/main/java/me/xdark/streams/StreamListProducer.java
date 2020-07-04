package me.xdark.streams;

import java.util.Collection;
import java.util.Objects;

public abstract class StreamListProducer {
    private static StreamListProducer instance;

    protected StreamListProducer() { }

    public static void setProducer(StreamListProducer instance) {
        StreamListProducer.instance = Objects.requireNonNull(instance, "Cannot set StreamListProducer to null!");
    }

    public static <E> StreamList<E> newList(int initialCapacity) {
        return instance._newList(initialCapacity);
    }

    public static <E> StreamList<E> newList(Collection<E> collection) {
        return instance._newList(collection);
    }

    public static <E> StreamList<E> newList(E[] es) {
        return instance._newList(es);
    }

    public static <E> StreamListBuilder<E> newBuilder() {
        return instance._newBuilder();
    }

    protected abstract <E> StreamList<E> _newList(int initialCapacity);

    protected abstract <E> StreamList<E> _newList(Collection<E> collection);

    protected abstract <E> StreamList<E> _newList(E[] es);

    protected abstract <E> StreamListBuilder<E> _newBuilder();

    static {
        setProducer(new StreamListProducer() {
            @Override
            protected <E> StreamList<E> _newList(int initialCapacity) {
                return new StreamList<>(initialCapacity);
            }

            @Override
            protected <E> StreamList<E> _newList(Collection<E> collection) {
                return new StreamList<>(collection);
            }

            @Override
            protected <E> StreamList<E> _newList(E[] es) {
                return new StreamList<>(es);
            }

            @Override
            protected <E> StreamListBuilder<E> _newBuilder() {
                return new StreamListBuilder<>();
            }
        });
    }
}
