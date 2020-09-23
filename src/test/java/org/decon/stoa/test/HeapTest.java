/**
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
package org.decon.stoa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Supplier;

import org.decon.stoa.Stoa;
import org.decon.stoa.test.data.Dummy4;
import org.decon.stoa.util.Heap;
import org.decon.stoa.util.StoaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;

@RunWith(Runner.class)
public class HeapTest {
    @Test
    public void createEmpty() {
        var heap = createHeap();
        assertEquals(heap.longSize(), 0);
    }

    @Property
    public void addOneRemove(final Dummy4 bean) {
        var heap = createHeap();
        heap.push(bean);
        assertEquals(heap.max(), bean);
        assertEquals(1, heap.longSize());
        assertEquals(heap.popMax(), bean);
        assertEquals(0, heap.longSize());
        heap.push(bean);
        assertEquals(heap.max(), bean);
        assertEquals(1, heap.longSize());
        heap.popMax();
        assertEquals(0, heap.longSize());
    }

    @Property
    public void addRandom(final Dummy4[] beans) {
        var expected = new ArrayList<Dummy4>();
        var heap = createHeap();

        for (int i = 0; i < beans.length; i++) {
            Dummy4 bean = beans[i];
            expected.add(bean);
            heap.push(bean);
        }

        assertTrue(Heap.isHeap(heap.getBackingList(), comparator));

        Collections.sort(expected, comparator);

        var actual = new ArrayList<Dummy4>();
        while ((heap.longSize() != 0)) {
            {
                final Dummy4 b = heap.popMax();
                actual.add(b);
            }
        }
        Collections.reverse(actual);
        assertEquals(expected, actual);
    }

    @Property
    public void validateHeapProperty(final Dummy4[] beans) {
        var actual = HeapTest.factory.get();
        actual.addAll(Arrays.asList(beans));

        Heap.wrap(actual, comparator);
        assertTrue(Heap.isHeap(actual, comparator));
    }

    @Property
    public void heapSort(final Dummy4[] beans) {
        var expected = new ArrayList<Dummy4>();
        var actual = HeapTest.factory.get();

        for (int i = 0; i < beans.length; i++) {
            Dummy4 bean = beans[i];
            expected.add(bean);
            actual.add(bean);
        }

        Collections.sort(expected, comparator);
        Heap.heapSort(actual, comparator);
        assertEquals(expected, actual);
    }

    private static final Comparator<Dummy4> comparator = (Dummy4 bean1, Dummy4 bean2) -> bean1.toString()
                                                                                              .compareTo(
                                                                                                      bean2.toString());

    private static final Supplier<Stoa<Dummy4>> factory = StoaBuilder.list(StoaBuilder.beanInfo(Dummy4.class));

    public Heap<Dummy4> createHeap() {
        return Heap.wrap(HeapTest.factory.get(), comparator);
    }
}
