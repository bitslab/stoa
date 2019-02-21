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
package org.decon.stoa.test

import com.pholser.junit.quickcheck.Property
import java.util.Collections
import java.util.List
import java.util.function.Supplier
import org.decon.stoa.Stoa
import org.decon.stoa.test.data.Dummy4
import org.decon.stoa.util.Heap
import org.decon.stoa.util.StoaBuilder
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

@RunWith(Runner)
class HeapTest {
	@Test
	def createEmpty() {
		val heap = createHeap()
		assertEquals(heap.longSize, 0)
	}

	@Property
	def void addOneRemove(Dummy4 bean) {
		val heap = createHeap()
		heap.push(bean)
		assertEquals(heap.max(), bean)
		assertEquals(1, heap.longSize())
		assertEquals(heap.popMax(), bean)
		assertEquals(0, heap.longSize())

		heap.push(bean)
		assertEquals(heap.max(), bean)
		assertEquals(1, heap.longSize())
		heap.popMax()
		assertEquals(0, heap.longSize())
	}

	@Property
	def void addRandom(Dummy4[] beans) {
		val expected = <Dummy4>newArrayList
		val heap = createHeap()

		beans.forEach [ b |
			expected.add(b)
			heap.push(b)
		]

		assertTrue(Heap.isHeap(heap.backingList, comparator))

		Collections.sort(expected, heap.comparator)
		val actual = <Dummy4>newArrayList
		while(heap.longSize != 0) {
			val b = heap.popMax
			actual.add(b)
		}
		actual.reverse
		assertEquals(expected, actual)
	}

	@Property
	def void validateHeapProperty(Dummy4[] beans) {
		val l = factory.get()

		beans.forEach [ b |
			l.add(b)
		]

		Heap.wrap(l, comparator)
		assertTrue(Heap.isHeap(l, comparator))
	}

	@Property
	def void heapSort(Dummy4[] beans) {
		val expected = <Dummy4>newArrayList
		val actual = factory.get()

		beans.forEach [ b |
			expected.add(b)
			actual.add(b)
		]

		Collections.sort(expected, comparator)
		Heap.heapSort(actual, comparator);
		assertEquals(expected, actual)
	}

	val comparator = [ Dummy4 bean1, Dummy4 bean2 |
		val List<(Dummy4)=>Comparable> accessors = #[[bool1], [int1], [bool2], [int2], [double1], [double2], [
			str1 ?: ""
		], [
			str2 ?: ""
		]]
		return accessors.fold(0, [ prev, acc |
			if(prev != 0) {
				return prev
			}
			return acc.apply(bean1).compareTo(acc.apply(bean2))
		])
	]

	static val Supplier<Stoa<Dummy4>> factory = StoaBuilder.list(StoaBuilder.beanInfo(Dummy4))

	def createHeap() {
		return Heap.wrap(factory.get(), comparator)
	}
}
