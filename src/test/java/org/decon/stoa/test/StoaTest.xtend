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
import java.beans.Introspector
import java.util.ArrayList
import java.util.List
import java.util.Random
import java.util.function.Supplier
import java.util.stream.IntStream
import org.decon.stoa.Stoa
import org.decon.stoa.test.data.Dummy1
import org.decon.stoa.test.data.Dummy2
import org.decon.stoa.test.data.Dummy22
import org.decon.stoa.test.data.Dummy3
import org.decon.stoa.test.data.Dummy4
import org.decon.stoa.util.StoaBuilder
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail

@RunWith(Runner)
class StoaTest {
	@Property
	def void addAll(Dummy1[] beans) {
		val expected = new ArrayList
		val actual = createList()
		val step = Step.proxy(List, expected, actual)

		step.addAll(beans)

		assertEquals(expected, actual)
	}

	@Property
	def void addRandom(Dummy1[] beans, int seed) {
		val expected = new ArrayList
		val actual = createList()
		val step = Step.proxy(List, expected, actual)
		val random = new Random(seed)

		for (b : beans) {
			val int index = random.nextInt(expected.size + 1) // index can be 1 beyond end of list
			step.add(index, b)
		}
		assertEquals(expected, actual)
	}

	@Property
	def void removeRandom(Dummy1[] beans, int seed) {
		val expected = new ArrayList
		val actual = createList()
		val step = Step.proxy(List, expected, actual)
		val random = new Random(seed)
		step.addAll(beans)

		val toRemove = IntStream.range(0, random.nextInt(beans.length + 1)).map[random.nextInt(beans.length - it)].
			toArray
		for (i : toRemove) {
			step.remove(i)
		}

		assertEquals(expected, actual)
	}

	@Test(expected=ClassCastException)
	def void addWrongClass() {
		val List<String> l = createList() as Object as Stoa<String>
		l.add("wrongClass")
	}

	@Property
	def void setRandom(Dummy1[] target, Dummy1[] src, int seed) {
		if(target.empty) {
			return
		}
		val expected = new ArrayList
		val actual = createList()
		val step = Step.proxy(List, expected, actual)
		val random = new Random(seed)
		step.addAll(target)

		for (b : src) {
			val int index = random.nextInt(target.length)
			step.set(index, b)
		}
		assertEquals(expected, actual)
	}

	@Property
	def void nullifyRandom(Dummy1[] beans, int seed) {
		if(beans.empty) {
			return
		}
		val actual = createList()
		val random = new Random(seed)
		actual.addAll(beans)

		val toNullify = random.ints(0, beans.length).limit(random.nextInt(beans.length + 1)).toArray

		for (i : toNullify) {
			actual.nullifyRange(i, i + 1)
		}

		for (i : toNullify) {
			actual.get(i) => [
				assertNull(str1)
				assertNull(notInline)
				assertNull(inline.str1)
				assertNull(inline.str2)
				assertNull(derivedInline.str1)
				assertNull(derivedInline.str2)
				assertNull(nestedInline.str1)
				assertNull(nestedInline.str2)
				assertNull(nestedInline.inline.str1)
				assertNull(nestedInline.inline.str2)
				assertNull(notNestedInline.str1)
				assertNull(notNestedInline.str2)
				assertNull(notNestedInline.inline)
			]
		}
	}

	@Property
	def void exceedSizeBoundAdd(Dummy1 bean1, Dummy1 bean2, Dummy1 bean3) {
		val l = createList()
		l.sizeBound = 2

		l.add(bean1)
		l.add(bean2)
		try {
			l.add(bean3)
			fail('''sizeBound = «l.sizeBound», added elements = «l.size»''')
		} catch(IllegalArgumentException e) {
			assertTrue(e.message.indexOf("sizeBound") != -1);
		}
	}

	@Property
	def void exceedSizeBoundSet(Dummy1 bean1, Dummy1 bean2, Dummy1 bean3) {
		val l = createList()

		l.add(bean1)
		l.add(bean2)
		l.add(bean3)
		try {
			l.sizeBound = 2
			fail('''sizeBound = «l.sizeBound», size = «l.size»''')
		} catch(IllegalArgumentException e) {
			assertTrue(e.message.indexOf("sizeBound") != -1);
		}
	}

