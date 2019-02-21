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
import java.util.TreeSet
import org.decon.stoa.util.BitBuffer
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals

@RunWith(Runner)
class BitBufferTest {
	@Property(shrink=false)
	def void transferWord(int seed) {
		val random = new Random(seed)
		val src = new BitBuffer() => [resize(random.nextInt(1000) + 1000)]

		val fillRandom = [ BitBuffer buf |
			for (var int i = 0; i < buf.size / 4; i++) {
				buf.writeBit(random.nextInt(src.size), true)
			}
			return buf
		]
		fillRandom.apply(src)
		val test = [ int wordSize, (BitBuffer, int)=>void transfer |
			val target = new BitBuffer() => [resize(src.size)]
			val orig = fillRandom.apply(target).clone
			val rand = random.nextInt(src.size - wordSize)

			transfer.apply(target, rand)

			orig.removeRange(rand, rand + wordSize)
			target.removeRange(rand, rand + wordSize)
			assertEquals(target, orig)
		]
		val byteTx = [ BitBuffer target, int rand |
			target.writeByte(rand, src.readByte(rand))
			assertEquals(target.readByte(rand), src.readByte(rand))
		]
		val shortTx = [ BitBuffer target, int rand |
			target.writeShort(rand, src.readShort(rand))
			assertEquals(target.readShort(rand), src.readShort(rand))
		]
		val intTx = [ BitBuffer target, int rand |
			target.writeInt(rand, src.readInt(rand))
			assertEquals(target.readInt(rand), src.readInt(rand))
		]
		val longTx = [ BitBuffer target, int rand |
			target.writeLong(rand, src.readLong(rand))
			assertEquals(target.readLong(rand), src.readLong(rand))
		]
		#[8 -> byteTx, 16 -> shortTx, 32 -> intTx, 64 -> longTx].forEach [
			for (var int i = 0; i < src.size / 16; i++) {
				test.apply(key, value)
			}
		]
	}

	@Test
	def void readWriteSingleBit() {
		val bits = new BitBuffer()
		bits.resize(64)
		bits.writeBit(10, true)
		bits.writeBit(63, true)

		#{0 -> 0, 8 -> 4, 9 -> 2, 16 -> 0, 24 -> 0, 32 -> 0, 48 -> 0, 56 -> -128}.forEach [ i, b |
			assertEquals(bits.readByte(i), b)
		]

		assertEquals(0, bits.readByte(0))
		assertEquals(4, bits.readByte(8))
		assertEquals(2, bits.readByte(9))
		assertEquals(0, bits.readByte(16))
		assertEquals(0, bits.readByte(24))
		assertEquals(0, bits.readByte(32))
		assertEquals(0, bits.readByte(48))
		assertEquals(-128, bits.readByte(56))

		bits.resize(128)
		bits.writeBit(67, true)
		bits.writeBit(127, true)
		assertEquals(8, bits.readByte(64))
		assertEquals(2, bits.readByte(66))
		assertEquals(0, bits.readByte(68))
		assertEquals(0, bits.readByte(68))
		assertEquals(-128, bits.readByte(120))

		bits.writeByte(8, 9 as byte)
		assertEquals(9, bits.readByte(8))
		assertEquals(4, bits.readByte(9))
	}

	@Property
	def void readWriteRandom(boolean[] barr) {
		val actual = new BitBuffer() => [addAll(barr)]
		val expected = barr.toList

		assertEquals(expected, actual)
	}

	@Property
	def void insertRandom(boolean[] barr, boolean value, @InRange(min="0") int random) {
		val int index = random % (barr.length + 1)
		val actual = new BitBuffer() => [addAll(barr)]
		val expected = new ArrayList<Boolean>(barr)

		actual.add(index, value)
		expected.add(index, value)

		assertEquals(expected, actual)
	}

	@Property
	def void removeRandom(boolean[] barr, @InRange(min="0") int random) {
		if(barr.size == 0) {
			return
		}
		val int fromIndex = random % barr.length
		val int span = (random % (barr.length - fromIndex))
		val int toIndex = fromIndex + span

		val actual = new BitBuffer() => [addAll(barr)]
		val expected = new ArrayList<Boolean>(barr)

		actual.subList(fromIndex, toIndex).clear()
		expected.subList(fromIndex, toIndex).clear()

		assertEquals(expected, actual)
	}

	@Property
	def void copyRandom(boolean[] barr, int seed) {
		if(barr.size == 0) {
			return
		}
		val random = new Random(seed)
		val int srcPos = random.nextInt(barr.length)
		val int destPos = random.nextInt(barr.length)
		val int length = random.nextInt(barr.length - Math.max(srcPos, destPos))

		val actual = new BitBuffer() => [addAll(barr)]

		actual.copyRange(srcPos, destPos, length)
		System.arraycopy(barr, srcPos, barr, destPos, length)

		assertEquals(barr.toList, actual)
	}

	@Property
	def void nextSetBit(boolean[] barr) {
		val expected = new ArrayList<Boolean>(barr)
		val actual = new BitBuffer() => [addAll(barr)]

		val setBits = expected.indexed.filter[value].map[key].toList.sort
		val next = new TreeSet<Integer>
		for (var int i = 0; i < barr.length; i++) {
			val j = actual.nextSetBit(i)
			if(j != -1) {
				next += j as int
			}
		}
		assertEquals(setBits, next.toList)
	}

	@Property
	def void nextUnsetBit(boolean[] barr) {
		val expected = new ArrayList<Boolean>(barr)
		val actual = new BitBuffer() => [addAll(barr)]

		val unsetBits = expected.indexed.filter[!value].map[key].toList.sort
		val next = new TreeSet<Integer>
		for (var int i = 0; i < barr.length; i++) {
			val j = actual.nextUnsetBit(i)
			if(j != -1) {
				next += j as int
			}
		}
		assertEquals(unsetBits, next.toList)
	}

}
