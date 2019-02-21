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
import com.pholser.junit.quickcheck.generator.InRange
import java.util.ArrayList
import java.util.Random
import org.decon.stoa.test.data.TestEnum
import org.decon.stoa.util.EnumBuffer
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals

@RunWith(Runner)
class EnumBufferTest {
	@Test
	def void readWriteSingle() {
		val eb = createList
		eb.resize(1)

		eb.set(0, TestEnum.enum4)
		assertEquals(eb.readEnum(0), TestEnum.enum4)
	}

	@Property
	def void readWriteRandom(TestEnum[] arr) {
		val actual = createList() => [addAll(arr)]
		val expected = arr.toList

		assertEquals(expected, actual)
	}

	@Property
	def void insertRandom(TestEnum[] arr, TestEnum value, @InRange(min="0") int random) {
		val int index = random % (arr.length + 1)
		val actual = createList() => [addAll(arr)]
		val expected = new ArrayList<TestEnum>(arr)
		actual.ensureCapacity(arr.length + 1)

		actual.add(index, value)
		expected.add(index, value)

		assertEquals(expected, actual)
	}

	@Property
	def void removeRandom(TestEnum[] arr, @InRange(min="0") int random) {
		if(arr.size == 0) {
			return
		}
		val int fromIndex = random % arr.length
		val int span = (random % (arr.length - fromIndex))
		val int toIndex = fromIndex + span
		val actual = createList() => [addAll(arr)]
		val expected = new ArrayList<TestEnum>(arr)

		actual.subList(fromIndex, toIndex).clear()
		expected.subList(fromIndex, toIndex).clear()

		assertEquals(expected, actual)
	}

	@Property
	def void copyRandom(TestEnum[] arr, int seed) {
		if(arr.size == 0) {
			return
		}
		val random = new Random(seed)
		val int srcPos = random.nextInt(arr.length)
		val int destPos = random.nextInt(arr.length)
		val int length = random.nextInt(arr.length - Math.max(srcPos, destPos))

		val actual = createList()  => [addAll(arr)]

		actual.copyRange(srcPos, destPos, length)
		System.arraycopy(arr, srcPos, arr, destPos, length)

		assertEquals(arr.toList, actual)
	}

	static val constants = TestEnum.enumConstants

	def static createList() {
		return new EnumBuffer(constants)
	}
}
