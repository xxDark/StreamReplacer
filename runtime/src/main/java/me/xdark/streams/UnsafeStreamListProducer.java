package me.xdark.streams;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;

public final class UnsafeStreamListProducer extends StreamListProducer {
    private static final Unsafe UNSAFE;

    @Override
    protected <E> StreamList<E> _newList(int initialCapacity) {
        StreamList<E> list = allocate();
        list.setSize(initialCapacity);
        list.setElementData(initialCapacity == 0 ? StreamList.EMPTY_ELEMENTDATA : new Object[initialCapacity]);
        return list;
    }

    @Override
    protected <E> StreamList<E> _newList(Collection<E> collection) {
        StreamList<E> list = allocate();
        Object[] a = collection.toArray();
        if (a.getClass() != Object[].class) {
            a = Arrays.copyOf(a, a.length, Object[].class);
        }
        list.setElementData(a);
        list.setSize(a.length);
        return list;
    }

    @Override
    protected <E> StreamList<E> _newList(E[] es) {
        StreamList<E> list = allocate();
        Object[] a = es;
        if (a.getClass() != Object[].class) {
            a = Arrays.copyOf(a, a.length, Object[].class);
        }
        list.setElementData(a);
        list.setSize(es.length);
        return list;
    }

    private static <E> StreamList<E> allocate() {
        try {
            return (StreamList<E>) UNSAFE.allocateInstance(StreamList.class);
        } catch (InstantiationException e) {
            throw new Error(e);
        }
    }

    static {
        Object maybeThrowable = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Field field = Unsafe.class.getDeclaredField("theUnsafe");
                    field.setAccessible(true);
                    return field.get(null);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    return e;
                }
            }
        });
        if (maybeThrowable instanceof Unsafe) {
            UNSAFE = (Unsafe) maybeThrowable;
        } else {
            throw new ExceptionInInitializerError((String) maybeThrowable);
        }
    }
}
