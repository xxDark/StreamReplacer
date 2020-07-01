package me.xdark.streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class is used in order to drop usage of stack emulation during transformations.
 * Since call to {@link java.util.stream.Stream} will pop it's reference from the stack,
 * there is no way to duplicate it before it happens without emulating method execution.
 * <p>
 * For internal use only.
 */
public final class StreamSupport {
    private StreamSupport() { }

    public static <R> Collection<R> empty() {
        return StreamListProducer.newList(0);
    }

    public static <R> Collection<R> of(R r) {
        List<R> list = StreamListProducer.newList(1);
        list.add(r);
        return list;
    }

    public static <R> Collection<R> of(R[] rs) {
        return StreamListProducer.newList(Arrays.asList(rs));
    }

    public static <T, R> Collection<R> map(Collection collection, Function<? super T, ? super R> mapper) {
        if (collection.isEmpty()) {
            return collection;
        }
        ((StreamList) collection).replaceAll(o -> mapper.apply((T) o));
        return collection;
    }

    public static <T> Collection<T> filter(Collection<T> collection, Predicate<? super T> filter) {
        if (collection.isEmpty()) {
            return collection;
        }
        collection.removeIf(t -> !filter.test(t));
        return collection;
    }

    public static <T> Collection<T> peek(Collection<T> collection, Consumer<? super T> consumer) {
        collection.forEach(consumer);
        return collection;
    }

    public static Collection<?> skip(Collection<?> collection, long v) {
        StreamList<?> list = (StreamList<?>) collection;
        list.subList(0, (int) Math.min(v, list.size())).clear();
        return collection;
    }

    public static Collection<?> limit(Collection<?> collection, long v) {
        StreamList<?> list = (StreamList<?>) collection;
        int size = list.size();
        if (v > size) {
            list.subList(size, (int) v).clear();
        }
        return collection;
    }

    public static Collection<?> onClose(Collection<?> collection, Runnable runnable) {
        return ((StreamList<?>) collection).onClose(runnable);
    }

    public static void close(Collection<?> collection) {
        ((StreamList<?>) collection).close();
    }

    public static <T> boolean anyMatch(Collection<T> collection, Predicate<? super T> matcher) {
        if (collection.isEmpty()) {
            return false;
        }
        StreamList<T> list = (StreamList<T>) collection;
        for (int i = 0, j = list.size(); i < j; i++) {
            if (matcher.test(list.get(i++))) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean noneMatch(Collection<T> collection, Predicate<? super T> matcher) {
        return !anyMatch(collection, matcher);
    }

    public static <T> boolean allMatch(Collection<T> collection, Predicate<? super T> matcher) {
        if (collection.isEmpty()) {
            return true;
        }
        StreamList<T> list = (StreamList<T>) collection;
        for (int i = 0, j = list.size(); i < j; i++) {
            if (!matcher.test(list.get(i++))) {
                return false;
            }
        }
        return true;
    }
}
