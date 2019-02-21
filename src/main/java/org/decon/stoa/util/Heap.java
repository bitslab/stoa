/*
 * Copyright (C) 2016 Adarsh Soodan
 * 
 * Stoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3.0 as 
 * published by the Free Software Foundation.
 *
 * Stoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3.0 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License version 3.0
 * along with Stoa.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.decon.stoa.util;

import java.util.Comparator;
import java.util.NoSuchElementException;

import org.decon.stoa.HugeList;

public class Heap<E> {
    private final Comparator<? super E> comparator;
    private final HugeList<E>           list;

    @SuppressWarnings("unchecked")
    public static <A> void heapSort(final HugeList<A> l) {
        heapSort(l, (A first, A second) -> ((Comparable<A>) first).compareTo(second));
    }

    @SuppressWarnings("unchecked")
    public static <A> boolean isHeap(final HugeList<A> l) {
        return isHeap(l, (A first, A second) -> ((Comparable<A>) first).compareTo(second));
    }

    @SuppressWarnings("unchecked")
    public static <E> Heap<E> wrap(HugeList<E> list) {
        return wrap(list, (E first, E second) -> ((Comparable<E>) first).compareTo(second), false);
    }

    public static <A> void heapSort(final HugeList<A> l, final Comparator<? super A> comparator) {
        final Heap<A> heap = Heap.wrap(l, comparator);
        final long size = l.longSize();
        for (long i = size - 1; i > 0; i--) {
            A max = l.read(0);
            A last = l.read(i);
            l.write(0, last);
            l.write(i, max);
            heap.heapifyDown(0, last, i);
        }
    }

    public static <A> boolean isHeap(final HugeList<A> l, final Comparator<A> comparator) {
        boolean ret = true;
        for (long i = l.longSize() - 1; i > 0; i--) {
            ret = ret && (comparator.compare(l.read(heapParent(i)), l.read(i)) >= 0);
        }
        return ret;
    }

    public static <E> Heap<E> wrap(HugeList<E> list, Comparator<? super E> comparator) {
        return wrap(list, comparator, false);
    }

    public static <E> Heap<E> wrap(HugeList<E> list, Comparator<? super E> comparator, boolean listIsHeaped) {
        Heap<E> ret = new Heap<>(list, comparator);
        if (!listIsHeaped) {
            for (long i = 1; i < list.longSize(); i++) {
                ret.heapifyUp(i);
            }
        }
        return ret;
    }

    public Heap(Heap<E> other) {
        this.comparator = other.comparator;
        this.list = other.list.clone();
    }

    protected Heap(HugeList<E> list, Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.list = list;
    }

    public long longSize() {
        return list.longSize();
    }

    public boolean push(E bean) {
        list.insert(list.longSize(), bean);
        heapifyUp(list.longSize() - 1);
        return true;
    }

    public E max() {
        if (list.longSize() == 0) {
            throw new NoSuchElementException();
        }
        return list.read(0);
    }

    public E popMax() {
        E ret = max();
        if (list.longSize() == 1) {
            list.resize(0);
            return ret;
        }
        final E replace = list.read(list.longSize() - 1); // Last element to replace 0 index
        list.write(0, replace);
        list.resize(list.longSize() - 1);
        heapifyDown(0, replace, list.longSize());
        return ret;
    }

    public HugeList<E> getBackingList() {
        return list;
    }

    public Comparator<? super E> getComparator() {
        return comparator;
    }

    private void heapifyUp(final long n) {
        E childBean = list.read(n);
        long child = n;
        while (child != 0) {
            long parent = heapParent(child);
            E parentBean = list.read(parent);
            if (comparator.compare(parentBean, childBean) < 0) {
                list.write(parent, childBean);
                list.write(child, parentBean);
                child = parent;
            } else {
                child = 0;
            }
        }
    }

    private long heapifyTriple(long parent, E parentBean, final long size) {
        long left = heapLeft(parent);
        long right = heapRight(parent);
        if (left < size) {
            final long maxChild;
            final E maxChildBean;
            E leftChild = list.read(left);
            if (right < size) {
                E rightChild = list.read(right);
                if (0 < comparator.compare(leftChild, rightChild)) {
                    maxChild = left;
                    maxChildBean = leftChild;
                } else {
                    maxChild = right;
                    maxChildBean = rightChild;
                }
            } else {
                maxChild = left;
                maxChildBean = leftChild;
            }
            if (comparator.compare(parentBean, maxChildBean) < 0) {
                list.write(parent, maxChildBean);
                list.write(maxChild, parentBean);
            }
            return maxChild;
        }
        return size;
    }

    private void heapifyDown(final long n, final E nBean, final long size) {
        long parent = n;
        E parentBean = nBean;
        while (parent < size) {
            parent = heapifyTriple(parent, parentBean, size);
        }
    }

    private static long heapParent(long n) {
        return (n - 1) / 2;
    }

    private static long heapLeft(long n) {
        return (n * 2) + 1;
    }

    private static long heapRight(long n) {
        return (n * 2) + 2;
    }

}
