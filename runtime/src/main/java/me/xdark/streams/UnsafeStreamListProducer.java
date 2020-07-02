package me.xdark.streams;

import java.util.Collection;

public final class UnsafeStreamListProducer extends StreamListProducer {
    @Override
    protected <E> StreamList<E> _newList(int initialCapacity) {
        StreamList<E> list = UnsafeAllocator.allocate(StreamList.class);
        list.setSize(initialCapacity);
        list.setElementData(initialCapacity == 0 ? StreamList.EMPTY_ELEMENTDATA : new Object[initialCapacity]);
        return list;
    }

    @Override
    protected <E> StreamList<E> _newList(Collection<E> collection) {
        StreamList<E> list = UnsafeAllocator.allocate(StreamList.class);
        Object[] a = collection.toArray();
        list.setElementData(StreamSupport.createCompatibleArray(a));
        list.setSize(a.length);
        return list;
    }

    @Override
    protected <E> StreamList<E> _newList(E[] es) {
        StreamList<E> list = UnsafeAllocator.allocate(StreamList.class);
        list.setElementData(StreamSupport.createCompatibleArray(es));
        list.setSize(es.length);
        return list;
    }

    static {
        UnsafeAllocator.allocate(Object.class); // Initialize and test
    }
}
