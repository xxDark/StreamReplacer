package me.xdark.streams;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
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

    public static <T> Collection<T> stream(Collection<T> collection) {
        return collection instanceof StreamList ? collection : StreamListProducer.newList(collection);
    }

    public static <T> Collection<T> empty() {
        return StreamListProducer.newList(0);
    }

    public static <T> Collection<T> of(T r) {
        StreamList<T> list = StreamListProducer.newList(1);
        list.add(r);
        return list;
    }

    public static <T> Collection<T> of(Object... rs) {
        return (StreamList) StreamListProducer.newList(rs);
    }

    public static <T> StreamListBuilder<T> builder() {
        return StreamListProducer.newBuilder();
    }

    public static <T> void accept(StreamListBuilder<T> builder, T value) {
        builder.add(value);
    }

    public static <T> StreamListBuilder<T> add(StreamListBuilder<T> builder, T value) {
        builder.add(value);
        return builder;
    }

    public static <T> Collection<T> build(StreamListBuilder<T> builder) {
        return builder.build();
    }

    public static <T, R> Collection<R> map(Collection collection, Function<? super T, ? super R> mapper) {
        StreamList list = (StreamList) collection;
        return list.map(mapper);
    }

    public static <T> Collection<T> filter(Collection<T> collection, Predicate<? super T> filter) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        int j = list.size();
        if (j == 0) {
            return list;
        }
        list.removeIf(filter.negate());
        return collection;
    }

    public static <T> void forEach(Collection<T> collection, Consumer<? super T> consumer) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        list.forEach(consumer);
    }

    public static <T> Collection<T> peek(Collection<T> collection, Consumer<? super T> consumer) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        list.forEach(consumer);
        return list;
    }

    public static Collection<?> skip(Collection<?> collection, long v) {
        StreamList<?> list = (StreamList<?>) collection;
        list.removeRange(0, (int) Math.min(v, list.size()));
        list.checkMap();
        return collection;
    }

    public static Collection<?> limit(Collection<?> collection, long v) {
        StreamList<?> list = (StreamList<?>) collection;
        int size = list.size();
        if (v > size) {
            list.removeRange(size, (int) v);
        }
        list.checkMap();
        return collection;
    }

    public static Collection<?> onClose(Collection<?> collection, Runnable runnable) {
        return ((StreamList<?>) collection).onClose(runnable);
    }

    public static void close(Collection<?> collection) throws Exception {
        ((StreamList<?>) collection).close();
    }

    public static long count(Collection<?> collection) {
        return ((StreamList<?>) collection).size();
    }

    public static <T> Collection<T> sorted(Collection<T> collection) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        list.sort();
        return list;
    }

    public static <T> Collection<T> sorted(Collection<T> collection, Comparator<? super T> comparator) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        list.sort(comparator);
        return list;
    }

    public static <T> Iterator<T> iterator(Collection<T> collection) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        return list.iterator();
    }

    public static <T> Spliterator<T> spliterator(Collection<T> collection) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        return list.spliterator();
    }

    public static <T> Optional<T> findFirst(Collection<T> collection) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.get(0));
    }

    public static <T> Optional<T> findAny(Collection<T> collection) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.get(ThreadLocalRandom.current().nextInt(list.size())));
    }

    public static <T> Collection<T> concat(Collection<T> a, Collection<T> b) {
        StreamList<T> list = (StreamList<T>) a;
        list.checkMap();
        list.addAll(b);
        return a;
    }

    public static <R, T> Collection<R> flatMap(Collection<T> collection, Function<? super T, ? extends Collection<? extends R>> mapper) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        int j = list.size(), k = j;
        for (int i = 0; i < j; i++) {
            Collection<? extends R> s = mapper.apply(list.get(i));
            list.addAll((Collection) s);
            j -= 1;
        }
        list.removeRange(0, k);
        return (Collection<R>) list;
    }

    public static <T> boolean anyMatch(Collection<T> collection, Predicate<? super T> matcher) {
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        int j = list.size();
        if (j == 0) {
            return false;
        }
        for (int i = 0; i < j; i++) {
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
        StreamList<T> list = (StreamList<T>) collection;
        list.checkMap();
        int j = list.size();
        if (j == 0) {
            return false;
        }
        for (int i = 0; i < j; i++) {
            if (!matcher.test(list.get(i++))) {
                return false;
            }
        }
        return true;
    }

    public static Object[] createCompatibleArray(Object[] a) {
        if (a.getClass() != Object[].class) {
            return Arrays.copyOf(a, a.length, Object[].class);
        }
        return a;
    }
}
