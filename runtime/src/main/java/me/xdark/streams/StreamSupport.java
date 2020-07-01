package me.xdark.streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.ThreadLocalRandom;
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
@SuppressWarnings("rawtypes")
public final class StreamSupport {
    private StreamSupport() { }

    public static <R> Collection<R> stream(Collection<R> collection) {
        return collection instanceof StreamList ? collection : StreamListProducer.newList(collection);
    }

    public static <R> Collection<R> empty() {
        return StreamListProducer.newList(0);
    }

    public static <R> Collection<R> of(R r) {
        List<R> list = StreamListProducer.newList(1);
        list.add(r);
        return list;
    }

    @SafeVarargs
    public static <R> Collection<R> of(R... rs) {
        return StreamListProducer.newList(Arrays.asList(rs));
    }

    public static <T, R> Collection<R> map(Collection collection, Function<? super T, ? super R> mapper) {
        if (collection.isEmpty()) {
            return collection;
        }

        StreamList list = (StreamList) collection;
        for (int i = 0, j = list.size(); i < j; i++) {
            list.set(i, mapper.apply((T) list.get(i)));
        }
        return list;
    }

    public static <T> Collection<T> filter(Collection<T> collection, Predicate<? super T> filter) {
        if (collection.isEmpty()) {
            return collection;
        }

        StreamList<T> list = (StreamList<T>) collection;
        for (int i = 0, j = list.size(); i < j; i++) {
            if (filter.test(list.get(i))) {
                list.remove(i--);
                j -= 1;
            }
        }
        return collection;
    }

    public static <T> Collection<T> peek(Collection<T> collection, Consumer<? super T> consumer) {
        StreamList<T> list = (StreamList<T>) collection;
        for (int i= 0, j = list.size(); i < j; consumer.accept(list.get(i++)));
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

    public static void close(Collection<?> collection) throws Exception {
        ((StreamList<?>) collection).close();
    }

    public static long count(Collection<?> collection) {
        return collection.size();
    }

    public static <T> Iterator<T> iterator(Collection<T> collection) {
        return collection.iterator();
    }

    public static <T> Spliterator<T> spliterator(Collection<T> collection) {
        return collection.spliterator();
    }

    public static <T> Optional<T> findFirst(Collection<T> collection) {
        StreamList<T> list = (StreamList<T>) collection;
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.get(0));
    }

    public static <T> Optional<T> findAny(Collection<T> collection) {
        StreamList<T> list = (StreamList<T>) collection;
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.get(ThreadLocalRandom.current().nextInt(list.size())));
    }

    public static <T> Collection<T> concat(Collection<T> a, Collection<T> b) {
        a.addAll(b);
        return a;
    }

    public static <R, T> Collection<R> flatMap(Collection<T> collection, Function<? super T, ? extends Collection<? extends R>> mapper) {
        StreamList<T> list = (StreamList<T>) collection;
        for (int i = 0, j = list.size(); i < j; i++) {
            Collection<? extends R> s = mapper.apply(list.get(i));
            list.remove(i--);
            list.addAll((Collection) s);
            j -= 1;
        }
        return (Collection<R>) list;
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