	@Test
	def void sizeBoundEnsureCapacity() {
		val l = createList()

		l.sizeBound = 10
		l.ensureCapacity(10)
		assertTrue("10 <= l.capacity", 10 <= l.capacity)
		try {
			l.ensureCapacity(11)
			fail("Expected IndexOutOfBoundsException")
		} catch(IndexOutOfBoundsException e) {
			assertTrue(e.message.indexOf("sizeBound") != -1);
		}
	}

	@Test
	def void checkInlineAndInternalArray() {
		val isBool = CoreMatchers.instanceOf(typeof(long[]))
		val isByte = CoreMatchers.instanceOf(typeof(byte[]))
		val isChar = CoreMatchers.instanceOf(typeof(char[]))
		val isShort = CoreMatchers.instanceOf(typeof(short[]))
		val isInt = CoreMatchers.instanceOf(typeof(int[]))
		val isFloat = CoreMatchers.instanceOf(typeof(float[]))
		val isDouble = CoreMatchers.instanceOf(typeof(double[]))
		val isLong = CoreMatchers.instanceOf(typeof(long[]))
		val isEnum = CoreMatchers.instanceOf(typeof(long[]))
		val isObject = CoreMatchers.instanceOf(typeof(Object[]))
		val isInline = CoreMatchers.instanceOf(Stoa)

		val l = createList()
		#{
			"str1" -> isObject,
			"bool1" -> isBool,
			"int1" -> isInt,
			"double1" -> isDouble,
			"testEnum" -> isEnum,
			"notInline" -> isObject,
			"inline" -> isInline,
			"derivedInline" -> isInline,
			"partialDerivedInline" -> isInline,
			"nestedInline" -> isInline,
			"nestedInline.inline" -> isInline,
			"notNestedInline" -> isInline
		}.forEach[prop, matcher|assertThat(l.data(prop), matcher)]
		assertThat(l.data("nestedInline"), isInline)

		val inlineBean3 = l.data("nestedInline") as Stoa<Object>
		assertThat(inlineBean3.data("inline"), isInline)

		val notNestedBean3 = l.data("notNestedInline") as Stoa<Object>
		assertThat(notNestedBean3.data("inline"), isObject)

		val Stoa<Dummy2> l2 = l.data("inline") as Stoa<Dummy2>
		#{
			"str1" -> isObject,
			"bool1" -> isBool,
			"byte1" -> isByte,
			"char1" -> isChar,
			"short1" -> isShort,
			"int1" -> isInt,
			"float1" -> isFloat,
			"double1" -> isDouble,
			"long1" -> isLong,
			"testEnum1" -> isEnum,
			"str2" -> isObject,
			"bool2" -> isBool,
			"double2" -> isDouble,
			"int2" -> isInt,
			"testEnum2" -> isEnum
		}.forEach[prop, matcher|assertThat(l2.data(prop), matcher)]
	}

	@Property
	def void clearSize(Dummy1[] beans) {
		val l = createList()
		l.addAll(beans);
		assertEquals(beans.size, l.size)
		l.clear
		assertEquals(l.size, 0)
	}

	static val dummy1 = StoaBuilder.beanInfo(Dummy1)
	static val dummy2 = StoaBuilder.beanInfo(Dummy2)
	static val dummy22 = StoaBuilder.beanInfo(Dummy22)
	static val partialDummy22 = Introspector.getBeanInfo(Dummy22, Dummy22.superclass)
	static val dummy3 = StoaBuilder.beanInfo(Dummy3)
	static val dummy4 = StoaBuilder.beanInfo(Dummy4)
	static val inlineBeans = #{"inline" -> dummy2, "derivedInline" -> dummy22, "partialDerivedInline" -> partialDummy22,
		"nestedInline" -> dummy3, "nestedInline.inline" -> dummy4, "notNestedInline" -> dummy3}
	static val Supplier<Stoa<Dummy1>> factory = StoaBuilder.list(dummy1, inlineBeans)

	def static Stoa<Dummy1> createList() {
		return factory.get()
	}

}
