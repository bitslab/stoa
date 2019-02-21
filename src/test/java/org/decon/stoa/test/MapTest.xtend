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
import java.util.ArrayList
import java.util.Map
import java.util.Random
import java.util.function.Supplier
import org.decon.stoa.StoaMap
import org.decon.stoa.test.data.Dummy2
import org.decon.stoa.test.data.Dummy4
import org.decon.stoa.util.StoaBuilder
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals

@RunWith(Runner)
class MapTest {
	@Property
	def void addRandomValue(Dummy4[] keys, Dummy2[] values, int seed) {
		if(keys.length == 0 || values.length == 0) {
			return
		}
		val expected = <Dummy4, Dummy2>newHashMap()
		val actual = createMap()
		val step = Step.proxy(Map, expected, actual)
		val random = new Random(seed)
		assertEquals(expected, actual)

		keys.forEach [ key |
			val value = values.get(random.nextInt(values.length))
			step.put(key, value)
		]
		assertEquals(expected, actual)
		assertEquals(expected.size, actual.size)
		actual.clear()
		assertNotEquals(expected, actual)
		assertNotEquals(expected.size, actual.size)
		expected.clear()
		assertEquals(expected, actual)
		assertEquals(expected.size, actual.size)
	}

	@Property
	def void addRandomKey(Dummy4[] keys, Dummy2[] values, int seed) {
		if(keys.length == 0 || values.length == 0) {
			return
		}
		val expected = <Dummy4, Dummy2>newHashMap()
		val actual = createMap()
		val step = Step.proxy(Map, expected, actual)
		val random = new Random(seed)
		assertEquals(expected, actual)

		values.forEach [ value |
			val key = keys.get(random.nextInt(keys.length))
			step.put(key, value)
		]
		assertEquals(expected, actual)
		assertEquals(expected.size, actual.size)
		actual.clear()
		assertNotEquals(expected, actual)
		assertNotEquals(expected.size, actual.size)
		expected.clear()
		assertEquals(expected, actual)
		assertEquals(expected.size, actual.size)
	}

	@Property
	def void overwriteRandomValue(Dummy4[] keys, Dummy2[] values, int seed) {
		if(keys.length == 0 || values.length == 0) {
			return
		}
		val expected = <Dummy4, Dummy2>newHashMap()
		val actual = createMap()
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
		assertEquals(expected, actual)
		assertEquals(expected.size, actual.size)
		actual.clear()
		assertNotEquals(expected, actual)
		assertNotEquals(expected.size, actual.size)
		expected.clear()
		assertEquals(expected, actual)
		assertEquals(expected.size, actual.size)
	}

	@Property
	def void removeRandom(Dummy4[] keys, Dummy2[] values, int seed) {
		if(keys.length == 0 || values.length == 0) {
			return
		}
		val expected = <Dummy4, Dummy2>newHashMap()
		val actual = createMap()
		val step = Step.proxy(Map, expected, actual)
		val random = new Random(seed)

		keys.forEach [ key |
			val value = values.get(random.nextInt(values.length))
			step.put(key, value)
		]

		(1 .. keys.size / 2).forEach [
			val key = keys.get(random.nextInt(keys.length))
			step.remove(key)
		]
		assertEquals(expected, actual)
		assertEquals(expected.size, actual.size)
	}

	@Test(expected=ClassCastException)
	def void addWrongClassKey() {
		val map = createMap() as Object as StoaMap<Object, Object>
		val wrongClass = new ArrayList
		val value = new Dummy4
		map.put(wrongClass, value)
	}

	@Test(expected=ClassCastException)
	def void addWrongClassValue() {
		val map = createMap() as Object as StoaMap<Object, Object>
		val wrongClass = new ArrayList
		val key = new Dummy2
		map.put(key, wrongClass)
	}

	static val Supplier<StoaMap<Dummy4, Dummy2>> factory = StoaBuilder.map(
		StoaBuilder.list(StoaBuilder.beanInfo(Dummy4)), StoaBuilder.list(StoaBuilder.beanInfo(Dummy2)))

	def static StoaMap<Dummy4, Dummy2> createMap() {
		return factory.get()
	}

}
