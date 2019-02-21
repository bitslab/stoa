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
import java.util.Random
import java.util.Set
import java.util.function.Supplier
import org.decon.stoa.test.data.Dummy4
import org.decon.stoa.util.SetAdapter
import org.decon.stoa.util.StoaBuilder
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals

@RunWith(Runner)
class SetTest {
	@Property
	def void addRandom(Dummy4[] elements, int seed) {
		val expected = <Dummy4>newHashSet()
		val actual = createSet()
		val step = Step.proxy(Set, expected, actual)
		val random = new Random(seed)
		assertEquals(expected, actual)

		(0 ..< elements.length / 2).forEach [
			val index = random.nextInt(elements.length)
			step.add(elements.get(index))
		]
		assertEquals(expected, actual)
		step.clear()
		assertEquals(expected, actual)
	}

	@Property
	def void removeRandom(Dummy4[] elements, int seed) {
		val expected = <Dummy4>newHashSet()
		val actual = createSet()
		val step = Step.proxy(Set, expected, actual)
		val random = new Random(seed)

		elements.forEach [ element |
			step.add(element)
		]

		(0 ..< elements.length / 2).forEach [
			val element = elements.get(random.nextInt(elements.length))
			step.remove(element)
		]
		assertEquals(expected, actual)
	}

	@Test(expected=ClassCastException)
	def void addWrongClass() {
		val map = createSet() as Object as SetAdapter<Object>
		val wrongClass = new ArrayList
		map.add(wrongClass)
	}

	static val Supplier<SetAdapter<Dummy4>> factory = StoaBuilder.set(StoaBuilder.list(StoaBuilder.beanInfo(Dummy4)))

	def static createSet() {
		return factory.get()
	}

}
