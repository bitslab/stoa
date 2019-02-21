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
import java.util.Map
import java.util.Random
import org.decon.stoa.test.data.Dummy2
import org.decon.stoa.test.data.Dummy4
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals

@RunWith(Runner)
class MapIterTest {

	@Property
	def void addRandom(Dummy4[] keys, Dummy2[] values, int seed) {
		if(keys.length == 0 || values.length == 0) {
			return
		}
		val expected = <Dummy4, Dummy2>newHashMap()
		val actual = MapTest.createMap()
		val step = Step.proxy(Map, expected, actual)
		val random = new Random(seed)
		assertEquals(expected, actual)

		keys.forEach [ key |
			val value = values.get(random.nextInt(values.length))
			step.put(key, value)
		]
		assertEquals(expected.entrySet, actual.entrySet)
		assertEquals(expected.entrySet.size, actual.entrySet.size)
		actual.entrySet.clear()
		assertNotEquals(expected.entrySet, actual.entrySet)
		assertNotEquals(expected.entrySet.size, actual.entrySet.size)
		expected.clear()
		assertEquals(expected.entrySet, actual.entrySet)
		assertEquals(expected.entrySet.size, actual.entrySet.size)
	}

	@Property
	def void overwriteRandom(Dummy4[] keys, Dummy2[] values, int seed) {
		if(keys.length == 0 || values.length == 0) {
			return
		}
		val expected = <Dummy4, Dummy2>newHashMap()
		val actual = MapTest.createMap()
		val step = Step.proxy(Map, expected, actual)
		val random = new Random(seed)

		keys.forEach [ key |
			val value = values.get(random.nextInt(values.length))
			step.put(key, value)
		]
		keys.forEach [ key |
			val value = values.get(random.nextInt(values.length))
			step.put(key, value)
		]
		assertEquals(expected.entrySet, actual.entrySet)
		assertEquals(expected.entrySet.size, actual.entrySet.size)
		actual.entrySet.clear()
		assertNotEquals(expected.entrySet, actual.entrySet)
		assertNotEquals(expected.entrySet.size, actual.entrySet.size)
		expected.clear()
		assertEquals(expected.entrySet, actual.entrySet)
		assertEquals(expected.entrySet.size, actual.entrySet.size)
	}

	@Property
	def void removeRandom(Dummy4[] keys, Dummy2[] values, int seed) {
		if(keys.length == 0 || values.length == 0) {
			return
		}
		val expected = <Dummy4, Dummy2>newHashMap()
		val actual = MapTest.createMap()
		val step = Step.proxy(Map, expected, actual)
		val random = new Random(seed)

		keys.forEach [ key |
			val value = values.get(random.nextInt(values.length))
			step.put(key, value)
		]

		val iter = actual.entrySet.iterator
		while(iter.hasNext) {
			val entry = iter.next
			if(random.nextInt(2) == 0) {
				expected.remove(entry.key)
				iter.remove
			}
		}

		assertEquals(expected.entrySet, actual.entrySet)
		assertEquals(expected.entrySet.size, actual.entrySet.size)
	}

}
