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

    protected abstract <E> StreamList<E> _newList(int initialCapacity);

    protected abstract <E> StreamList<E> _newList(Collection<E> collection);

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
        });
    }
}
