package me.xdark.streams;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

public final class JavaUnsafe {
    private static final Unsafe UNSAFE;
    private static final MethodHandle ARRAYLIST_ELEMENT_DATA;

    private JavaUnsafe() { }

    public static <T> T allocate(Class<T> type) {
        try {
            return (T) UNSAFE.allocateInstance(type);
        } catch (InstantiationException e) {
            throw new Error(e);
        }
    }

    public static Object[] getElementData(ArrayList<?> list) {
        try {
            return (Object[]) ARRAYLIST_ELEMENT_DATA.invokeExact(list);
        } catch (Throwable t) {
            throw new Error(t);
        }
    }

    static {
        Object maybeThrowable = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Field field = Unsafe.class.getDeclaredField("theUnsafe");
                    field.setAccessible(true);
                    Unsafe unsafe = (Unsafe) field.get(null);
                    MethodHandles.publicLookup();
                    field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                    return new Object[] {unsafe, unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field))};
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    return e;
                }
            }
        });
        if (maybeThrowable instanceof Object[]) {
            Object[] data = (Object[]) maybeThrowable;
            UNSAFE = (Unsafe) data[0];
            try {
                ARRAYLIST_ELEMENT_DATA = ((MethodHandles.Lookup)data[1]).findGetter(ArrayList.class, "elementData", Object[].class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ExceptionInInitializerError(e);
            }
        } else {
            throw new ExceptionInInitializerError((String) maybeThrowable);
        }
    }
}
