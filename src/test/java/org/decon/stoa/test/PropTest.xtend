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
import java.util.List
import java.util.Random
import java.util.stream.IntStream
import org.decon.stoa.Stoa
import org.decon.stoa.test.data.Dummy2
import org.decon.stoa.test.data.TestEnum
import org.decon.stoa.util.StoaBuilder
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertThat

@RunWith(Runner)
class PropTest {
	@Property
	def void addAll(Dummy2[] beans) {
		val expected = new ArrayList
		val actual = createList()
		val step = Step.proxy(List, expected, actual)
		val props = beans.map[bean2prop]
		step.addAll(props)

		assertEquals(expected, actual)
	}

	@Property
	def void addRandom(Dummy2[] beans, int seed) {
		val expected = new ArrayList
		val actual = createList()
		val step = Step.proxy(List, expected, actual)
		val random = new Random(seed)
		assertEquals(expected, actual)

		val props = beans.map[bean2prop]
		for (p : props) {
			val int index = random.nextInt(expected.size + 1) // index can be 1 beyond end of list
			step.add(index, p)
		}
		assertEquals(expected, actual)
	}

	@Property
	def void removeRandom(Dummy2[] beans, int seed) {
		val expected = new ArrayList
		val actual = createList()
		val step = Step.proxy(List, expected, actual)
		val random = new Random(seed)
		val props = beans.map[bean2prop]
		step.addAll(props)

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
	def void setRandom(Dummy2[] target, Dummy2[] src, int seed) {
		if(target.empty) {
			return
		}
		val expected = new ArrayList
		val actual = createList()
		val step = Step.proxy(List, expected, actual)
		val random = new Random(seed)
		val targetProps = target.map[bean2prop]
		step.addAll(targetProps)

		val srcProps = src.map[bean2prop]
		for (p : srcProps) {
			val int index = random.nextInt(target.length)
			step.set(index, p)
		}
		assertEquals(expected, actual)
	}

	@Property
	def void nullifyRandom(Dummy2[] beans, int seed) {
		if(beans.empty) {
			return
		}
		val actual = createList()
		val random = new Random(seed)
		val props = beans.map[bean2prop]
		actual.addAll(props)

		val toNullify = random.ints(0, beans.length).limit(random.nextInt(beans.length + 1)).toArray

		for (i : toNullify) {
			actual.nullifyRange(i, i + 1)
		}

		for (i : toNullify) {
			assertNull(actual.get(i).get("str1"))
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
//		val isInline = CoreMatchers.instanceOf(Stoa)
		val l = createList()
		#{
			"str1" -> isObject,
			"bool1" -> isBool,
			"int1" -> isInt,
			"double1" -> isDouble,
			"testEnum1" -> isEnum,
			"byte1" -> isByte,
			"char1" -> isChar,
			"short1" -> isShort,
			"float1" -> isFloat,
			"long1" -> isLong
		}.forEach[prop, matcher|assertThat(l.data(prop), matcher)]

	/*
	 * 		assertThat(l.internalArray("notInline"), isObject)
	 * 		assertThat(l.internalArray("inline"), isInline)
	 * 		assertThat(l.internalArray("derivedInline"), isInline)
	 * 		assertThat(l.internalArray("partialDerivedInline"), isInline)

	 * 		assertThat(l.internalArray("nestedInline"), isInline)
	 * 		val inlineBean3 = l.internalArray("nestedInline") as Stoa<Object>
	 * 		assertThat(inlineBean3.internalArray("inline"), isInline)
	 * 		assertThat(l.internalArray("nestedInline.inline"), isInline)

	 * 		assertThat(l.internalArray("notNestedInline"), isInline)
	 * 		val notNestedBean3 = l.internalArray("notNestedInline") as Stoa<Object>
	 * 		assertThat(notNestedBean3.internalArray("inline"), isObject)

	 * 		val Stoa<Dummy2> l2 = l.internalArray("inline") as Stoa<Dummy2>
	 * 		assertThat(l2.internalArray("str1"), isObject)
	 * 		assertThat(l2.internalArray("bool1"), isBool)
	 * 		assertThat(l2.internalArray("byte1"), isByte)
	 * 		assertThat(l2.internalArray("char1"), isChar)
	 * 		assertThat(l2.internalArray("short1"), isShort)
	 * 		assertThat(l2.internalArray("int1"), isInt)
	 * 		assertThat(l2.internalArray("float1"), isFloat)
	 * 		assertThat(l2.internalArray("double1"), isDouble)
	 * 		assertThat(l2.internalArray("long1"), isLong)
	 */
	}

	@Property
	def void clearSize(Dummy2[] beans) {
		val l = createList()
		val props = beans.map[bean2prop]
		l.addAll(props);
		assertEquals(beans.size, l.size)
		l.clear
		assertEquals(l.size, 0)
	}

	static val factory = StoaBuilder.list(#{
		"str1" -> String,
		"int1" -> int,
		"bool1" -> boolean,
		"double1" -> double,
		"testEnum1" -> TestEnum,
		"byte1" -> byte,
		"char1" -> char,
		"short1" -> short,
		"float1" -> float,
		"long1" -> long
	})

	def static createList() {
		return factory.get()
	}

	def static bean2prop(Dummy2 it) {
		return #{"str1" -> str1, "int1" -> int1, "bool1" -> bool1, "double1" -> double1, "testEnum1" -> testEnum1,
			"byte1" -> byte1, "char1" -> char1, "short1" -> short1, "float1" -> float1, "long1" -> long1}
	}
}
