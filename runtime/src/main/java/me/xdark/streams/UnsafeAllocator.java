package me.xdark.streams;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class UnsafeAllocator {
    private static final Unsafe UNSAFE;

    private UnsafeAllocator() { }

    public static <T> T allocate(Class<T> type) {
        try {
            return (T) UNSAFE.allocateInstance(type);
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
            throw new ExceptionInInitializerError((Throwable) maybeThrowable);
        }
    }
}
